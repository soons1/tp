package scrolls.elder.logic.commands;

import static java.util.Objects.requireNonNull;
import static scrolls.elder.logic.parser.CliSyntax.PREFIX_DURATION;
import static scrolls.elder.logic.parser.CliSyntax.PREFIX_REMARKS;
import static scrolls.elder.logic.parser.CliSyntax.PREFIX_START;
import static scrolls.elder.logic.parser.CliSyntax.PREFIX_TITLE;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import scrolls.elder.commons.core.index.Index;
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
 * Adds a person to the address book.
 */
public class LogAddCommand extends Command {

    public static final String COMMAND_WORD = "logadd";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a log to the address book. "
        + "Parameters: BEFRIENDEE_INDEX VOLUNTEER_INDEX "
        + PREFIX_TITLE + "TITLE "
        + PREFIX_START + "START_DATE (yyyy-MM-dd) "
        + PREFIX_DURATION + "DURATION (in hours) "
        + PREFIX_REMARKS + "REMARKS "
        + "\n"
        + "Example: " + COMMAND_WORD + " 1 2 "
        + PREFIX_TITLE + "Icebreaker session "
        + PREFIX_START + "2021-03-01 "
        + PREFIX_DURATION + "2 "
        + PREFIX_REMARKS + "was a good session ";

    public static final String MESSAGE_SUCCESS = "New log added!";
    public static final String MESSAGE_NEGATIVE_DURATION = "Duration cannot be negative.";
    public static final String MESSAGE_PERSONS_NOT_PAIRED = "The volunteer and befriendee are not paired.";

    /**
     * Contains data for the log to be added.
     * Does not contain the final log ID.
     */
    private final String title;
    private final Index volunteerIndex;
    private final Index befriendeeIndex;
    private final int duration;
    private final Date startDate;
    private final String remarks;

    /**
     * Creates an LogAddCommand to add the specified {@code Log}
     */
    public LogAddCommand(String title, Index befriendeeIndex, Index volunteerIndex, int duration,
                         Date startDate, String remarks) {
        this.title = title;
        this.volunteerIndex = volunteerIndex;
        this.befriendeeIndex = befriendeeIndex;
        this.duration = duration;
        this.startDate = startDate;
        this.remarks = remarks;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (duration <= 0) {
            throw new CommandException(MESSAGE_NEGATIVE_DURATION);
        }

        PersonStore personStore = model.getMutableDatastore().getMutablePersonStore();
        LogStore logStore = model.getMutableDatastore().getMutableLogStore();

        List<Person> lastShownBList = personStore.getFilteredBefriendeeList();
        List<Person> lastShownVList = personStore.getFilteredVolunteerList();

        if (befriendeeIndex.getZeroBased() >= lastShownBList.size()
            || volunteerIndex.getZeroBased() >= lastShownVList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person befriendee = lastShownBList.get(befriendeeIndex.getZeroBased());
        Person volunteer = lastShownVList.get(volunteerIndex.getZeroBased());

        if (!befriendee.isPairedWith(volunteer) && !volunteer.isPairedWith(befriendee)) {
            throw new CommandException(MESSAGE_PERSONS_NOT_PAIRED);
        }

        Log toAdd =
                new Log(model.getDatastore(), title, volunteer.getPersonId(), befriendee.getPersonId(),
                        duration, startDate, remarks);

        Integer logId = logStore.addLog(toAdd);

        Integer latestLogIdBefriendee = getLatestLogId(befriendee, toAdd, logStore, logId);
        Integer latestLogIdVolunteer = getLatestLogId(volunteer, toAdd, logStore, logId);

        // create updated persons
        Person updatedBefriendee = createUpdatedPerson(befriendee, duration, latestLogIdBefriendee);
        Person updatedVolunteer = createUpdatedPerson(volunteer, duration, latestLogIdVolunteer);

        personStore.setPerson(befriendee, updatedBefriendee);
        personStore.setPerson(volunteer, updatedVolunteer);
        personStore.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
        logStore.updateFilteredLogList(LogStore.PREDICATE_SHOW_ALL_LOGS);
        logStore.updateFilteredLogListByPersonId(null);
        model.commitDatastore();
        return new CommandResult(MESSAGE_SUCCESS);
    }

    /**
     * Create and return a {@code Person} with the details of {@code personToUpdate}
     * edited with the updated timeServed and latestLogId.
     */
    private Person createUpdatedPerson(Person personToUpdate, int duration, Integer logId) {
        assert personToUpdate != null;

        Name name = personToUpdate.getName();
        Phone phone = personToUpdate.getPhone();
        Email email = personToUpdate.getEmail();
        Address address = personToUpdate.getAddress();
        Set<Tag> tags = personToUpdate.getTags();
        Role role = personToUpdate.getRole();
        Optional<Name> pairedWithName = personToUpdate.getPairedWithName();
        Optional<Integer> pairedWithId = personToUpdate.getPairedWithId();
        int updatedTimeServed = personToUpdate.getTimeServed() + duration;
        Optional<Integer> latestLogId = Optional.of(logId);

        return PersonFactory.withIdFromParams(personToUpdate.getPersonId(), name, phone, email, address, role,
                tags, pairedWithName, pairedWithId, updatedTimeServed, latestLogId);
    }

    /**
     * Returns the ID of the latest log for the person.
     */
    private Integer getLatestLogId(Person person, Log toAdd, LogStore logStore, Integer toAddId) {
        Date toAddDate = toAdd.getStartDate();

        if (person.isLatestLogPresent()) {
            Log currentLatest = logStore.getLogById(person.getLatestLogId().get());
            Date latestLogDate = currentLatest.getStartDate();

            if (toAddDate.before(latestLogDate)) {
                return currentLatest.getLogId();
            } else {
                return toAddId;
            }
        }

        return toAddId;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof LogAddCommand)) {
            return false;
        }

        LogAddCommand otherAddCommand = (LogAddCommand) other;
        return otherAddCommand.title.equals(title)
            && otherAddCommand.volunteerIndex.equals(volunteerIndex)
            && otherAddCommand.befriendeeIndex.equals(befriendeeIndex)
            && otherAddCommand.duration == duration
            && otherAddCommand.startDate.equals(startDate)
            && otherAddCommand.remarks.equals(remarks);
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
