package scrolls.elder.model;

import java.util.function.Predicate;

import javafx.collections.ObservableList;
import scrolls.elder.model.log.Log;


/**
 * Unmodifiable view of the log store.
 */
public interface ReadOnlyLogStore {

    /**
     * Returns an unmodifiable view of all logs in an unfiltered list.
     * This list is unaffected by any filtering operations.
     */
    ObservableList<Log> getUnfilteredAllLogsList();

    /**
     * Returns an unmodifiable view of the log list.
     * This Log List is affected by personID filtering operations.
     * However, it is not affected by Predicate&lt;Log&gt; filtering operations.
     */
    ObservableList<Log> getLogList();

    /**
     * Returns a filtered view of the log list.
     * This Log List is affected by personID filtering operations.
     */
    ObservableList<Log> getFilteredLogList();

    /**
     * Updates the filter of the filtered log list to filter by the given {@code predicate}.
     * Predicate&lt;Log&gt; filters are independent of PersonID filters.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredLogList(Predicate<Log> predicate);

    /**
     * Returns the log with the given ID.
     * {@code logId} must exist in the store.
     */
    Log getLogById(int logId);

    /**
     * Updates the log list to filter by the given {@code personId}.
     * Supply null to reset the filter.
     * This filter will persist for all filtering done with {@link LogStore#updateFilteredLogList}.
     */
    void updateFilteredLogListByPersonId(Integer personId);

}
