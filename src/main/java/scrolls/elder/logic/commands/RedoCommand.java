package scrolls.elder.logic.commands;

import scrolls.elder.logic.commands.exceptions.CommandException;
import scrolls.elder.model.Model;

import static java.util.Objects.requireNonNull;

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
        if (!model.getDatastoreVersionStorage().canRedo()) {
            throw new CommandException(MESSAGE_REDO_ERROR);
        }
        model.redoChanges();
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
