package scrolls.elder.logic.commands;

import static java.util.Objects.requireNonNull;

import scrolls.elder.logic.commands.exceptions.CommandException;
import scrolls.elder.model.Model;

/**
 * Reverses the effect of a previous undo operation on the Datastore.
 */
public class RedoCommand extends Command {
    public static final String COMMAND_WORD = "redo";
    public static final String MESSAGE_SUCCESS = "Previous undo operation has been reversed!";
    public static final String MESSAGE_REDO_ERROR = "No previous undo operation to be reversed";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (!model.canRedoDatastore()) {
            throw new CommandException(MESSAGE_REDO_ERROR);
        }

        model.redoChanges();
        model.getDatastore().getPersonStore().updateFilteredPersonList(Model.PREDICATE_SHOW_ALL);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
