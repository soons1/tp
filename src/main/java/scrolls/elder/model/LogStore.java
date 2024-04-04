package scrolls.elder.model;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import scrolls.elder.commons.util.ToStringBuilder;
import scrolls.elder.model.log.Log;

/**
 * Wraps data for all logs stored.
 * All logs stored are guaranteed to have IDs consistent with the {@code logIdSequence} of the {@code LogStore}
 * instance.
 */
public class LogStore implements ReadOnlyLogStore {
    public static final Predicate<Log> PREDICATE_SHOW_ALL_LOGS = unused -> true;
    private final ObservableMap<Integer, Log> logs;
    private final ObservableMap<Integer, ArrayList<Integer>> normalisedLogsByPerson;
    private final ObservableList<Log> logList;
    private final FilteredList<Log> filteredLogList;

    /**
     * The sequence number that determines the ID of the next log to be added.
     */
    private int logIdSequence;

    /**
     * Creates an empty LogStore.
     */
    public LogStore() {
        this.logIdSequence = 0;

        this.logs = FXCollections.observableHashMap();
        this.normalisedLogsByPerson = FXCollections.observableHashMap();

        this.logList = FXCollections.observableArrayList();
        this.filteredLogList = new FilteredList<>(logList);

        // Binds the backing Map to:
        // - The main ObservableList of logs
        // - The normalised map of logs by person
        MapChangeListener<? super Integer, ? super Log> listener = change -> {
            if (change.wasRemoved()) {
                logList.remove(change.getValueRemoved());
                System.out.println("removed");
                deepRemoveId(normalisedLogsByPerson, change.getValueRemoved().getVolunteerId(),
                    change.getValueRemoved().getLogId());
                deepRemoveId(normalisedLogsByPerson, change.getValueRemoved().getBefriendeeId(),
                    change.getValueRemoved().getLogId());
            }
            if (change.wasAdded()) {
                logList.add(change.getValueAdded());
                System.out.println("added");
                deepAddId(normalisedLogsByPerson, change.getValueAdded().getVolunteerId(),
                    change.getValueAdded().getLogId());
                deepAddId(normalisedLogsByPerson, change.getValueAdded().getBefriendeeId(),
                    change.getValueAdded().getLogId());
            }
        };
        logs.addListener(listener);
    }

    /**
     * Creates an LogStore using the Logs in {@code toBeCopied}
     */
    public LogStore(ReadOnlyLogStore toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// Collection-level getters and setters
    @Override
    public ObservableList<Log> getUnfilteredAllLogsList() {
        return logs.values()
                .stream()
                .collect(Collectors.collectingAndThen(Collectors.toList(), FXCollections::observableArrayList));
    }

    @Override
    public ObservableList<Log> getLogList() {
        return FXCollections.unmodifiableObservableList(logList);
    }

    @Override
    public ObservableList<Log> getFilteredLogList() {
        return filteredLogList;
    }

    @Override
    public void updateFilteredLogList(Predicate<Log> predicate) {
        requireNonNull(predicate);
        filteredLogList.setPredicate(predicate);
    }

    @Override
    public void updateFilteredLogListByPersonId(Integer personId) {
        // Reset when no filter is applied
        if (personId == null) {
            logList.setAll(logs.values());
            return;
        }

        // Filter by personId
        logList.clear();

        // if logs do not exist
        if (normalisedLogsByPerson.get(personId) == null || normalisedLogsByPerson.get(personId).isEmpty()) {
            return;
        }

        normalisedLogsByPerson.get(personId).forEach(logId -> {
            Log log = logs.get(logId);
            if (log != null) {
                logList.add(log);
            }
        });
    }

    /**
     * Replaces the contents of the log list with {@code logs}.
     */
    public void setLogList(List<Log> logs) {
        this.logs.clear();
        int maxId = -1;
        for (Log log : logs) {
            maxId = Math.max(maxId, log.getLogId());
            this.logs.put(log.getLogId(), log);
        }
        logIdSequence = maxId + 1;
    }

    /**
     * Resets the existing data of this {@code LogStore} with {@code newData}.
     */
    public void resetData(ReadOnlyLogStore newData) {
        requireNonNull(newData);
        setLogList(newData.getLogList());
    }

    //// Log-level CRUD operations

    /**
     * Returns true if a log with the same identity as {@code log} exists in the store.
     */
    public boolean hasLog(Log log) {
        requireNonNull(log);

        return logs.containsKey(log.getLogId());
    }

    /**
     * Adds a log to the store.
     * Any relational validation (i.e., volunteerId) should be done before calling this method.
     * Returns the log id for the newly added log.
     */
    public Integer addLog(Log newLog) {
        requireNonNull(newLog);
        int prevLogIdSequence = logIdSequence;
        logs.put(logIdSequence, new Log(logIdSequence, newLog));
        logIdSequence++;
        return prevLogIdSequence;
    }

    /**
     * Adds an existing log to the store.
     * For the case where existing logs are read from storage.
     */
    public void addLogWithId(Log newLog) {
        requireNonNull(newLog);

        logs.put(newLog.getLogId(), newLog);
        if (newLog.getLogId() >= logIdSequence) {
            logIdSequence = newLog.getLogId() + 1;
        }
    }

    @Override
    public Log getLogById(int logId) {
        return logs.get(logId);
    }

    /**
     * Updates a log with the given data from {@code editedLog}.
     * {@code editedLog} must contain an ID that matches an existing log in the store.
     */
    public void setLog(Log editedLog) {
        requireNonNull(editedLog);

        logs.put(editedLog.getLogId(), editedLog);
    }

    /**
     * Removes the log with the given ID from the store.
     * {@code idToRemove} must exist in the store.
     */
    public void removeLog(Integer idToRemove) {
        logs.remove(idToRemove);
    }

    /**
     * Adds a logId to the normalised map.
     * Must replace the entire list to trigger the listener.
     */
    private void deepAddId(ObservableMap<Integer, ArrayList<Integer>> map, int key, int value) {
        ArrayList<Integer> oldList = map.getOrDefault(key, new ArrayList<>());
        ArrayList<Integer> temp = new ArrayList<>(oldList);
        temp.add(value);
        map.put(key, temp);
    }

    /**
     * Removes a logId from the normalised map.
     * Must replace the entire list to trigger the listener.
     */
    private void deepRemoveId(ObservableMap<Integer, ArrayList<Integer>> map, int key, Integer value) {
        ArrayList<Integer> oldList = map.getOrDefault(key, new ArrayList<>());
        ArrayList<Integer> temp = new ArrayList<>(oldList);
        temp.remove(value);
        map.put(key, temp);
    }

    //// Overrides

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .add("logs", logList)
            .toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof LogStore)) {
            return false;
        }

        LogStore otherLogStore = (LogStore) other;
        return logs.equals(otherLogStore.logs);
    }

    @Override
    public int hashCode() {
        return logs.hashCode();
    }
}
