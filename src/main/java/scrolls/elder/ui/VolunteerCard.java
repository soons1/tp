package scrolls.elder.ui;

import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import scrolls.elder.model.person.Person;
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

    /**
     * Creates a {@code PersonCode} with the given {@code Person} and index to display.
     */
    public VolunteerCard(Volunteer vol, int displayedIndex) {
        super(FXML);
        this.vol = vol;
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
    }
}
