package scrolls.elder.logic;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import scrolls.elder.logic.parser.Prefix;
import scrolls.elder.model.log.Log;
import scrolls.elder.model.person.Person;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format! \n%1$s";
    public static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The person index provided is invalid.";
    public static final String MESSAGE_INVALID_LOG_DISPLAYED_INDEX = "The log index provided is invalid.";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d persons listed!";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW_WITH_ROLE = "%1$d persons listed with role %2$s!";

    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";
    public static final String MESSAGE_CONTACT_PAIRED_BEFORE_DELETE =
            "Contact is paired. Please unpair before deleting.";
    public static final String MESSAGE_CONTACT_LOG_BEFORE_DELETE =
            "Contact has a log in Elder Scrolls. Please delete the log before deleting the contact.";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String formatPerson(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("; Role: ")
                .append(person.getRole())
                .append("; Phone: ")
                .append(person.getPhone())
                .append("; Email: ")
                .append(person.getEmail())
                .append("; Address: ")
                .append(person.getAddress())
                .append("; Tags: ");
        person.getTags().forEach(builder::append);
        return builder.toString();
    }

    /**
     * Formats the {@code log} for display to the user.
     */
    public static String formatLog(Log log) {
        LocalDate startDateWithoutTime = log.getStartDate()
                .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

        final StringBuilder builder = new StringBuilder();
        builder.append("Title: ")
                .append(log.getLogTitle())
                .append("; Volunteer ID: ")
                .append(log.getVolunteerId())
                .append("; Befriendee ID: ")
                .append(log.getBefriendeeId())
                .append("; Start Date: ")
                .append(startDateWithoutTime)
                .append("; Duration: ")
                .append(log.getDuration())
                .append("; Remarks: ")
                .append(log.getRemarks());
        return builder.toString();
    }


}
