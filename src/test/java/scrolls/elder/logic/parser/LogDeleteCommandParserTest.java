package scrolls.elder.logic.parser;

import static scrolls.elder.logic.parser.CommandParserTestUtil.assertParseFailure;
import static scrolls.elder.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import scrolls.elder.logic.Messages;
import scrolls.elder.logic.commands.LogDeleteCommand;
import scrolls.elder.testutil.TypicalIndexes;

class LogDeleteCommandParserTest {

    private LogDeleteCommandParser parser = new LogDeleteCommandParser();

    @Test
    public void parse_validArgs_returnsLogDeleteCommand() {
        assertParseSuccess(parser, "1",
                new LogDeleteCommand(TypicalIndexes.INDEX_FIRST_LOG));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, LogDeleteCommand.MESSAGE_USAGE));
    }
}
