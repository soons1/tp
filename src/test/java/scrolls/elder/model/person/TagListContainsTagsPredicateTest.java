package scrolls.elder.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import scrolls.elder.model.tag.Tag;

public class TagListContainsTagsPredicateTest {

    @Test
    public void equals() {
        TagListContainsTagsPredicate firstTagPredicate = prepareTagPredicate("first");
        TagListContainsTagsPredicate secondTagPredicate = prepareTagPredicate("first second");

        // same object -> returns true
        assertTrue(firstTagPredicate.equals(firstTagPredicate));

        // same values -> returns true
        TagListContainsTagsPredicate firstPredicateCopy = prepareTagPredicate("first");
        assertTrue(firstTagPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstTagPredicate.equals(1));

        // null -> returns false
        assertFalse(firstTagPredicate.equals(null));

        // different tags -> returns false
        assertFalse(firstTagPredicate.equals(secondTagPredicate));
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
