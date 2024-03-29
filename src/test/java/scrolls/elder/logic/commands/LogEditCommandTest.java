package scrolls.elder.logic.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import scrolls.elder.model.Datastore;
import scrolls.elder.model.LogStore;
import scrolls.elder.model.Model;
import scrolls.elder.model.ModelManager;
import scrolls.elder.model.UserPrefs;
import scrolls.elder.testutil.TypicalDatastore;

/**
 * ToDo: Complete the relevant tests later.
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
    }

    @Test
    void execute() {
    }
    @Test
    void testEquals() {
    }

    @Test
    void testToString() {
    }
}
