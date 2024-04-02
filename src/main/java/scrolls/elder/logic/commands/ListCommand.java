package scrolls.elder.logic.commands;

import static java.util.Objects.requireNonNull;

import scrolls.elder.model.LogStore;
import scrolls.elder.model.Model;
import scrolls.elder.model.ReadOnlyLogStore;
import scrolls.elder.model.ReadOnlyPersonStore;

/**
 * Lists all persons in the address book to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_SUCCESS = "Listed all persons and logs!";


    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        ReadOnlyLogStore logStore = model.getDatastore().getLogStore();
        ReadOnlyPersonStore personStore = model.getDatastore().getPersonStore();

        personStore.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
        logStore.updateFilteredLogList(LogStore.PREDICATE_SHOW_ALL_LOGS);
        logStore.updateFilteredLogListByPersonId(null);

        return new CommandResult(MESSAGE_SUCCESS);
    }
}
