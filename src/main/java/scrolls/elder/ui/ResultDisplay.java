package scrolls.elder.ui;

import static java.util.Objects.requireNonNull;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;

/**
 * A ui for the status bar that is displayed at the header of the application.
 */
public class ResultDisplay extends UiPart<Region> {

    private static final String FXML = "ResultDisplay.fxml";

    @FXML
    private TextArea resultDisplay;

    public ResultDisplay() {
        super(FXML);
    }

    public void setFeedbackToUser(String feedbackToUser, boolean isErrorMsg) {
        requireNonNull(feedbackToUser);
        if (isErrorMsg) {
            // Remove the error style class if it is present, so as to not duplicate the error style class
            resultDisplay.getStyleClass().remove("result-display-error");
            resultDisplay.getStyleClass().add("result-display-error");
        } else {
            resultDisplay.getStyleClass().remove("result-display-error");
        }

        resultDisplay.setText(feedbackToUser);
    }

}
