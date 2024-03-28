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

public class RedoCommandTest {
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
    public void execute_redoAdd_success() {
        Person validPerson = new PersonBuilder().build();

        // Add valid person to model
        personStore.addPerson(validPerson);
        model.commitDatastore();

        // This should be the state after redo is performed
        expectedPersonStore.addPerson(validPerson);
        expectedModel.commitDatastore();

        // Undo adding valid person
        model.undoChanges();
        assertCommandSuccess(new RedoCommand(), model, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_invalidRedoNewList_throwsCommandException() {
        RedoCommand redoCommand = new RedoCommand();
        String expectedMessage = RedoCommand.MESSAGE_REDO_ERROR;
        assertCommandFailure(redoCommand, model, expectedMessage);
    }

    @Test
    public void execute_invalidRedoLatestState_throwsCommandException() {
        Person validPerson = new PersonBuilder().build();

        // Add valid person to model
        personStore.addPerson(validPerson);
        model.commitDatastore();

        RedoCommand redoCommand = new RedoCommand();
        String expectedMessage = RedoCommand.MESSAGE_REDO_ERROR;
        assertCommandFailure(redoCommand, model, expectedMessage);
    }

    @Test
    public void execute_redoDelete_success() {
        // Delete person
        Person personToDelete =
                personStore.getFilteredVolunteerList().get(TypicalIndexes.INDEX_SECOND_PERSON.getZeroBased());
        personStore.removePerson(personToDelete);
        model.commitDatastore();

        // Delete in expected person store
        expectedPersonStore.removePerson(personToDelete);
        expectedModel.commitDatastore();

        // Undo adding valid person
        model.undoChanges();

        assertCommandSuccess(new RedoCommand(), model, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_redoClear_success() {
        model.setDatastore(new Datastore());
        model.commitDatastore();

        expectedModel.setDatastore(new Datastore());
        expectedModel.commitDatastore();

        // Undo changes
        model.undoChanges();

        assertCommandSuccess(new RedoCommand(), model, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_redoEdit_success() {
        Person editedPerson = new PersonBuilder(TypicalPersons.BENSON).build();
        personStore.setPerson(personStore.getFilteredVolunteerList().get(1), editedPerson);
        model.commitDatastore();

        expectedPersonStore.setPerson(personStore.getFilteredVolunteerList().get(1), editedPerson);
        expectedModel.commitDatastore();

        // Undo changes
        model.undoChanges();

        assertCommandSuccess(new RedoCommand(), model, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_redoPair_success() {
        // Preparing the pair
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

        // Pair volunteer and befriendee in model, changes to be undone
        personStore.setPerson(
                personStore.getFilteredBefriendeeList().get(TypicalIndexes.INDEX_SECOND_PERSON.getZeroBased()),
                afterPairingPerson1);
        personStore.setPerson(
                personStore.getFilteredVolunteerList().get(TypicalIndexes.INDEX_SECOND_PERSON.getZeroBased()),
                afterPairingPerson2);
        model.commitDatastore();

        // Pair volunteer and befriendee in expected model
        expectedPersonStore.setPerson(
                expectedPersonStore.getFilteredBefriendeeList().get(TypicalIndexes.INDEX_SECOND_PERSON.getZeroBased()),
                afterPairingPerson1);
        expectedPersonStore.setPerson(
                expectedPersonStore.getFilteredVolunteerList().get(TypicalIndexes.INDEX_SECOND_PERSON.getZeroBased()),
                afterPairingPerson2);
        expectedModel.commitDatastore();

        // Undo changes
        model.undoChanges();

        assertCommandSuccess(new RedoCommand(), model, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_redoUnpair_success() {
        // Prepare the unpair
        Person personToUnpair1 = personStore.getFilteredBefriendeeList()
                .get(TypicalIndexes.INDEX_FIRST_PERSON.getZeroBased());
        Person personToUnpair2 =
                personStore.getFilteredVolunteerList().get(TypicalIndexes.INDEX_FIRST_PERSON.getZeroBased());

        Person afterUnpairingPerson1 = new PersonBuilder(personToUnpair1)
                .withPairedWithName(Optional.empty()).withPairedWithID(Optional.empty()).build();
        Person afterUnpairingPerson2 = new PersonBuilder(personToUnpair2)
                .withPairedWithName(Optional.empty()).withPairedWithID(Optional.empty()).build();

        // Unpair in model
        personStore.setPerson(
                personStore.getFilteredBefriendeeList().get(TypicalIndexes.INDEX_FIRST_PERSON.getZeroBased()),
                afterUnpairingPerson1);
        personStore.setPerson(
                personStore.getFilteredVolunteerList().get(TypicalIndexes.INDEX_FIRST_PERSON.getZeroBased()),
                afterUnpairingPerson2);
        model.commitDatastore();

        // Unpair in expected model
        expectedPersonStore.setPerson(
                expectedPersonStore.getFilteredBefriendeeList().get(TypicalIndexes.INDEX_FIRST_PERSON.getZeroBased()),
                afterUnpairingPerson1);
        expectedPersonStore.setPerson(
                expectedPersonStore.getFilteredVolunteerList().get(TypicalIndexes.INDEX_FIRST_PERSON.getZeroBased()),
                afterUnpairingPerson2);
        expectedModel.commitDatastore();

        // Undo changes in model
        model.undoChanges();

        assertCommandSuccess(new RedoCommand(), model, RedoCommand.MESSAGE_SUCCESS, expectedModel);
    }
}
