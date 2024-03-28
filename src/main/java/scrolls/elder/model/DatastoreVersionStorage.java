package scrolls.elder.model;

import java.util.ArrayList;

public class DatastoreVersionStorage {
    private ArrayList<ReadOnlyDatastore> datastoreVersions;
    private int pointer;

    public DatastoreVersionStorage(ReadOnlyDatastore datastore) {
        this.datastoreVersions = new ArrayList<ReadOnlyDatastore>();
        datastoreVersions.add(datastore);
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
        this.datastoreVersions.add(datastore);
        pointer++;
    }
}
