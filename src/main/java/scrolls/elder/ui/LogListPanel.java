package scrolls.elder.ui;

import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import scrolls.elder.commons.core.LogsCenter;
import scrolls.elder.model.Datastore;
import scrolls.elder.model.ReadOnlyDatastore;
import scrolls.elder.model.ReadOnlyPersonStore;
import scrolls.elder.model.log.Log;

/**
 * Panel containing the list of logs.
 */
public class LogListPanel extends UiPart<Region> {
    public static final Predicate<Log> PREDICATE_SHOW_ALL_LOGS = unused -> true;
    private static final String FXML = "LogListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(LogListPanel.class);

    private ReadOnlyDatastore datastore;

    @FXML
    private ListView<Log> logListView;

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList}.
     */
    public LogListPanel(ReadOnlyDatastore datastore) {
        super(FXML);

        this.datastore = datastore;
        ObservableList<Log> logList = datastore.getLogStore().getFilteredLogList();
        logListView.setItems(logList);
        logListView.setCellFactory(listView -> new LogListPanel.LogListViewCell());
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Log} using a {@code LogCard}.
     */
    class LogListViewCell extends ListCell<Log> {
        @Override
        protected void updateItem(Log log, boolean empty) {
            super.updateItem(log, empty);

            if (empty || log == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new LogCard(log, getIndex() + 1, datastore).getRoot());
            }
        }
    }
}
