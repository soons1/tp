package scrolls.elder.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static scrolls.elder.logic.commands.CommandTestUtil.assertCommandSuccess;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import scrolls.elder.logic.Messages;
import scrolls.elder.model.Model;
import scrolls.elder.model.ModelManager;
import scrolls.elder.model.ReadOnlyPersonStore;
import scrolls.elder.model.UserPrefs;
import scrolls.elder.model.person.NameContainsKeywordsPredicate;
import scrolls.elder.model.person.Person;
import scrolls.elder.model.person.TagListContainsTagsPredicate;
import scrolls.elder.model.tag.Tag;
import scrolls.elder.testutil.TypicalDatastore;
import scrolls.elder.testutil.TypicalPersons;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private final Model model = new ModelManager(TypicalDatastore.getTypicalDatastore(), new UserPrefs());
    private final ReadOnlyPersonStore personStore = model.getDatastore().getPersonStore();
    private final Model expectedModel = new ModelManager(TypicalDatastore.getTypicalDatastore(), new UserPrefs());
    private final ReadOnlyPersonStore expectedPersonStore = expectedModel.getDatastore().getPersonStore();
    private final NameContainsKeywordsPredicate emptyNamePredicate =
            new NameContainsKeywordsPredicate(Collections.emptyList());
    private final TagListContainsTagsPredicate emptyTagPredicate =
            new TagListContainsTagsPredicate(Collections.emptySet());


    @Test
    public void equals() {
        NameContainsKeywordsPredicate namePredicate1 =
                new NameContainsKeywordsPredicate(Collections.singletonList("first"));
        NameContainsKeywordsPredicate namePredicate2 =
                new NameContainsKeywordsPredicate(Collections.singletonList("second"));
        TagListContainsTagsPredicate tagPredicate1 = prepareTagPredicate("friends");
        TagListContainsTagsPredicate tagPredicate2 = prepareTagPredicate("owesMoney");

        FindCommand findFirstCommand =
                new FindCommand(namePredicate1, tagPredicate1, true, true, true, true);

        FindCommand findSecondCommand =
                new FindCommand(namePredicate2, tagPredicate1, true, true, true, true);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values and same search -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(namePredicate1, tagPredicate1,
                true, true, true, true);

        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // same values but different search -> returns false
        FindCommand findFirstCommandCopy2 = new FindCommand(namePredicate1, tagPredicate1,
                false, true, true, true);
        assertFalse(findFirstCommand.equals(findFirstCommandCopy2));

        // same values but different search -> returns false
        FindCommand findFirstCommandCopy3 = new FindCommand(namePredicate1, tagPredicate1,
                true, false, true, true);
        assertFalse(findFirstCommand.equals(findFirstCommandCopy3));

        // same values, same search, different tag -> returns false
        FindCommand findFirstCommandCopy4 = new FindCommand(namePredicate1, tagPredicate2,
                true, true, true, true);
        assertFalse(findFirstCommand.equals(findFirstCommandCopy4));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    // If no name predicates and no tag predicates, then all persons should be found.
    // This is opposed to original AB3 implementation, where no one would be found.
    @Test
    public void execute_nameKeywordsTagKeywords_peopleFound() {
        String expectedMessage = String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, 7);

        // Find within both volunteer and befriendee
        FindCommand commandAll =
                new FindCommand(emptyNamePredicate, emptyTagPredicate, true, true, true, true);
        expectedPersonStore.updateFilteredPersonList(emptyNamePredicate);
        assertCommandSuccess(commandAll, model, expectedMessage, expectedModel);
        assertEquals(TypicalPersons.getTypicalPersons(), personStore.getFilteredPersonList());

        // Find within volunteer only
        FindCommand commandSearchVolunteer =
                new FindCommand(emptyNamePredicate, emptyTagPredicate, true, false, true, true);
        expectedMessage = String.format(
                Messages.MESSAGE_PERSONS_LISTED_OVERVIEW_WITH_ROLE, 4, CommandTestUtil.VALID_ROLE_VOLUNTEER);
        expectedPersonStore.updateFilteredVolunteerList(emptyNamePredicate);
        assertCommandSuccess(commandSearchVolunteer, model, expectedMessage, expectedModel);
        assertEquals(TypicalPersons.getTypicalVolunteerPersons(), personStore.getFilteredVolunteerList());

        // Find within befriendee only
        FindCommand commandSearchBefriendee =
                new FindCommand(emptyNamePredicate, emptyTagPredicate, false, true, true, true);
        expectedMessage = String.format(
                Messages.MESSAGE_PERSONS_LISTED_OVERVIEW_WITH_ROLE, 3, CommandTestUtil.VALID_ROLE_BEFRIENDEE);
        expectedPersonStore.updateFilteredBefriendeeList(emptyNamePredicate);
        assertCommandSuccess(commandSearchBefriendee, model, expectedMessage, expectedModel);
        assertEquals(TypicalPersons.getTypicalBefriendeePersons(), personStore.getFilteredBefriendeeList());

    }

    @Test
    public void execute_multipleKeywords_multiplePersonsFound() {
        String expectedMessage = String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        NameContainsKeywordsPredicate namePredicate = prepareNamePredicate("Kurz Elle Kunz");
        FindCommand command = new FindCommand(namePredicate, emptyTagPredicate, true, true, true, true);

        expectedPersonStore.updateFilteredPersonList(namePredicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(TypicalPersons.CARL, TypicalPersons.ELLE, TypicalPersons.FIONA),
                personStore.getFilteredPersonList());

        // Find within volunteer only
        String expectedMessageV = String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW_WITH_ROLE, 1, "volunteer");
        command = new FindCommand(namePredicate, emptyTagPredicate, true, false, true, true);

        expectedPersonStore.updateFilteredVolunteerList(namePredicate);
        assertCommandSuccess(command, model, expectedMessageV, expectedModel);
        assertEquals(Collections.singletonList(TypicalPersons.CARL), personStore.getFilteredVolunteerList());

        // Find within befriendee only
        String expectedMessageB = String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW_WITH_ROLE, 2, "befriendee");
        command = new FindCommand(namePredicate, emptyTagPredicate, false, true, true, true);
        expectedPersonStore.updateFilteredBefriendeeList(namePredicate);
        assertCommandSuccess(command, model, expectedMessageB, expectedModel);
        assertEquals(Arrays.asList(TypicalPersons.ELLE, TypicalPersons.FIONA), personStore.getFilteredBefriendeeList());

    }



    @Test
    public void execute_singleTag_multiplePersons() {
        // All Persons
        String expectedMessage = String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        TagListContainsTagsPredicate tagPredicate = prepareTagPredicate("friends");
        FindCommand command = new FindCommand(emptyNamePredicate, tagPredicate, true, true, true, true);
        expectedPersonStore.updateFilteredPersonList(tagPredicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(TypicalPersons.ALICE, TypicalPersons.BENSON, TypicalPersons.DANIEL),
                personStore.getFilteredPersonList());

        // Voluteers
        String expectedVMessage = String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW_WITH_ROLE, 3, "volunteer");
        FindCommand commandV = new FindCommand(emptyNamePredicate, tagPredicate, true, false, true, true);
        expectedPersonStore.updateFilteredVolunteerList(tagPredicate);
        assertCommandSuccess(commandV, model, expectedVMessage, expectedModel);
        assertEquals(Arrays.asList(TypicalPersons.ALICE, TypicalPersons.BENSON, TypicalPersons.DANIEL),
                personStore.getFilteredVolunteerList());

        // Befriendees
        String expectedMessageB = String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW_WITH_ROLE, 1, "befriendee");
        TagListContainsTagsPredicate tagPredicateB = prepareTagPredicate("exConvict");
        FindCommand commandB = new FindCommand(emptyNamePredicate, tagPredicateB,
                false, true, true, true);
        expectedPersonStore.updateFilteredBefriendeeList(tagPredicate);
        assertCommandSuccess(commandB, model, expectedMessageB, expectedModel);
        assertEquals(Collections.singletonList(TypicalPersons.GEORGE), personStore.getFilteredBefriendeeList());
    }


    @Test
    public void execute_singleTag_multiplePersonsFoundBefriendee() {
        String expectedMessage = String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW_WITH_ROLE, 1, "befriendee");
        TagListContainsTagsPredicate tagPredicate = prepareTagPredicate("exConvict");

        FindCommand command = new FindCommand(emptyNamePredicate, tagPredicate, false, true, true, true);
        expectedPersonStore.updateFilteredBefriendeeList(tagPredicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(TypicalPersons.GEORGE), personStore.getFilteredBefriendeeList());
    }

    @Test
    public void execute_multipleTag_mutiplePersonsFound() {
        String expectedMessage = String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, 4);
        TagListContainsTagsPredicate tagPredicate = prepareTagPredicate("friends exConvict");
        FindCommand command = new FindCommand(emptyNamePredicate, tagPredicate, true, true, true, true);


        expectedPersonStore.updateFilteredPersonList(tagPredicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(
                TypicalPersons.ALICE, TypicalPersons.BENSON, TypicalPersons.DANIEL, TypicalPersons.GEORGE),
                personStore.getFilteredPersonList());
    }

    @Test
    public void execute_pairedUnpairedFlag_multiplePersonsFound() {
        String expectedMessagePaired = String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, 2);
        FindCommand command = new FindCommand(emptyNamePredicate, emptyTagPredicate,
                true, true, true, false);
        expectedPersonStore.updateFilteredPersonList(Person::isPaired);
        assertCommandSuccess(command, model, expectedMessagePaired, expectedModel);
        assertEquals(TypicalPersons.getPairedPersons(), personStore.getFilteredPersonList());

        String expectedMessageUnpaired = String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, 5);
        command = new FindCommand(emptyNamePredicate, emptyTagPredicate,
                true, true, false, true);
        expectedPersonStore.updateFilteredPersonList(Person -> !Person.isPaired());
        assertCommandSuccess(command, model, expectedMessageUnpaired, expectedModel);
        assertEquals(TypicalPersons.getUnpairedPersons(), personStore.getFilteredPersonList());
    }

    @Test
    public void execute_pairedUnpairedFlagVolunteerBefriendee_multiplePersonsFound() {
        // Volunteer
        String expectedMessagePairedV =
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW_WITH_ROLE, 1, "volunteer");
        FindCommand command = new FindCommand(emptyNamePredicate, emptyTagPredicate,
                true, false, true, false);
        expectedPersonStore.updateFilteredVolunteerList(Person::isPaired);
        assertCommandSuccess(command, model, expectedMessagePairedV, expectedModel);
        assertEquals(Collections.singletonList(TypicalPersons.ALICE), personStore.getFilteredVolunteerList());

        String expectedMessageUnpairedV =
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW_WITH_ROLE, 3, "volunteer");
        command = new FindCommand(emptyNamePredicate, emptyTagPredicate,
                true, false, false, true);
        expectedPersonStore.updateFilteredPersonList(Person -> !Person.isPaired());
        assertCommandSuccess(command, model, expectedMessageUnpairedV, expectedModel);
        assertEquals(Arrays.asList(TypicalPersons.BENSON, TypicalPersons.CARL, TypicalPersons.DANIEL),
                personStore.getFilteredVolunteerList());

        // Befriendee
        String expectedMessagePairedB =
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW_WITH_ROLE, 1, "befriendee");
        command = new FindCommand(emptyNamePredicate, emptyTagPredicate,
                false, true, true, false);
        expectedPersonStore.updateFilteredBefriendeeList(Person::isPaired);
        assertCommandSuccess(command, model, expectedMessagePairedB, expectedModel);
        assertEquals(Collections.singletonList(TypicalPersons.ELLE), personStore.getFilteredBefriendeeList());

        String expectedMessageUnpairedB =
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW_WITH_ROLE, 2, "befriendee");
        command = new FindCommand(emptyNamePredicate, emptyTagPredicate,
                false, true, false, true);
        expectedPersonStore.updateFilteredBefriendeeList(Person -> !Person.isPaired());
        assertCommandSuccess(command, model, expectedMessageUnpairedB, expectedModel);
        assertEquals(Arrays.asList(TypicalPersons.FIONA, TypicalPersons.GEORGE),
                personStore.getFilteredBefriendeeList());
    }



    @Test
    public void toStringMethod() {

        NameContainsKeywordsPredicate namePredicate = new NameContainsKeywordsPredicate(Arrays.asList("keyword"));
        TagListContainsTagsPredicate tagPredicate = prepareTagPredicate(" ");
        FindCommand findCommand = new FindCommand(namePredicate, tagPredicate,
                true, true, true, true);
        String expected = FindCommand.class.getCanonicalName()
                + "{"
                + "namePredicate=" + namePredicate + ", "
                + "tagPredicate=" + tagPredicate + ", "
                + "isSearchingVolunteer=true, "
                + "isSearchingBefriendee=true, "
                + "isSearchingPaired=true, "
                + "isSearchingUnpaired=true"
                + "}";
        assertEquals(expected, findCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsPredicate}.
     */
    private NameContainsKeywordsPredicate prepareNamePredicate(String userInput) {
        return new NameContainsKeywordsPredicate(Arrays.asList(userInput.split("\\s+")));
    }

    private TagListContainsTagsPredicate prepareTagPredicate(String userInput) {
        Set<Tag> tagList = new HashSet<>();
        String[] tagsArray = userInput.split("\\s+");

        for (String tag : tagsArray) {
            tagList.add(new Tag(tag));
        }

        return new TagListContainsTagsPredicate(tagList);
    }

}
