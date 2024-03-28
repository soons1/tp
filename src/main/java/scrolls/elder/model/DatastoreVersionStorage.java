package scrolls.elder.model;

import java.util.ArrayList;

public class DatastoreVersionStorage {
    private ArrayList<ReadOnlyDatastore> datastoreVersions;
    private int pointer;

    public DatastoreVersionStorage(ReadOnlyDatastore datastore) {
        this.datastoreVersions = new ArrayList<ReadOnlyDatastore>();
        datastoreVersions.add(new Datastore(datastore));
        this.pointer = 0;
    }

    public boolean canUndo() {
        // Pointer has at least 1 version of datastore to its left
        if (pointer > 0) {
            return true;
        }

        return false;
    }

    public boolean canRedo() {
        int size = datastoreVersions.size() - 1;

        // pointer is not at the end of the version history
        if (pointer < size) {
            return true;
        }

        return false;

    }

    public ReadOnlyDatastore executeUndo() {
        assert this.canUndo() : "Undo command cannot be executed";

        pointer--;
        ReadOnlyDatastore prevDatastore = datastoreVersions.get(pointer);
        return prevDatastore;
    }

    public ReadOnlyDatastore executeRedo() {
        assert this.canRedo() : "Redo command cannot be executed";

        pointer++;
        ReadOnlyDatastore nextDatastore = datastoreVersions.get(pointer);
        return nextDatastore;
    }

    public void commitDatastore(ReadOnlyDatastore datastore) {
        int size = this.datastoreVersions.size();

        // If not at end of list, purge the data before adding new datastore snapshot
        if (pointer < size - 1) {
            for (int i = pointer + 1; i < size; i++) {
                this.datastoreVersions.remove(i);
            }
        }

        this.datastoreVersions.add(new Datastore(datastore));
        pointer++;
    }
}
