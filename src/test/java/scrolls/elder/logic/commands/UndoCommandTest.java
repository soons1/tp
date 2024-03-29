package scrolls.elder.logic.commands;

import static scrolls.elder.logic.commands.CommandTestUtil.assertCommandFailure;
import static scrolls.elder.logic.commands.CommandTestUtil.assertCommandSuccess;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import scrolls.elder.model.Datastore;
import scrolls.elder.model.Model;
import scrolls.elder.model.ModelManager;
import scrolls.elder.model.PersonStore;
import scrolls.elder.model.UserPrefs;
import scrolls.elder.model.person.Person;
import scrolls.elder.testutil.PersonBuilder;
import scrolls.elder.testutil.TypicalDatastore;
import scrolls.elder.testutil.TypicalIndexes;
import scrolls.elder.testutil.TypicalPersons;
public class UndoCommandTest {
    private Model model;
    private Model expectedModel;
    private PersonStore personStore;
    private PersonStore expectedPersonStore;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(TypicalDatastore.getTypicalDatastore(), new UserPrefs());
        personStore = model.getMutableDatastore().getMutablePersonStore();
        expectedModel = new ModelManager(new Datastore(model.getDatastore()), new UserPrefs());
        expectedPersonStore = expectedModel.getMutableDatastore().getMutablePersonStore();
    }

    @Test
    public void execute_undoAdd_success() {
        Person validPerson = new PersonBuilder().build();
        personStore.addPerson(validPerson);
        model.commitDatastore();

        assertCommandSuccess(new UndoCommand(), model, UndoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_invalidUndoNewModel_throwsCommandException() {
        UndoCommand undoCommand = new UndoCommand();
        String expectedMessage = UndoCommand.MESSAGE_UNDO_ERROR;
        assertCommandFailure(undoCommand, model, expectedMessage);
    }

    @Test
    public void execute_invalidUndoAfterUndoToFirstState_throwsCommandException() {
        // Add a person
        Person validPerson = new PersonBuilder().build();
        personStore.addPerson(validPerson);
        model.commitDatastore();

        // Undo to first state
        model.undoChanges();

        UndoCommand undoCommand = new UndoCommand();
        String expectedMessage = UndoCommand.MESSAGE_UNDO_ERROR;
        assertCommandFailure(undoCommand, model, expectedMessage);
    }

    @Test
    public void execute_undoAfterRedo_success() {
        // Add valid Person
        Person validPerson = new PersonBuilder().build();
        personStore.addPerson(validPerson);
        model.commitDatastore();

        // Undo changes
        model.undoChanges();

        // Redo changes
        model.redoChanges();

        assertCommandSuccess(new UndoCommand(), model, UndoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_undoDelete_success() {
        // Delete person
        Person personToDelete =
                personStore.getFilteredVolunteerList().get(TypicalIndexes.INDEX_SECOND_PERSON.getZeroBased());
        personStore.removePerson(personToDelete);
        model.commitDatastore();

        assertCommandSuccess(new UndoCommand(), model, UndoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_undoClear_success() {
        model.setDatastore(new Datastore());
        model.commitDatastore();

        assertCommandSuccess(new UndoCommand(), model, UndoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_undoEdit_success() {
        Person editedPerson = new PersonBuilder(TypicalPersons.BENSON).build();
        personStore.setPerson(personStore.getFilteredVolunteerList().get(1), editedPerson);
        model.commitDatastore();

        assertCommandSuccess(new UndoCommand(), model, UndoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_undoPair_success() {
        Person befriendeeToPair =
                personStore.getFilteredBefriendeeList().get(TypicalIndexes.INDEX_SECOND_PERSON.getZeroBased());
        Person volunteerToPair =
                personStore.getFilteredVolunteerList().get(TypicalIndexes.INDEX_SECOND_PERSON.getZeroBased());

        Person afterPairingPerson1 = new PersonBuilder(befriendeeToPair)
                .withPairedWithName(Optional.of(volunteerToPair.getName()))
                .withPairedWithID(Optional.of(volunteerToPair.getPersonId())).build();
        Person afterPairingPerson2 = new PersonBuilder(volunteerToPair)
                .withPairedWithName(Optional.of(befriendeeToPair.getName()))
                .withPairedWithID(Optional.of(befriendeeToPair.getPersonId())).build();

        personStore.setPerson(
                personStore.getFilteredBefriendeeList().get(TypicalIndexes.INDEX_SECOND_PERSON.getZeroBased()),
                afterPairingPerson1);
        personStore.setPerson(
                personStore.getFilteredVolunteerList().get(TypicalIndexes.INDEX_SECOND_PERSON.getZeroBased()),
                afterPairingPerson2);
        model.commitDatastore();

        assertCommandSuccess(new UndoCommand(), model, UndoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_undoUnpair_success() {
        Person personToUnpair1 = personStore.getFilteredBefriendeeList()
                .get(TypicalIndexes.INDEX_FIRST_PERSON.getZeroBased());
        Person personToUnpair2 =
                personStore.getFilteredVolunteerList().get(TypicalIndexes.INDEX_FIRST_PERSON.getZeroBased());

        Person afterUnpairingPerson1 = new PersonBuilder(personToUnpair1)
                .withPairedWithName(Optional.empty()).withPairedWithID(Optional.empty()).build();
        Person afterUnpairingPerson2 = new PersonBuilder(personToUnpair2)
                .withPairedWithName(Optional.empty()).withPairedWithID(Optional.empty()).build();

        personStore.setPerson(
                personStore.getFilteredBefriendeeList().get(TypicalIndexes.INDEX_FIRST_PERSON.getZeroBased()),
                afterUnpairingPerson1);
        personStore.setPerson(
                personStore.getFilteredVolunteerList().get(TypicalIndexes.INDEX_FIRST_PERSON.getZeroBased()),
                afterUnpairingPerson2);

        model.commitDatastore();

        assertCommandSuccess(new UndoCommand(), model, UndoCommand.MESSAGE_SUCCESS, expectedModel);
    }
}
