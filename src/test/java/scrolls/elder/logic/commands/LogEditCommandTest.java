package scrolls.elder.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static scrolls.elder.logic.commands.CommandTestUtil.assertCommandSuccess;
import static scrolls.elder.logic.commands.LogEditCommand.MESSAGE_EDIT_LOG_SUCCESS;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import scrolls.elder.commons.core.index.Index;
import scrolls.elder.logic.Messages;
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
import scrolls.elder.testutil.EditLogDescriptorBuilder;
import scrolls.elder.testutil.PersonBuilder;
import scrolls.elder.testutil.TypicalDatastore;
import scrolls.elder.testutil.TypicalIndexes;
import scrolls.elder.testutil.TypicalLogs;

/**
 * Contains integration tests (interaction with the Model) and unit tests for LogEditCommand.
 */
class LogEditCommandTest {
    private Model model;
    private LogStore logStore;
    private Model expectedModel;
    private LogStore expectedLogStore;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(TypicalDatastore.getTypicalDatastore(), new UserPrefs());
        logStore = model.getMutableDatastore().getMutableLogStore();
        expectedModel = new ModelManager(new Datastore(model.getDatastore()), new UserPrefs());
        expectedLogStore = expectedModel.getMutableDatastore().getMutableLogStore();
        logStore.addLog(TypicalLogs.LOG_ALICE_TO_ELLE);
        expectedLogStore.addLog(TypicalLogs.LOG_ALICE_TO_ELLE);
    }

    @Test
    void execute_validWithNoChange_success() {
        LogEditCommand.EditLogDescriptor editLogDescriptor =
                new EditLogDescriptorBuilder(TypicalLogs.LOG_ALICE_TO_ELLE).build();
        LogEditCommand logEditCommand = new LogEditCommand(TypicalIndexes.INDEX_FIRST_PERSON, editLogDescriptor);

        Log editedLog = new Log(0, TypicalLogs.LOG_ALICE_TO_ELLE.getLogTitle(),
                TypicalLogs.LOG_ALICE_TO_ELLE.getVolunteerId(), TypicalLogs.LOG_ALICE_TO_ELLE.getBefriendeeId(),
                TypicalLogs.LOG_ALICE_TO_ELLE.getDuration(), TypicalLogs.LOG_ALICE_TO_ELLE.getStartDate(),
                TypicalLogs.LOG_ALICE_TO_ELLE.getRemarks());

        expectedLogStore.setLog(editedLog);
        expectedModel.commitDatastore();
        String expectedMessage = String.format(MESSAGE_EDIT_LOG_SUCCESS, Messages.formatLog(editedLog));
        assertCommandSuccess(logEditCommand, model, expectedMessage, expectedModel);

    }

    @Test
    void execute_validLogEditCommand_success() {
        LogEditCommand.EditLogDescriptor editLogDescriptor =
                new EditLogDescriptorBuilder(TypicalLogs.LOG_ALICE_TO_ELLE).withDuration(5).build();
        LogEditCommand logEditCommand = new LogEditCommand(TypicalIndexes.INDEX_FIRST_PERSON, editLogDescriptor);

        Log editedLog = new Log(0, TypicalLogs.LOG_ALICE_TO_ELLE.getLogTitle(),
                TypicalLogs.LOG_ALICE_TO_ELLE.getVolunteerId(), TypicalLogs.LOG_ALICE_TO_ELLE.getBefriendeeId(),
                5, TypicalLogs.LOG_ALICE_TO_ELLE.getStartDate(),
                TypicalLogs.LOG_ALICE_TO_ELLE.getRemarks());

        PersonStore expectedPersonStore = expectedModel.getMutableDatastore().getMutablePersonStore();
        Person befriendee = expectedPersonStore.getPersonFromID(editedLog.getBefriendeeId());
        Person volunteer = expectedPersonStore.getPersonFromID(editedLog.getVolunteerId());
        Person updatedBefriendee = new PersonBuilder(befriendee).withTimeServed(5).build();
        Person updatedVolunteer = new PersonBuilder(volunteer).withTimeServed(5).build();

        expectedPersonStore.setPerson(befriendee, updatedBefriendee);
        expectedPersonStore.setPerson(volunteer, updatedVolunteer);
        expectedLogStore.setLog(editedLog);
        expectedModel.commitDatastore();
        String expectedMessage = String.format(MESSAGE_EDIT_LOG_SUCCESS, Messages.formatLog(editedLog));
        assertCommandSuccess(logEditCommand, model, expectedMessage, expectedModel);

    }

    @Test
    void execute_invalidIndex_throwsCommandException() {
        LogEditCommand.EditLogDescriptor editLogDescriptor =
                new EditLogDescriptorBuilder(TypicalLogs.LOG_ALICE_TO_ELLE).build();
        LogEditCommand logEditCommand = new LogEditCommand(TypicalIndexes.INDEX_FIFTH_PERSON, editLogDescriptor);
        Assert.assertThrows(
                CommandException.class,
                Messages.MESSAGE_INVALID_LOG_DISPLAYED_INDEX, () -> logEditCommand.execute(model));
    }

    @Test
    void execute_invalidPersonId_throwsCommandException() {
        LogEditCommand.EditLogDescriptor editLogDescriptor =
                new EditLogDescriptorBuilder(TypicalLogs.LOG_ALICE_TO_ELLE)
                        .withBefriendeeIndex(Index.fromZeroBased(7)).build();
        LogEditCommand logEditCommand = new LogEditCommand(TypicalIndexes.INDEX_FIRST_PERSON, editLogDescriptor);
        Assert.assertThrows(
                CommandException.class, Log.MESSAGE_INVALID_ID, () -> logEditCommand.execute(model));
    }

    @Test
    void execute_negativeDuration_throwsCommandException() {
        LogEditCommand.EditLogDescriptor editLogDescriptor =
                new EditLogDescriptorBuilder(TypicalLogs.LOG_ALICE_TO_ELLE).withDuration(-1).build();
        LogEditCommand logEditCommand = new LogEditCommand(TypicalIndexes.INDEX_FIRST_PERSON, editLogDescriptor);
        Assert.assertThrows(
                CommandException.class, LogAddCommand.MESSAGE_NEGATIVE_DURATION, () -> logEditCommand.execute(model));
    }

    @Test
    void testEquals() {
        LogEditCommand.EditLogDescriptor editLogDescriptor1 =
                new EditLogDescriptorBuilder(TypicalLogs.LOG_BENSON_TO_FIONA).build();
        LogEditCommand.EditLogDescriptor editLogDescriptor2 =
                new EditLogDescriptorBuilder(TypicalLogs.LOG_ALICE_TO_ELLE).build();
        LogEditCommand logEditCommand1 = new LogEditCommand(TypicalIndexes.INDEX_FIRST_PERSON, editLogDescriptor1);
        LogEditCommand logEditCommand2 = new LogEditCommand(TypicalIndexes.INDEX_FIRST_PERSON, editLogDescriptor2);

        // same object -> returns true
        assertEquals(logEditCommand1, logEditCommand1);

        // same values -> returns true
        LogEditCommand logEditCommand1Copy = new LogEditCommand(TypicalIndexes.INDEX_FIRST_PERSON, editLogDescriptor1);
        assertEquals(logEditCommand1, logEditCommand1Copy);

        // different types -> returns false
        assertNotEquals(1, logEditCommand1);

        // null -> returns false
        assertNotEquals(null, logEditCommand1);

        // different command -> returns false
        assertNotEquals(logEditCommand1, logEditCommand2);
    }

    @Test
    void testToString() {
        LogEditCommand.EditLogDescriptor editLogDescriptor =
                new EditLogDescriptorBuilder(TypicalLogs.LOG_ALICE_TO_ELLE).build();
        LogEditCommand logEditCommand = new LogEditCommand(TypicalIndexes.INDEX_FIRST_PERSON, editLogDescriptor);
        String expected = LogEditCommand.class.getCanonicalName()
                + "{index=" + TypicalIndexes.INDEX_FIRST_PERSON
                + ", editLogDescriptor=" + editLogDescriptor + "}";
        assertEquals(expected, logEditCommand.toString());
    }
}
