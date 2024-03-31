package scrolls.elder.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static scrolls.elder.logic.commands.CommandTestUtil.assertCommandFailure;
import static scrolls.elder.logic.commands.CommandTestUtil.assertCommandSuccess;

import org.junit.jupiter.api.Test;

import scrolls.elder.commons.core.index.Index;
import scrolls.elder.logic.Messages;
import scrolls.elder.model.Datastore;
import scrolls.elder.model.LogStore;
import scrolls.elder.model.Model;
import scrolls.elder.model.ModelManager;
import scrolls.elder.model.UserPrefs;
import scrolls.elder.model.log.Log;
import scrolls.elder.testutil.TypicalDatastore;
import scrolls.elder.testutil.TypicalIndexes;
import scrolls.elder.testutil.TypicalLogs;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code LogDeleteCommand}.
 */
class LogDeleteCommandTest {

    private Model model;
    private LogStore logStore;
    private Model expectedModel;
    private LogStore expectedLogStore;

    @Test
    public void setUp() {
        model = new ModelManager(TypicalDatastore.getTypicalDatastore(), new UserPrefs());
        logStore = model.getMutableDatastore().getMutableLogStore();
        expectedModel = new ModelManager(new Datastore(), new UserPrefs());
        expectedLogStore = expectedModel.getMutableDatastore().getMutableLogStore();
    }

    @Test
    void execute_invalidIndexList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(logStore.getFilteredLogList().size() + 1);
        LogDeleteCommand logDeleteCommand = new LogDeleteCommand(outOfBoundIndex);
        String expectedMessage =
                LogDeleteCommand.MESSAGE_DELETE_LOG_ERROR + Messages.MESSAGE_INVALID_LOG_DISPLAYED_INDEX;
        assertCommandFailure(logDeleteCommand, model, expectedMessage);
    }

    @Test
    void execute_validIndexList_success() {
        logStore.addLogWithId(TypicalLogs.LOG_BENSON_TO_FIONA);
        Index indexLastLog = Index.fromOneBased(logStore.getLogList().size());
        LogDeleteCommand logDeleteCommand = new LogDeleteCommand(indexLastLog);
        Log logToDelete = logStore.getLogList().get(indexLastLog.getZeroBased());
        String expectedMessage = String.format(LogDeleteCommand.MESSAGE_DELETE_LOG_SUCCESS,
                Messages.formatLog(logToDelete));
        expectedLogStore.removeLog(logToDelete.getLogId());
        expectedModel.commitDatastore();
        assertCommandSuccess(logDeleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    void equals() {
        LogDeleteCommand deleteFirstCommand = new LogDeleteCommand(TypicalIndexes.INDEX_FIRST_LOG);
        LogDeleteCommand deleteSecondCommand = new LogDeleteCommand(TypicalIndexes.INDEX_SECOND_LOG);

        //same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        //same values -> returns true
        LogDeleteCommand deleteFirstCommandCopy = new LogDeleteCommand(TypicalIndexes.INDEX_FIRST_LOG);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        //different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        //null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        //different log -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    void testToString() {
        Index targetIndex = Index.fromOneBased(1);
        LogDeleteCommand logDeleteCommand = new LogDeleteCommand(targetIndex);
        String expected = String.format("%s{targetIndex=%s}", LogDeleteCommand.class.getCanonicalName(), targetIndex);
        assertEquals(expected, logDeleteCommand.toString());
    }
}
