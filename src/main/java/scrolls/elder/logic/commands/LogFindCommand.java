package scrolls.elder.logic.commands;

import static java.util.Objects.requireNonNull;
import static scrolls.elder.logic.parser.CliSyntax.PREFIX_ROLE;

import java.util.List;

import scrolls.elder.commons.core.index.Index;
import scrolls.elder.commons.util.ToStringBuilder;
import scrolls.elder.logic.Messages;
import scrolls.elder.logic.commands.exceptions.CommandException;
import scrolls.elder.model.Model;
import scrolls.elder.model.ReadOnlyLogStore;
import scrolls.elder.model.ReadOnlyPersonStore;
import scrolls.elder.model.person.Person;
import scrolls.elder.model.person.Role;

/**
 * Finds an existing logs related to specified Person in the address book.
 */
public class LogFindCommand extends Command {

    public static final String COMMAND_WORD_LOGFIND = "logfind";
    public static final String COMMAND_WORD_FINDLOG = "findlog";

    public static final String MESSAGE_USAGE = COMMAND_WORD_LOGFIND + ": Finds logs by person. "
        + "Parameters: INDEX "
        + PREFIX_ROLE + "ROLE "
        + "\n"
        + "Example: " + COMMAND_WORD_LOGFIND + " 1 "
        + PREFIX_ROLE + "volunteer";

    // Sample Format: logfind 1 r/volunteer

    public static final String MESSAGE_SUCCESS = "Logs found!";

    public static final String MESSAGE_FINDLOG_PERSON_ERROR = "Unable to find logs: ";

    private final Role role;
    private final Index targetIndex;

    /**
     * @param targetIndex             of the specified person in the filtered list
     * @param role                   of the person to differentiate between volunteer and befriendee
     */
    public LogFindCommand(Index targetIndex, Role role) {
        this.targetIndex = targetIndex;
        this.role = role;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        ReadOnlyPersonStore personStore = model.getDatastore().getPersonStore();
        ReadOnlyLogStore logStore = model.getDatastore().getLogStore();

        List<Person> lastShownList;
        if (role.isVolunteer()) {
            lastShownList = personStore.getFilteredVolunteerList();
        } else {
            lastShownList = personStore.getFilteredBefriendeeList();
        }

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(MESSAGE_FINDLOG_PERSON_ERROR + Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToFindLog = lastShownList.get(targetIndex.getZeroBased());
        int personID = personToFindLog.getPersonId();
        logStore.updateFilteredLogListByPersonId(personID);

        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls and different types
        if (!(other instanceof LogFindCommand)) {
            return false;
        }

        // state check
        LogFindCommand otherFindCommand = (LogFindCommand) other;
        return otherFindCommand.role.equals(role)
                && otherFindCommand.targetIndex.equals(targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .add("role", role)
            .add("listIndex", targetIndex)
            .toString();
    }



}
