package scrolls.elder.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import scrolls.elder.model.ReadOnlyDatastore;
import scrolls.elder.model.log.Log;
import scrolls.elder.model.person.Volunteer;

/**
 * An UI component that displays information of a {@code Person}.
 */
public class VolunteerCard extends UiPart<Region> {

    private static final String FXML = "VolunteerListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Volunteer vol;
    private String dateFormatPattern = "dd MMM yyyy";
    private DateFormat dateFormatter;
    private ReadOnlyDatastore datastore;
    private final String style = "-fx-font-family: \"Segoe UI\";"
            + "-fx-font-size: 13px;";

    private final String noLogStyle = "-fx-background-color: #696969;";

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private FlowPane tags;

    @FXML
    private Label pairedWith;
    @FXML
    private Label timeServed;
    @FXML
    private VBox latestLog;

    /**
     * Creates a {@code PersonCode} with the given {@code Person} and index to display.
     */
    public VolunteerCard(Volunteer vol, int displayedIndex, ReadOnlyDatastore datastore) {
        super(FXML);
        this.vol = vol;
        dateFormatter = new SimpleDateFormat(dateFormatPattern);
        this.datastore = datastore;

        id.setText(displayedIndex + ". ");
        name.setText(vol.getName().fullName);
        phone.setText(vol.getPhone().value);
        address.setText(vol.getAddress().value);
        email.setText(vol.getEmail().value);
        pairedWith.setText(vol.getPairedWithName().map(p -> "Paired with: " + p.fullName).orElse("Not paired"));
        timeServed.setText("Time Served: " + vol.getTimeServed() + " hours");
        vol.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));


        // If latest log is present, add new log summary card, else add no logs
        if (vol.isLatestLogPresent()) {
            int latestLogId = vol.getLatestLogId().get();
            Log latestLogInstance = datastore.getLogStore().getLogById(latestLogId);
            String latestLogDateString = dateFormatter.format(latestLogInstance.getStartDate());
            Label logTitle = new Label(latestLogInstance.getLogTitle());
            Label logDate = new Label(latestLogDateString);
            int partnerId = latestLogInstance.getBefriendeeId();
            String partnerName = datastore.getPersonStore().getNameFromID(partnerId).fullName;
            Label logPartner = new Label("Befriendee: " + partnerName);
            logTitle.setStyle(style);
            logDate.setStyle(style);
            logPartner.setStyle(style);
            latestLog.getChildren().addAll(logTitle, logDate, logPartner);
        } else {
            Label noLog = new Label("No logs currently in Elder Scrolls");
            noLog.setStyle(style);
            latestLog.setStyle(noLogStyle);
            latestLog.getChildren().add(noLog);
        }
    }
}
