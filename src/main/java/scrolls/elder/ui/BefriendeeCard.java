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
import scrolls.elder.model.person.Person;

/**
 * An UI component that displays information of a {@code Person}.
 */
public class BefriendeeCard extends UiPart<Region> {

    private static final String FXML = "BefriendeeListCard.fxml";
    private static final String SMALL_LABEL = "list-cell-small-label";
    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person befriendee;
    private String dateFormatPattern = "dd MMM yyyy";
    private DateFormat dateFormatter;
    private ReadOnlyDatastore datastore;

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
    private VBox latestLog;

    /**
     * Creates a {@code PersonCode} with the given {@code Person} and index to display.
     */
    public BefriendeeCard(Person person, int displayedIndex, ReadOnlyDatastore datastore) {
        super(FXML);
        this.befriendee = person;
        dateFormatter = new SimpleDateFormat(dateFormatPattern);
        this.datastore = datastore;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        address.setText(person.getAddress().value);
        email.setText(person.getEmail().value);
        pairedWith.setText(person.getPairedWithName().map(p -> "Paired with: " + p.fullName).orElse("Not paired"));
        person.getTags().stream()
            .sorted(Comparator.comparing(tag -> tag.tagName))
            .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));

        // If latest log is present, add new log summary card, else add no logs
        if (befriendee.isLatestLogPresent()) {
            int latestLogId = befriendee.getLatestLogId().get();
            Log latestLogInstance = datastore.getLogStore().getLogById(latestLogId);
            String latestLogDateString = dateFormatter.format(latestLogInstance.getStartDate());
            Label logTitle = new Label(latestLogInstance.getLogTitle());
            Label logDate = new Label(latestLogDateString);
            int partnerId = latestLogInstance.getVolunteerId();
            String partnerName = datastore.getPersonStore().getNameFromID(partnerId).fullName;
            Label logPartner = new Label("Volunteer: " + partnerName);
            logTitle.getStyleClass().add(SMALL_LABEL);
            logDate.getStyleClass().add(SMALL_LABEL);
            logPartner.getStyleClass().add(SMALL_LABEL);
            latestLog.getChildren().addAll(logTitle, logDate, logPartner);
        } else {
            Label noLog = new Label("No logs currently in Elder Scrolls");
            noLog.getStyleClass().add(SMALL_LABEL);
            latestLog.getStyleClass().remove("latest-log-card");
            latestLog.getStyleClass().add("latest-log-card-disabled");
            latestLog.getChildren().add(noLog);
        }
    }
}
