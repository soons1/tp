package scrolls.elder.model.person;

import java.util.Set;
import java.util.function.Predicate;

import scrolls.elder.commons.util.ToStringBuilder;
import scrolls.elder.model.tag.Tag;

/**
 * Tests that a {@code Person}'s {@code Tags} matches any of the tags given.
 */
public class TagListContainsTagsPredicate implements Predicate<Person> {

    private final Set<Tag> tagList;

    public TagListContainsTagsPredicate(Set<Tag> tagList) {
        this.tagList = tagList;
    }

    public boolean isEmpty() {
        return tagList.isEmpty();
    }

    @Override
    public boolean test(Person person) {
        return tagList.stream()
                .anyMatch(tag -> person.getTags().contains(tag));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof TagListContainsTagsPredicate)) {
            return false;
        }

        TagListContainsTagsPredicate otherTagListContainsTagsPredicate = (TagListContainsTagsPredicate) other;
        return tagList.equals(otherTagListContainsTagsPredicate.tagList);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("tags", tagList).toString();
    }
}


