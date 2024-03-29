package scrolls.elder.model;

import java.util.ArrayList;

/**
 * Represents a storage for snapshots of the Datastore.
 * After every operation where the Datastore of the application is mutated in someway,
 * there will be an entry created in the DatastoreVersionStorage to enable undo and redo operations.
 */
public class DatastoreVersionStorage {
    /**
     * The list of datastore versions stored in this storage.
     */
    private ArrayList<ReadOnlyDatastore> datastoreVersions;

    /**
     * The pointer indicating the current position in the list of versions.
     */
    private int currentStatePointer;

    /**
     * Constructs a new DatastoreVersionStorage with an initial snapshot of the provided datastore.
     *
     * @param datastore The initial state of the Datastore.
     */
    public DatastoreVersionStorage(ReadOnlyDatastore datastore) {
        this.datastoreVersions = new ArrayList<ReadOnlyDatastore>();
        // Note that snapshot must be a deep copy.
        datastoreVersions.add(new Datastore(datastore));
        this.currentStatePointer = 0;
    }

    /**
     * Checks if an undo operation is possible.
     *
     * @return true if an undo operation can be performed, false otherwise.
     */
    public boolean canUndo() {
        // Pointer has at least 1 version of datastore to its left
        if (currentStatePointer > 0) {
            return true;
        }

        return false;
    }

    /**
     * Checks if a redo operation is possible.
     *
     * @return true if a redo operation can be performed, false otherwise.
     */
    public boolean canRedo() {
        int size = datastoreVersions.size() - 1;

        // pointer is not at the end of the version history
        if (currentStatePointer < size) {
            return true;
        }

        return false;

    }

    /**
     * Executes the undo operation by reverting to the previous version of the Datastore.
     *
     * @return The Datastore in its previous state after undoing an operation.
     */
    public ReadOnlyDatastore executeUndo() {
        assert this.canUndo() : "Undo command cannot be executed";

        currentStatePointer--;
        ReadOnlyDatastore prevDatastore = datastoreVersions.get(currentStatePointer);
        return prevDatastore;
    }

    /**
     * Executes the redo operation by moving forward to the next version of the Datastore.
     *
     * @return The Datastore in its next state after reversing an undo operation.
     */
    public ReadOnlyDatastore executeRedo() {
        assert this.canRedo() : "Redo command cannot be executed";

        currentStatePointer++;
        ReadOnlyDatastore nextDatastore = datastoreVersions.get(currentStatePointer);
        return nextDatastore;
    }

    /**
     * Commits a new snapshot of the Datastore to the storage.
     * If the currentStatePointer is not at the end of the list, purges
     * the subsequent snapshots before adding the new one.
     *
     * @param datastore The updated state of the Datastore to be committed.
     */
    public void commitDatastore(ReadOnlyDatastore datastore) {
        int size = this.datastoreVersions.size();

        // If not at end of list, purge the data before adding new datastore snapshot
        if (currentStatePointer < size - 1) {
            for (int i = currentStatePointer + 1; i < size; i++) {
                this.datastoreVersions.remove(i);
            }
        }

        this.datastoreVersions.add(new Datastore(datastore));
        currentStatePointer++;
    }
}
