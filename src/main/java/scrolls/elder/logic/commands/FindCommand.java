package scrolls.elder.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import scrolls.elder.commons.util.ToStringBuilder;
import scrolls.elder.logic.Messages;
import scrolls.elder.model.Model;
import scrolls.elder.model.PersonStore;
import scrolls.elder.model.person.NameContainsKeywordsPredicate;
import scrolls.elder.model.person.Person;
import scrolls.elder.model.person.TagListContainsTagsPredicate;


/**
 * Finds and lists all persons in address book whose name contains any of the argument keywords.
 * Keyword matching is case-insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD_FIND = "find";
    public static final String COMMAND_WORD_SEARCH = "search";

    public static final String MESSAGE_USAGE = COMMAND_WORD_FIND + ": Finds all persons whose names contain any of "
            + "the specified keywords (case-insensitive), displays them in the respective lists with index numbers.\n"
            + "Parameters: [r/ROLE] [t/TAG] [--paired]/[--unpaired] KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD_FIND + " alex david roy --paired"
            + "\nExample: " + COMMAND_WORD_FIND + " alex r/volunteer";

    private final NameContainsKeywordsPredicate namePredicate;

    private final TagListContainsTagsPredicate tagPredicate;
    private final Boolean isSearchingVolunteer;
    private final Boolean isSearchingBefriendee;
    private final Boolean isSearchingNamePredicate;
    private final Boolean isSearchingTagPredicate;
    private final Boolean isSearchingPaired;
    private final Boolean isSearchingUnpaired;


    /**
     * Creates a FindCommand to find the specified {@code NameContainsKeywordsPredicate}
     */
    public FindCommand(NameContainsKeywordsPredicate namePredicate, TagListContainsTagsPredicate tagPredicate,
                       Boolean isSearchingVolunteer, Boolean isSearchingBefriendee,
                       Boolean isSearchingPaired, Boolean isSearchingUnpaired) {
        this.namePredicate = namePredicate;
        this.isSearchingNamePredicate = !namePredicate.isEmpty();
        this.tagPredicate = tagPredicate;
        this.isSearchingTagPredicate = !tagPredicate.isEmpty();
        this.isSearchingBefriendee = isSearchingBefriendee;
        this.isSearchingVolunteer = isSearchingVolunteer;

        this.isSearchingPaired = isSearchingPaired;
        this.isSearchingUnpaired = isSearchingUnpaired;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        PersonStore store = model.getMutableDatastore().getMutablePersonStore();
        Predicate<Person> combinedPredicate = getCombinedPredicate();

        assert (isSearchingVolunteer || isSearchingBefriendee)
                : "At least one or both isSearchingVolunteer and isSearchingBefriendee should be true.";

        if (isSearchingVolunteer && isSearchingBefriendee) {
            return searchAllPersons(store, combinedPredicate);

        } else if (isSearchingVolunteer) {
            return searchVolunteerOnly(store, combinedPredicate);

        } else {
            return searchBefriendeeOnly(store, combinedPredicate);
        }

    }

    private Predicate<Person> getCombinedPredicate() {
        List<Predicate<Person>> predicates = new ArrayList<>();

        if (isSearchingPaired && !isSearchingUnpaired) {
            predicates.add(Person::isPaired);
        } else if (isSearchingUnpaired && !isSearchingPaired) {
            predicates.add(Person -> !Person.isPaired());
        }

        if (isSearchingNamePredicate) {
            predicates.add(namePredicate);
        }
        if (isSearchingTagPredicate) {
            predicates.add(tagPredicate);
        }

        return predicates.stream().reduce(Predicate::and).orElse(person -> true);
    }

    private CommandResult searchAllPersons(PersonStore store, Predicate<Person> combinedPredicate) {
        store.updateFilteredPersonList(combinedPredicate);

        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, store.getFilteredPersonList().size()));
    }

    private CommandResult searchVolunteerOnly(PersonStore store, Predicate<Person> combinedPredicate) {
        store.updateFilteredVolunteerList(combinedPredicate);

        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW_WITH_ROLE,
                        store.getFilteredVolunteerList().size(),
                        "volunteer"));
    }

    private CommandResult searchBefriendeeOnly(PersonStore store, Predicate<Person> combinedPredicate) {
        store.updateFilteredBefriendeeList(combinedPredicate);

        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW_WITH_ROLE,
                        store.getFilteredBefriendeeList().size(),
                        "befriendee"));
    }


    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand)) {
            return false;
        }

        FindCommand otherFindCommand = (FindCommand) other;
        return namePredicate.equals(otherFindCommand.namePredicate)
                && tagPredicate.equals(otherFindCommand.tagPredicate)
                && isSearchingVolunteer.equals(otherFindCommand.isSearchingVolunteer)
                && isSearchingBefriendee.equals(otherFindCommand.isSearchingBefriendee);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("namePredicate", namePredicate)
                .add("tagPredicate", tagPredicate)
                .add("isSearchingVolunteer", isSearchingVolunteer)
                .add("isSearchingBefriendee", isSearchingBefriendee)
                .add("isSearchingPaired", isSearchingPaired)
                .add("isSearchingUnpaired", isSearchingUnpaired)
                .toString();
    }
}
