package scrolls.elder.logic.commands;

import static java.util.Objects.requireNonNull;

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

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all persons whose names contain any of "
            + "the specified keywords (case-insensitive), displays them in the respective lists with index numbers.\n"
            + "Parameters: [r/ROLE] [t/TAG] [--paired]/[--unpaired] KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " alice bob charlie";

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

        assert (isSearchingVolunteer || isSearchingBefriendee)
                : "At least one or both isSearchingVolunteer and isSearchingBefriendee should be true.";

        if (isSearchingVolunteer && isSearchingBefriendee) {
            return searchAllPersons(store);

        } else if (isSearchingVolunteer) {
            return searchVolunteerOnly(store);

        } else {
            return searchBefriendeeOnly(store);
        }

    }

    private CommandResult searchAllPersons(PersonStore store) {
        if (isSearchingPaired && !isSearchingUnpaired) {
            store.updateFilteredPersonList(Person::isPaired);
        } else if (isSearchingUnpaired && !isSearchingPaired) {
            store.updateFilteredPersonList(Person -> !Person.isPaired());
        }

        if (isSearchingNamePredicate) {
            store.updateFilteredPersonList(namePredicate);
        }
        if (isSearchingTagPredicate) {
            store.updateFilteredPersonList(tagPredicate);
        }
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, store.getFilteredPersonList().size()));
    }

    private CommandResult searchVolunteerOnly(PersonStore store) {
        if (isSearchingPaired && !isSearchingUnpaired) {
            store.updateFilteredVolunteerList(Person::isPaired);
        } else if (isSearchingUnpaired && !isSearchingPaired) {
            store.updateFilteredVolunteerList(Person -> !Person.isPaired());
        }

        if (isSearchingNamePredicate) {
            store.updateFilteredVolunteerList(namePredicate);
        }
        if (isSearchingTagPredicate) {
            store.updateFilteredVolunteerList(tagPredicate);
        }
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW_WITH_ROLE,
                        store.getFilteredVolunteerList().size(),
                        "volunteer"));
    }

    private CommandResult searchBefriendeeOnly(PersonStore store) {
        if (isSearchingPaired && !isSearchingUnpaired) {
            store.updateFilteredBefriendeeList(Person::isPaired);
        } else if (isSearchingUnpaired && !isSearchingPaired) {
            store.updateFilteredBefriendeeList(Person -> !Person.isPaired());
        }
        if (isSearchingNamePredicate) {
            store.updateFilteredBefriendeeList(namePredicate);
        }
        if (isSearchingTagPredicate) {
            store.updateFilteredBefriendeeList(tagPredicate);
        }
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
