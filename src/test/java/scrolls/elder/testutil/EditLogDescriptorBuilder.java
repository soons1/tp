package scrolls.elder.testutil;

import java.util.Date;

import scrolls.elder.commons.core.index.Index;
import scrolls.elder.logic.commands.LogEditCommand;
import scrolls.elder.model.log.Log;

/**
 * A utility class to help with building EditLogDescriptor objects.
 */
public class EditLogDescriptorBuilder {

    private LogEditCommand.EditLogDescriptor descriptor;

    public EditLogDescriptorBuilder() {
        descriptor = new LogEditCommand.EditLogDescriptor();
    }

    public EditLogDescriptorBuilder(LogEditCommand.EditLogDescriptor descriptor) {
        this.descriptor = new LogEditCommand.EditLogDescriptor(descriptor);
    }

    /**
     * Returns an {@code EditLogDescriptor} with fields containing {@code log}'s details
     */
    public EditLogDescriptorBuilder(Log log) {
        descriptor = new LogEditCommand.EditLogDescriptor();
        descriptor.setTitle(log.getLogTitle());
        descriptor.setVolunteerIndex(Index.fromZeroBased(log.getVolunteerId()));
        descriptor.setBefriendeeIndex(Index.fromZeroBased(log.getBefriendeeId()));
        descriptor.setDuration(log.getDuration());
        descriptor.setStartDate(log.getStartDate());
        descriptor.setRemarks(log.getRemarks());
    }

    /**
     * Sets the {@code title} of the {@code EditLogDescriptor} that we are building.
     */
    public EditLogDescriptorBuilder withTitle(String title) {
        descriptor.setTitle(title);
        return this;
    }

    /**
     * Sets the {@code volunteerIndex} of the {@code EditLogDescriptor} that we are building.
     */
    public EditLogDescriptorBuilder withVolunteerIndex(Index volunteerIndex) {
        descriptor.setVolunteerIndex(volunteerIndex);
        return this;
    }

    /**
     * Sets the {@code befriendeeIndex} of the {@code EditLogDescriptor} that we are building.
     */
    public EditLogDescriptorBuilder withBefriendeeIndex(Index befriendeeIndex) {
        descriptor.setVolunteerIndex(befriendeeIndex);
        return this;
    }

    /**
     * Sets the {@code duration} of the {@code EditLogDescriptor} that we are building.
     */
    public EditLogDescriptorBuilder withDuration(int duration) {
        descriptor.setDuration(duration);
        return this;
    }

    /**
     * Parses the {@code startDate} into a {@code Set<Tag>} and set it to the {@code EditLogDescriptor}
     * that we are building.
     */
    public EditLogDescriptorBuilder withStartDate(Date startDate) {
        descriptor.setStartDate(startDate);
        return this;
    }

    /**
     * Sets the {@code remark} of the {@code EditLogDescriptor} that we are building.
     */
    public EditLogDescriptorBuilder withRemarks(String remarks) {
        descriptor.setRemarks(remarks);
        return this;
    }

    public LogEditCommand.EditLogDescriptor build() {
        return descriptor;
    }
}
