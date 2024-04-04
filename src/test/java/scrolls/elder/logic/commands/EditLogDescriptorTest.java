package scrolls.elder.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import scrolls.elder.testutil.EditLogDescriptorBuilder;
import scrolls.elder.testutil.TypicalIndexes;

public class EditLogDescriptorTest {

    @Test
    public void equals() {

        // same values -> returns true
        LogEditCommand.EditLogDescriptor descriptor1 =
                new EditLogDescriptorBuilder().withTitle("hello").withBefriendeeIndex(TypicalIndexes.INDEX_FIRST_PERSON)
                        .withVolunteerIndex(TypicalIndexes.INDEX_FIRST_PERSON)
                        .withDuration(2).withRemarks("test").build();
        LogEditCommand.EditLogDescriptor descriptorWithSameValues = new EditLogDescriptorBuilder(descriptor1).build();
        assertTrue(descriptor1.equals(descriptorWithSameValues));

        // same object -> returns true
        assertTrue(descriptor1.equals(descriptor1));

        // null -> returns false
        assertFalse(descriptor1.equals(null));

        // different types -> returns false
        assertFalse(descriptor1.equals(5));

        LogEditCommand.EditLogDescriptor descriptor2 =
                new EditLogDescriptorBuilder().withTitle("bye").withBefriendeeIndex(TypicalIndexes.INDEX_SECOND_PERSON)
                        .withVolunteerIndex(TypicalIndexes.INDEX_FIRST_PERSON).withDuration(2)
                        .withRemarks("test1").build();

        // different values -> returns false
        assertFalse(descriptor1.equals(descriptor2));

        // different title -> returns false
        LogEditCommand.EditLogDescriptor descriptorWithDiffTitle = new EditLogDescriptorBuilder(descriptor1)
                .withTitle("what").build();
        assertFalse(descriptor1.equals(descriptorWithDiffTitle));

        // different remark -> returns false
        LogEditCommand.EditLogDescriptor descriptorWithDiffRemark = new EditLogDescriptorBuilder(descriptor1)
                .withRemarks("huh").build();
        assertFalse(descriptor1.equals(descriptorWithDiffRemark));

        // different duration -> returns false
        LogEditCommand.EditLogDescriptor descriptorWithDiffDuration = new EditLogDescriptorBuilder(descriptor1)
                .withDuration(6).build();
        assertFalse(descriptor1.equals(descriptorWithDiffDuration));

        // different address -> returns false
        LogEditCommand.EditLogDescriptor descriptorWithDiffBIndex = new EditLogDescriptorBuilder(descriptor1)
                .withBefriendeeIndex(TypicalIndexes.INDEX_FOURTH_PERSON).build();
        assertFalse(descriptor1.equals(descriptorWithDiffBIndex));

        // different tags -> returns false
        LogEditCommand.EditLogDescriptor descriptorWithDiffVIndex = new EditLogDescriptorBuilder(descriptor1)
                .withVolunteerIndex(TypicalIndexes.INDEX_FOURTH_PERSON).build();
        assertFalse(descriptor1.equals(descriptorWithDiffVIndex));
    }

    @Test
    public void toStringMethod() {
        LogEditCommand.EditLogDescriptor editLogDescriptor = new LogEditCommand.EditLogDescriptor();
        String expected = LogEditCommand.EditLogDescriptor.class.getCanonicalName() + "{title="
                + editLogDescriptor.getTitle().orElse(null) + ", volunteerIndex="
                + editLogDescriptor.getVolunteerIndex().orElse(null) + ", befriendeeIndex="
                + editLogDescriptor.getBefriendeeIndex().orElse(null) + ", duration="
                + editLogDescriptor.getDuration().orElse(null) + ", startDate="
                + editLogDescriptor.getStartDate().orElse(null) + ", remarks="
                + editLogDescriptor.getRemarks().orElse(null) + "}";
        assertEquals(expected, editLogDescriptor.toString());
    }
}
