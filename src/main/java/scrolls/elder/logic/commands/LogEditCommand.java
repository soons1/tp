package scrolls.elder.logic.commands;

import static java.util.Objects.requireNonNull;
import static scrolls.elder.logic.parser.CliSyntax.PREFIX_DURATION;
import static scrolls.elder.logic.parser.CliSyntax.PREFIX_REMARKS;
import static scrolls.elder.logic.parser.CliSyntax.PREFIX_START;
import static scrolls.elder.logic.parser.CliSyntax.PREFIX_TITLE;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import scrolls.elder.commons.core.index.Index;
import scrolls.elder.commons.util.CollectionUtil;
import scrolls.elder.commons.util.ToStringBuilder;
import scrolls.elder.logic.Messages;
import scrolls.elder.logic.commands.exceptions.CommandException;
import scrolls.elder.model.LogStore;
import scrolls.elder.model.Model;
import scrolls.elder.model.PersonStore;
import scrolls.elder.model.log.Log;
import scrolls.elder.model.person.Address;
import scrolls.elder.model.person.Email;
import scrolls.elder.model.person.Name;
import scrolls.elder.model.person.Person;
import scrolls.elder.model.person.PersonFactory;
import scrolls.elder.model.person.Phone;
import scrolls.elder.model.person.Role;
import scrolls.elder.model.tag.Tag;


/**
 * Edits the details of an existing log in the address book.
 */
public class LogEditCommand extends Command {

    public static final String COMMAND_WORD = "logedit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the log specified "
            + "by the log index number used in the displayed log list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: LOG_INDEX (must be a positive integer) "
            + "[" + PREFIX_TITLE + "TITLE] "
            + "[" + PREFIX_START + "START_DATE (yyyy-MM-dd)] "
            + "[" + PREFIX_DURATION + "DURATION (in hours)] "
            + "[" + PREFIX_REMARKS + "REMARKS]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_TITLE + "Icebreaker session "
            + PREFIX_START + "2021-03-01 "
            + PREFIX_DURATION + "2 "
            + PREFIX_REMARKS + "was a good session ";

    public static final String MESSAGE_EDIT_LOG_SUCCESS = "Edited Log successfully: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_NEGATIVE_DURATION = "Duration cannot be negative.";

    private final Index index;
    private final EditLogDescriptor editLogDescriptor;

    /**
     * @param index             of the log in the filtered log list to edit
     * @param editLogDescriptor details to edit the log with
     */
    public LogEditCommand(Index index, EditLogDescriptor editLogDescriptor) {
        requireNonNull(index);
        requireNonNull(editLogDescriptor);

        this.index = index;
        this.editLogDescriptor = new EditLogDescriptor(editLogDescriptor);
    }

    /**
     * Creates and returns a {@code Log} with the details of {@code logToEdit}
     * edited with {@code editLogDescriptor}.
     */
    private static Log createEditedLog(Log logToEdit, EditLogDescriptor editLogDescriptor) {
        assert logToEdit != null;

        int volunteerId;
        int befriendeeId;

        if (editLogDescriptor.getVolunteerIndex().isPresent()) {
            volunteerId = editLogDescriptor.getVolunteerIndex().get().getZeroBased();
        } else {
            volunteerId = logToEdit.getVolunteerId();
        }

        if (editLogDescriptor.getBefriendeeIndex().isPresent()) {
            befriendeeId = editLogDescriptor.getBefriendeeIndex().get().getZeroBased();
        } else {
            befriendeeId = logToEdit.getBefriendeeId();
        }

        String title = editLogDescriptor.getTitle().orElse(logToEdit.getLogTitle());
        Integer duration = editLogDescriptor.getDuration().orElse(logToEdit.getDuration());
        Date startDate = editLogDescriptor.getStartDate().orElse(logToEdit.getStartDate());
        String remarks = editLogDescriptor.getRemarks().orElse(logToEdit.getRemarks());

        return new Log(logToEdit.getLogId(), title, volunteerId, befriendeeId, duration, startDate, remarks);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        PersonStore personStore = model.getMutableDatastore().getMutablePersonStore();
        LogStore store = model.getMutableDatastore().getMutableLogStore();

        List<Log> lastShownList = store.getLogList();
        List<Person> lastShownPList = personStore.getPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_LOG_DISPLAYED_INDEX);
        }

        Log logToEdit = lastShownList.get(index.getZeroBased());
        Log editedLog = createEditedLog(logToEdit, editLogDescriptor);

        if (!logToEdit.areValidIds(model.getDatastore(), editedLog.getVolunteerId(), editedLog.getBefriendeeId())) {
            throw new CommandException(Log.MESSAGE_INVALID_ID);
        }

        if (editedLog.getDuration() < 0) {
            throw new CommandException(MESSAGE_NEGATIVE_DURATION);
        }

        int durationDiff = editedLog.getDuration() - logToEdit.getDuration();
        if (durationDiff != 0) {
            Person befriendee = lastShownPList.get(logToEdit.getBefriendeeId());
            Person volunteer = lastShownPList.get(logToEdit.getVolunteerId());
            Person updatedBefriendee = createPersonWithTimeServed(befriendee, durationDiff);
            Person updatedVolunteer = createPersonWithTimeServed(volunteer, durationDiff);
            personStore.setPerson(befriendee, updatedBefriendee);
            personStore.setPerson(volunteer, updatedVolunteer);
            personStore.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
        }

        store.setLog(editedLog);
        model.commitDatastore();
        return new CommandResult(String.format(MESSAGE_EDIT_LOG_SUCCESS, Messages.formatLog(editedLog)));
    }

    private Person createPersonWithTimeServed(Person p, int duration) {
        assert p != null;

        Name name = p.getName();
        Phone phone = p.getPhone();
        Email email = p.getEmail();
        Address address = p.getAddress();
        Set<Tag> tags = p.getTags();
        Role role = p.getRole();
        Optional<Name> pairedWithName = p.getPairedWithName();
        Optional<Integer> pairedWithId = p.getPairedWithId();
        int updatedTimeServed = p.getTimeServed() + duration;

        return PersonFactory.withIdFromParams(p.getPersonId(), name, phone, email, address, role, tags, pairedWithName,
                pairedWithId, updatedTimeServed);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof LogEditCommand)) {
            return false;
        }

        LogEditCommand otherLogEditCommand = (LogEditCommand) other;
        return index.equals(otherLogEditCommand.index)
                && editLogDescriptor.equals(otherLogEditCommand.editLogDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editLogDescriptor", editLogDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the log with. Each non-empty field value will replace the
     * corresponding field value of the log.
     */
    public static class EditLogDescriptor {
        private String title;
        private Index volunteerIndex;
        private Index befriendeeIndex;
        private Integer duration;
        private Date startDate;
        private String remarks;

        public EditLogDescriptor() {
        }

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditLogDescriptor(EditLogDescriptor toCopy) {
            setTitle(toCopy.title);
            setVolunteerIndex(toCopy.volunteerIndex);
            setBefriendeeIndex(toCopy.befriendeeIndex);
            setDuration(toCopy.duration);
            setStartDate(toCopy.startDate);
            setRemarks(toCopy.remarks);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(title, volunteerIndex, befriendeeIndex, duration, startDate, remarks);
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Optional<String> getTitle() {
            return Optional.ofNullable(title);
        }

        public void setVolunteerIndex(Index volunteerIndex) {
            this.volunteerIndex = volunteerIndex;
        }

        public Optional<Index> getVolunteerIndex() {
            return Optional.ofNullable(volunteerIndex);
        }

        public void setBefriendeeIndex(Index befriendeeIndex) {
            this.befriendeeIndex = befriendeeIndex;
        }

        public Optional<Index> getBefriendeeIndex() {
            return Optional.ofNullable(befriendeeIndex);
        }

        public void setDuration(Integer duration) {
            this.duration = duration;
        }

        public Optional<Integer> getDuration() {
            return Optional.ofNullable(duration);
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        public Optional<Date> getStartDate() {
            return Optional.ofNullable(startDate);
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public Optional<String> getRemarks() {
            return Optional.ofNullable(remarks);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            if (!(other instanceof EditLogDescriptor)) {
                return false;
            }

            EditLogDescriptor otherEditLogDescriptor = (EditLogDescriptor) other;
            return Objects.equals(title, otherEditLogDescriptor.title)
                    && Objects.equals(volunteerIndex, otherEditLogDescriptor.volunteerIndex)
                    && Objects.equals(befriendeeIndex, otherEditLogDescriptor.befriendeeIndex)
                    && Objects.equals(duration, otherEditLogDescriptor.duration)
                    && Objects.equals(startDate, otherEditLogDescriptor.startDate)
                    && Objects.equals(remarks, otherEditLogDescriptor.remarks);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("title", title)
                    .add("volunteerIndex", volunteerIndex)
                    .add("befriendeeIndex", befriendeeIndex)
                    .add("duration", duration)
                    .add("startDate", startDate)
                    .add("remarks", remarks)
                    .toString();
        }
    }
}
