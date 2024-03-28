package scrolls.elder.logic.commands;

import scrolls.elder.logic.commands.exceptions.CommandException;
import scrolls.elder.model.Datastore;
import scrolls.elder.model.Model;

import static java.util.Objects.requireNonNull;

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
        if (!model.getDatastoreVersionStorage().canUndo()) {
            throw new CommandException(MESSAGE_UNDO_ERROR);
        }

        model.undoChanges();
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
