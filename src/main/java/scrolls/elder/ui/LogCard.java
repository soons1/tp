package scrolls.elder.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import scrolls.elder.model.ReadOnlyDatastore;
import scrolls.elder.model.log.Log;

/**
 * A UI component that displays information of a {@code Log}.
 */
public class LogCard extends UiPart<Region> {
    private static final String FXML = "LogCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Log log;

    private String dateFormatPattern = "dd MMM yyyy";
    private DateFormat dateFormatter;

    @FXML
    private HBox cardPane;
    @FXML
    private Label id;
    @FXML
    private Label date;
    @FXML
    private Label title;
    @FXML
    private Label volunteer;
    @FXML
    private Label befriendee;
    @FXML
    private Label duration;
    @FXML
    private Label remarks;

    /**
     * Creates a {@code PersonCode} with the given {@code Person} and index to display.
     */
    public LogCard(Log log, int displayedIndex, ReadOnlyDatastore datastore) {
        super(FXML);

        this.log = log;
        dateFormatter = new SimpleDateFormat(dateFormatPattern);
        String volunteerName = datastore.getPersonStore().getNameFromID(log.getVolunteerId()).fullName;
        String befriendeeName = datastore.getPersonStore().getNameFromID(log.getBefriendeeId()).fullName;

        id.setText(displayedIndex + ". ");
        title.setText(log.getLogTitle());
        befriendee.setText("Befriendee: " + befriendeeName);
        volunteer.setText("Volunteer: " + volunteerName);
        date.setText(dateFormatter.format(log.getStartDate()));
        duration.setText("Duration: " + log.getDuration() + " hrs");
        remarks.setText("Remarks: " + log.getRemarks());
    }
}
