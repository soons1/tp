package scrolls.elder.ui;

import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import scrolls.elder.commons.core.LogsCenter;
import scrolls.elder.model.person.Person;
import scrolls.elder.model.person.Volunteer;

/**
 * Panel containing the list of persons.
 */
public class PersonListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(PersonListPanel.class);

    @FXML
    private ListView<Person> personListView;

    /**
     * Creates a {@code PersonListPanel} with the given {@code ObservableList}.
     */
    public PersonListPanel(ObservableList<Person> personList) {
        super(FXML);
        personListView.setItems(personList);
        personListView.setCellFactory(listView -> new PersonListViewCell());
    }

    /**
     * Custom {@code ListCell} that displays the graphics of a {@code Person} using a {@code PersonCard}.
     */
    class PersonListViewCell extends ListCell<Person> {
        @Override
        protected void updateItem(Person person, boolean empty) {
            super.updateItem(person, empty);

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
            } else {
                if (person.isVolunteer()) {
                    // if statement checks if person is a volunteer, hence safe to cast to type Volunteer
                    @SuppressWarnings("unchecked")
                    Volunteer vol = (Volunteer) person;
                    setGraphic(new VolunteerCard(vol, getIndex() + 1).getRoot());
                } else {
                    setGraphic(new BefriendeeCard(person, getIndex() + 1).getRoot());
                }
            }
        }
    }

}
