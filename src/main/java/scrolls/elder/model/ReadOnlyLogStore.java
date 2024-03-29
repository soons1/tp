package scrolls.elder.model;

import javafx.collections.ObservableList;
import scrolls.elder.model.log.Log;
import scrolls.elder.model.person.Person;

import java.util.function.Predicate;

/**
 * Unmodifiable view of the log store.
 */
public interface ReadOnlyLogStore {
    /**
     * Returns an unmodifiable view of the log list.
     */
    ObservableList<Log> getLogList();

    /**
     * Returns a filtered view of the log list.
     */
    ObservableList<Log> getFilteredLogList();

    /**
     * Updates the filter of the filtered log list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredLogList(Predicate<Log> predicate);
}
