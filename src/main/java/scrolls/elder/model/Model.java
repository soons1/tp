package scrolls.elder.model;

import java.nio.file.Path;
import java.util.function.Predicate;

import com.sun.jdi.request.InvalidRequestStateException;
import scrolls.elder.commons.core.GuiSettings;
import scrolls.elder.model.person.Person;

/**
 * The API of the Model component. Controls in-memory data of the application.
 */
public interface Model {
    /**
     * {@code Predicate} that always evaluate to true
     */
    Predicate<Person> PREDICATE_SHOW_ALL = unused -> true;

    //// UserPrefs getters and setters

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Replaces user prefs data with the data in {@code userPrefs}.
     */
    void setUserPrefs(ReadOnlyUserPrefs userPrefs);

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    //// Datastore getters and setters

    /**
     * Returns the user prefs' datastore file path.
     */
    Path getDatastoreFilePath();

    /**
     * Sets the user prefs' datastore file path.
     */
    void setDatastoreFilePath(Path datastoreFilePath);

    //// Datastore getters and setters

    /**
     * Returns a readonly view of the Datastore
     */
    ReadOnlyDatastore getDatastore();

    /**
     * Returns a mutable view of the Datastore
     */
    Datastore getMutableDatastore();

    /**
     * Replaces Datastore with the data in {@code datastore}.
     */
    void setDatastore(ReadOnlyDatastore datastore);

    /**
     * Returns a view of the DatastoreVersionStorage
     */
    DatastoreVersionStorage getDatastoreVersionStorage();

    /**
     * Commits Datastore to the DatastoreVersionStorage with the cuurent data in {@code datastore}.
     */
    void commitDatastore();

    /**
     * Reverts the datastore to its previous state immediately before the current datastore state.
     * @throws InvalidRequestStateException If there are no changes to undo.
     */
    void undoChanges() throws InvalidRequestStateException;

    /**
     * Reverses the effects of the most recent undo operation.
     * @throws InvalidRequestStateException If there are no undo operations to reverse.
     */
    void redoChanges() throws InvalidRequestStateException ;

}
