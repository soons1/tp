package scrolls.elder.logic.parser;

import static scrolls.elder.logic.parser.CommandParserTestUtil.assertParseFailure;

import org.junit.jupiter.api.Test;

import scrolls.elder.logic.Messages;
import scrolls.elder.logic.commands.LogAddCommand;

class LogAddCommandParserTest {

    private LogAddCommandParser parser = new LogAddCommandParser();

    //TODO Implement test cases to create same LogAddCommand object as in LogAddCommandParser
    @Test
    void parse_validArgs_returnsLogAddCommand() {
        /*
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        assertParseSuccess(parser, "1 1 t/Movies s/2024-03-07 d/2 r/Good",
                new LogAddCommand("Movies", TypicalIndexes.INDEX_FIRST_PERSON, TypicalIndexes.INDEX_FIRST_PERSON,
                        2, formatter.parse("2024-03-07"), "Good"));
         */
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "pe",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, LogAddCommand.MESSAGE_USAGE));
    }
}
