package scrolls.elder.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static scrolls.elder.logic.commands.CommandTestUtil.assertCommandSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import scrolls.elder.logic.Messages;
import scrolls.elder.logic.commands.exceptions.CommandException;
import scrolls.elder.model.Datastore;
import scrolls.elder.model.LogStore;
import scrolls.elder.model.Model;
import scrolls.elder.model.ModelManager;
import scrolls.elder.model.UserPrefs;
import scrolls.elder.model.person.Role;
import scrolls.elder.testutil.Assert;
import scrolls.elder.testutil.TypicalDatastore;
import scrolls.elder.testutil.TypicalIndexes;
import scrolls.elder.testutil.TypicalLogs;
import scrolls.elder.testutil.TypicalPersons;

/**
 * Contains integration tests (interaction with the Model) and unit tests for LogFindCommand.
 */
public class LogFindCommandTest {

    private Model model;
    private LogStore logStore;
    private Model expectedModel;
    private LogStore expectedLogStore;

    private final Role volunteerRole = new Role(CommandTestUtil.VALID_ROLE_VOLUNTEER);
    private final Role befriendeeRole = new Role(CommandTestUtil.VALID_ROLE_BEFRIENDEE);

    @BeforeEach
    public void setUp() {
        model = new ModelManager(TypicalDatastore.getTypicalDatastore(), new UserPrefs());
        logStore = model.getMutableDatastore().getMutableLogStore();
        expectedModel = new ModelManager(new Datastore(model.getDatastore()), new UserPrefs());
        expectedLogStore = expectedModel.getMutableDatastore().getMutableLogStore();

        logStore.addLog(TypicalLogs.LOG_ALICE_TO_ELLE);
        expectedLogStore.addLog(TypicalLogs.LOG_ALICE_TO_ELLE);

        logStore.addLog(TypicalLogs.LOG_BENSON_TO_FIONA);
        expectedLogStore.addLog(TypicalLogs.LOG_BENSON_TO_FIONA);
    }

    @Test
    void execute_invalidIndex_throwsCommandException() {
        LogFindCommand logFindCommandVolunteer = new LogFindCommand(TypicalIndexes.INDEX_FIFTH_PERSON, volunteerRole);
        Assert.assertThrows(CommandException.class, LogFindCommand.MESSAGE_FINDLOG_PERSON_ERROR
                + Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, () -> logFindCommandVolunteer.execute(model));

        LogFindCommand logFindCommandBefriendee = new LogFindCommand(TypicalIndexes.INDEX_FIFTH_PERSON, befriendeeRole);
        Assert.assertThrows(CommandException.class, LogFindCommand.MESSAGE_FINDLOG_PERSON_ERROR
                + Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, () -> logFindCommandBefriendee.execute(model));
    }

    @Test
    void execute_validLogFindCommandVolunteer_success() {
        LogFindCommand logFindCommandAlice = new LogFindCommand(TypicalIndexes.INDEX_FIRST_PERSON, volunteerRole);

        int alicePersonId = TypicalPersons.ALICE.getPersonId();
        expectedLogStore.updateFilteredLogListByPersonId(alicePersonId);
        expectedModel.commitDatastore();

        String expectedMessage = LogFindCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(logFindCommandAlice, model, expectedMessage, expectedModel);
    }

    @Test
    void execute_validLogFindCommandBefriendee_success() {
        LogFindCommand logFindCommandElle = new LogFindCommand(TypicalIndexes.INDEX_FIRST_PERSON, befriendeeRole);

        int ellePersonId = TypicalPersons.ELLE.getPersonId();
        expectedLogStore.updateFilteredLogListByPersonId(ellePersonId);
        expectedModel.commitDatastore();

        String expectedMessage = LogFindCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(logFindCommandElle, model, expectedMessage, expectedModel);
    }


    @Test
    void testEquals() {
        LogFindCommand logFindCommand1 = new LogFindCommand(TypicalIndexes.INDEX_FIRST_PERSON, volunteerRole);
        LogFindCommand logFindCommand2 = new LogFindCommand(TypicalIndexes.INDEX_FIRST_PERSON, befriendeeRole);

        // same object -> returns true
        assertEquals(logFindCommand1, logFindCommand1);

        // same values -> returns true
        LogFindCommand logFindCommand1Copy = new LogFindCommand(TypicalIndexes.INDEX_FIRST_PERSON, volunteerRole);
        assertEquals(logFindCommand1, logFindCommand1Copy);

        // different types -> returns false
        assertNotEquals(1, logFindCommand1);
        assertFalse(logFindCommand1.equals(1));

        // null -> returns false
        assertNotEquals(null, logFindCommand1);

        // different command -> returns false
        assertNotEquals(logFindCommand1, logFindCommand2);
    }

    @Test
    void testToString() {
        LogFindCommand logFindCommand = new LogFindCommand(TypicalIndexes.INDEX_FIRST_PERSON, volunteerRole);
        String expected = LogFindCommand.class.getCanonicalName()
                + "{role=" + volunteerRole
                + ", listIndex=" + TypicalIndexes.INDEX_FIRST_PERSON + "}";
        assertEquals(expected, logFindCommand.toString());
    }





}
