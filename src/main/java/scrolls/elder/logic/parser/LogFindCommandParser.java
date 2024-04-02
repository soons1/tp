package scrolls.elder.logic.parser;

import static scrolls.elder.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static scrolls.elder.logic.parser.CliSyntax.PREFIX_ROLE;

import scrolls.elder.commons.core.index.Index;
import scrolls.elder.logic.commands.LogFindCommand;
import scrolls.elder.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new LogFindCommand object
 */
public class LogFindCommandParser implements Parser<LogFindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the LogFindCommand
     * and returns an LogFindCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public LogFindCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_ROLE);
        Index index;

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_ROLE);

        if (argMultimap.getValue(PREFIX_ROLE).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, LogFindCommand.MESSAGE_USAGE));
        }

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, LogFindCommand.MESSAGE_USAGE),
                    pe);
        }
        return new LogFindCommand(index, ParserUtil.parseRole(argMultimap.getValue(PREFIX_ROLE).get()));

    }

}
