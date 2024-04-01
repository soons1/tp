package scrolls.elder.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static scrolls.elder.logic.commands.CommandTestUtil.assertCommandSuccess;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import scrolls.elder.logic.commands.exceptions.CommandException;
import scrolls.elder.model.Datastore;
import scrolls.elder.model.LogStore;
import scrolls.elder.model.Model;
import scrolls.elder.model.ModelManager;
import scrolls.elder.model.PersonStore;
import scrolls.elder.model.UserPrefs;
import scrolls.elder.model.log.Log;
import scrolls.elder.model.person.Person;
import scrolls.elder.testutil.Assert;
import scrolls.elder.testutil.PersonBuilder;
import scrolls.elder.testutil.TypicalDatastore;
import scrolls.elder.testutil.TypicalIndexes;

class LogAddCommandTest {

    private Model model;
    private PersonStore personStore;
    private Model expectedModel;
    private PersonStore expectedPersonStore;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(TypicalDatastore.getTypicalDatastore(), new UserPrefs());
        personStore = model.getMutableDatastore().getMutablePersonStore();
        expectedModel = new ModelManager(new Datastore(model.getDatastore()), new UserPrefs());
        expectedPersonStore = expectedModel.getMutableDatastore().getMutablePersonStore();
    }

    @Test
    void execute_validLogAddCommand_success() {
        Person befriendee =
                personStore.getFilteredBefriendeeList().get(TypicalIndexes.INDEX_FIRST_PERSON.getZeroBased());
        Person volunteer =
                personStore.getFilteredVolunteerList().get(TypicalIndexes.INDEX_FIRST_PERSON.getZeroBased());
        LogAddCommand logAddCommand = new LogAddCommand("test1", TypicalIndexes.INDEX_FIRST_PERSON,
                TypicalIndexes.INDEX_FIRST_PERSON,
                1,
                new Date(2023, 1, 1),
                "was a good session");

        String expectedMessage = LogAddCommand.MESSAGE_SUCCESS;
        Person afterLoggingBefriendee = new PersonBuilder(befriendee)
                .withTimeServed(1).build();
        Person afterLoggingVolunteer = new PersonBuilder(volunteer)
                .withTimeServed(1).build();

        PersonStore personStore = expectedModel.getMutableDatastore().getMutablePersonStore();
        LogStore logStore = expectedModel.getMutableDatastore().getMutableLogStore();
        Log toAdd =
                new Log(model.getDatastore(), "test1", volunteer.getPersonId(), befriendee.getPersonId(),
                        1, new Date(2023, 1, 1), "was a good session");
        logStore.addLog(toAdd);
        personStore.setPerson(befriendee, afterLoggingBefriendee);
        personStore.setPerson(volunteer, afterLoggingVolunteer);
        expectedModel.commitDatastore();

        assertCommandSuccess(logAddCommand, model, expectedMessage, expectedModel);
    }

    @Test
    void execute_negativeDuration_throwsCommandException() {
        LogAddCommand logAddCommand = new LogAddCommand("test2", TypicalIndexes.INDEX_FIRST_PERSON,
                TypicalIndexes.INDEX_FIRST_PERSON,
                -1,
                new Date(2023, 1, 1),
                "was a good session");
        Assert.assertThrows(
                CommandException.class, LogAddCommand.MESSAGE_NEGATIVE_DURATION, () -> logAddCommand.execute(model));
    }

    @Test
    void execute_personsNotPaired_throwsCommandException() {
        LogAddCommand logAddCommand = new LogAddCommand("test3", TypicalIndexes.INDEX_FIRST_PERSON,
                TypicalIndexes.INDEX_SECOND_PERSON,
                1,
                new Date(2023, 1, 1),
                "was a good session");
        Assert.assertThrows(
                CommandException.class, LogAddCommand.MESSAGE_PERSONS_NOT_PAIRED, () -> logAddCommand.execute(model));
    }

    @Test
    void testEquals() {
        LogAddCommand logAddCommand1 = new LogAddCommand("test4-1", TypicalIndexes.INDEX_FIRST_PERSON,
                TypicalIndexes.INDEX_FIRST_PERSON,
                1,
                new Date(2023, 1, 1),
                "was a good session");
        LogAddCommand logAddCommand2 = new LogAddCommand("test4-2", TypicalIndexes.INDEX_FIRST_PERSON,
                TypicalIndexes.INDEX_SECOND_PERSON,
                1,
                new Date(2023, 1, 1),
                "was a good session");

        // same object -> returns true
        assertEquals(logAddCommand1, logAddCommand1);

        // same values -> returns true
        LogAddCommand logAddCommand1Copy = new LogAddCommand("test4-1", TypicalIndexes.INDEX_FIRST_PERSON,
                TypicalIndexes.INDEX_FIRST_PERSON,
                1,
                new Date(2023, 1, 1),
                "was a good session");
        assertEquals(logAddCommand1, logAddCommand1Copy);

        // different types -> returns false
        assertNotEquals(1, logAddCommand1);

        // null -> returns false
        assertNotEquals(null, logAddCommand1);

        // different command -> returns false
        assertNotEquals(logAddCommand1, logAddCommand2);
    }

    @Test
    void toStringMethod() {
        LogAddCommand logAddCommand = new LogAddCommand("test5", TypicalIndexes.INDEX_FIRST_PERSON,
                TypicalIndexes.INDEX_FIRST_PERSON,
                1,
                new Date(2023, 1, 1),
                "was a good session");
        String expected = LogAddCommand.class.getCanonicalName()
                + "{title=" + "test5"
                + ", volunteerIndex=" + TypicalIndexes.INDEX_FIRST_PERSON
                + ", befriendeeIndex=" + TypicalIndexes.INDEX_FIRST_PERSON
                + ", duration=" + Integer.toString(1)
                + ", startDate=" + new Date(2023, 1, 1)
                + ", remarks=" + "was a good session" + "}";
        assertEquals(expected, logAddCommand.toString());
    }
}
