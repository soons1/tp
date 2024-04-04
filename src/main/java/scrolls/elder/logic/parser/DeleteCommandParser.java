package scrolls.elder.logic.parser;

import static scrolls.elder.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static scrolls.elder.logic.parser.CliSyntax.PREFIX_ROLE;

import java.util.stream.Stream;

import scrolls.elder.commons.core.index.Index;
import scrolls.elder.logic.commands.DeleteCommand;
import scrolls.elder.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new DeleteCommand object
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns a DeleteCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeleteCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_ROLE);
        Index index;

        if (argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        }

        if (!arePrefixesPresent(argMultimap, CliSyntax.PREFIX_ROLE)) {
            throw new ParseException(String.format(DeleteCommand.MESSAGE_NO_ROLE, DeleteCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(CliSyntax.PREFIX_ROLE);

        index = ParserUtil.parseIndex(argMultimap.getPreamble());

        return new DeleteCommand(index, ParserUtil.parseRole(argMultimap.getValue(PREFIX_ROLE).get()));

    }

}
