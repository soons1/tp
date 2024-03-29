package scrolls.elder.logic.commands;

import static java.util.Objects.requireNonNull;
import static scrolls.elder.logic.parser.CliSyntax.PREFIX_DURATION;
import static scrolls.elder.logic.parser.CliSyntax.PREFIX_REMARKS;
import static scrolls.elder.logic.parser.CliSyntax.PREFIX_START;

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
        + "Parameters: INDEX1 INDEX2 "
        + PREFIX_START + "START_DATE (yyyy-MM-dd) "
        + PREFIX_DURATION + "DURATION (in hours) "
        + PREFIX_REMARKS + "REMARKS "
        + "Example: " + COMMAND_WORD + " 1 2 "
        + PREFIX_START + "2021-03-01 "
        + PREFIX_DURATION + "2 "
        + PREFIX_REMARKS + "was a good session";

    public static final String MESSAGE_SUCCESS = "New log added!";
    public static final String MESSAGE_NEGATIVE_DURATION = "Duration cannot be negative.";
    public static final String MESSAGE_PERSONS_NOT_PAIRED = "The volunteer and befriendee are not paired.";

    /**
     * Contains data for the log to be added.
     * Does not contain the final log ID.
     */
    private final Index volunteerIndex;
    private final Index befriendeeIndex;
    private final int duration;
    private final Date startDate;
    private final String remarks;

    /**
     * Creates an LogAddCommand to add the specified {@code Log}
     */
    public LogAddCommand(Index volunteerIndex, Index befriendeeIndex, int duration, Date startDate, String remarks) {
        this.volunteerIndex = volunteerIndex;
        this.befriendeeIndex = befriendeeIndex;
        this.duration = duration;
        this.startDate = startDate;
        this.remarks = remarks;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (duration < 0) {
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

        Person updatedBefriendee = createPersonWithTimeServed(befriendee, duration);
        Person updatedVolunteer = createPersonWithTimeServed(volunteer, duration);

        Log toAdd =
            new Log(model.getDatastore(), volunteer.getPersonId(), befriendee.getPersonId(), duration, startDate,
                remarks);

        logStore.addLog(toAdd);

        personStore.setPerson(befriendee, updatedBefriendee);
        personStore.setPerson(volunteer, updatedVolunteer);
        personStore.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL);
        model.commitDatastore();
        return new CommandResult(MESSAGE_SUCCESS);
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

        if (!(other instanceof LogAddCommand)) {
            return false;
        }

        LogAddCommand otherAddCommand = (LogAddCommand) other;
        return otherAddCommand.volunteerIndex == volunteerIndex
            && otherAddCommand.befriendeeIndex == befriendeeIndex
            && otherAddCommand.duration == duration
            && otherAddCommand.startDate.equals(startDate)
            && otherAddCommand.remarks.equals(remarks);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .add("volunteerId", volunteerIndex)
            .add("befriendeeId", befriendeeIndex)
            .add("duration", duration)
            .add("startDate", startDate)
            .add("remarks", remarks)
            .toString();
    }
}
