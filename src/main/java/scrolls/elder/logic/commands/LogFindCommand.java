package scrolls.elder.logic.commands;

import static java.util.Objects.requireNonNull;
import static scrolls.elder.logic.parser.CliSyntax.PREFIX_ROLE;

import scrolls.elder.commons.core.index.Index;
import scrolls.elder.commons.util.ToStringBuilder;
import scrolls.elder.logic.Messages;
import scrolls.elder.logic.commands.exceptions.CommandException;
import scrolls.elder.model.LogStore;
import scrolls.elder.model.Model;
import scrolls.elder.model.PersonStore;
import scrolls.elder.model.person.Person;
import scrolls.elder.model.person.Role;

import java.util.List;

public class LogFindCommand extends Command {

    public static final String COMMAND_WORD = "logfind";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds logs by person. "
        + "Parameters: INDEX "
        + PREFIX_ROLE + "ROLE "
        + "Example: " + COMMAND_WORD + " 1 "
        + PREFIX_ROLE + "volunteer";

    public static final String MESSAGE_SUCCESS = "Logs found!";

    public static final String MESSAGE_FINDLOG_PERSON_ERROR = "Unable to find logs: ";

    private final Role role;
    private final Index targetIndex;

    public LogFindCommand(Index targetIndex, Role role) {
        this.targetIndex = targetIndex;
        this.role = role;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        PersonStore personStore = model.getMutableDatastore().getMutablePersonStore();
        LogStore logStore = model.getMutableDatastore().getMutableLogStore();

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
