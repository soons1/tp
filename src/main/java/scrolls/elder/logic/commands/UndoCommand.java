package scrolls.elder.logic.commands;

import static java.util.Objects.requireNonNull;

import scrolls.elder.logic.commands.exceptions.CommandException;
import scrolls.elder.model.Model;

/**
 * Reverses the effect of a previous operation on the Datastore.
 */
public class UndoCommand extends Command {
    public static final String COMMAND_WORD = "undo";
    public static final String MESSAGE_SUCCESS = "Previous operation has been undone!";
    public static final String MESSAGE_UNDO_ERROR = "No previous operation to be undone";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (!model.canUndoDatastore()) {
            throw new CommandException(MESSAGE_UNDO_ERROR);
        }

        model.undoChanges();
        model.getDatastore().getPersonStore().updateFilteredPersonList(Model.PREDICATE_SHOW_ALL);
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
