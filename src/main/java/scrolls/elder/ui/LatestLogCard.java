package scrolls.elder.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import scrolls.elder.model.person.Name;

/**
 * A UI component that displays information of a {@code LatestLog}.
 */
public class LatestLogCard extends UiPart<Region> {
    private static final String FXML = "LatestLogCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    private String dateFormatPattern = "dd MMM yyyy";
    private DateFormat dateFormatter;

    @FXML
    private Label title;
    @FXML
    private Label date;
    @FXML
    private Label partner;

    /**
     * Creates a {@code PersonCode} with the given {@code Person} and index to display.
     */
    public LatestLogCard(String titleString, Date startDate, Name partnerName) {
        super(FXML);

        dateFormatter = new SimpleDateFormat(dateFormatPattern);

        title.setText(titleString);
        date.setText(dateFormatter.format(startDate));
        partner.setText("Pairee involved: " + partnerName.fullName);
    }
}
