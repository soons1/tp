package scrolls.elder.logic.parser;

import static scrolls.elder.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import scrolls.elder.commons.core.index.Index;
import scrolls.elder.logic.commands.LogDeleteCommand;
import scrolls.elder.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new LogDeleteCommand object
 */
public class LogDeleteCommandParser implements Parser<LogDeleteCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the LogDeleteCommand
     * and returns a LogDeleteCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public LogDeleteCommand parse(String args) throws ParseException {
        try {
            ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args);
            Index index;
            try {
                index = ParserUtil.parseIndex(argMultimap.getPreamble());
            } catch (ParseException pe) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, LogDeleteCommand.MESSAGE_USAGE),
                        pe);
            }
            return new LogDeleteCommand(index);

        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, LogDeleteCommand.MESSAGE_USAGE), pe);
        }
    }
}
