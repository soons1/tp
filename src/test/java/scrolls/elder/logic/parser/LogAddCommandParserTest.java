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
        formatter.setLenient(false); // to make sure that the date strictly follows the format "yyyy-MM-dd"
        Date date;
        try {
            date = formatter.parse("2024-03-07");
        } catch (java.text.ParseException e) {
            date = new Date();
        }
        assertParseSuccess(parser, "1 2 t/Movies s/2024-03-07 d/2 r/Good.",
                new LogAddCommand("Movies", TypicalIndexes.INDEX_FIRST_PERSON, TypicalIndexes.INDEX_SECOND_PERSON,
                        2, date, "Good."));
        */
    }

    @Test
    public void parse_noArgs_throwsParseException() {
        assertParseFailure(parser, "  ",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, LogAddCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_noIndex_throwsParseException() {
        assertParseFailure(parser, "t/Movies s/2024-03-07 d/2 r/Good",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, LogAddCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidDateFormat_throwsParseException() {
        assertParseFailure(parser, "1 2 t/Movies s/2024/03/07 d/2 r/Good",
                "Invalid date format. Expected format is yyyy-MM-dd.");
    }

    @Test
    public void parse_invalidDurationFormat_throwsParseException() {
        assertParseFailure(parser, "1 2 t/Movies s/2024-03-07 d/Two r/Good",
                "Invalid number format. Expected a number.");
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "pe",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, LogAddCommand.MESSAGE_USAGE));
    }
}
