package scrolls.elder.logic.parser;

import static scrolls.elder.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static scrolls.elder.logic.parser.CliSyntax.PREFIX_DURATION;
import static scrolls.elder.logic.parser.CliSyntax.PREFIX_REMARKS;
import static scrolls.elder.logic.parser.CliSyntax.PREFIX_START;

import scrolls.elder.commons.core.index.Index;
import scrolls.elder.logic.commands.LogEditCommand;
import scrolls.elder.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new LogEditCommand object
 */
public class LogEditCommandParser implements Parser<LogEditCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the LogEditCommand
     * and returns an LogEditCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public LogEditCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_START, PREFIX_DURATION, PREFIX_REMARKS);

        Index index;

        //Check for Index
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, LogEditCommand.MESSAGE_USAGE), pe);
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_START, PREFIX_DURATION, PREFIX_REMARKS);

        LogEditCommand.EditLogDescriptor editLogDescriptor = new LogEditCommand.EditLogDescriptor();

        if (argMultimap.getValue(PREFIX_START).isPresent()) {
            editLogDescriptor.setStartDate(ParserUtil.parseDate(argMultimap.getValue(PREFIX_START).get()));
        }
        if (argMultimap.getValue(PREFIX_DURATION).isPresent()) {
            editLogDescriptor.setDuration(ParserUtil.parseInt(argMultimap.getValue(PREFIX_DURATION).get()));
        }
        if (argMultimap.getValue(PREFIX_REMARKS).isPresent()) {
            editLogDescriptor.setRemarks(argMultimap.getValue(PREFIX_REMARKS).get());
        }

        if (!editLogDescriptor.isAnyFieldEdited()) {
            throw new ParseException(LogEditCommand.MESSAGE_NOT_EDITED);
        }

        return new LogEditCommand(index, editLogDescriptor);
    }
}
