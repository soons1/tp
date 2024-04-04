package scrolls.elder.logic.parser;

import static scrolls.elder.logic.parser.CommandParserTestUtil.assertParseFailure;
import static scrolls.elder.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;

import scrolls.elder.logic.Messages;
import scrolls.elder.logic.commands.LogEditCommand;
import scrolls.elder.testutil.EditLogDescriptorBuilder;
import scrolls.elder.testutil.TypicalIndexes;
import scrolls.elder.testutil.TypicalLogs;

class LogEditCommandParserTest {

    private LogEditCommandParser parser = new LogEditCommandParser();

    @Test
    void parse_validArgs_returnsLogEditCommand() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setLenient(false); // to make sure that the date strictly follows the format "yyyy-MM-dd"
        Date date;
        try {
            date = formatter.parse("2024-04-08");
        } catch (java.text.ParseException e) {
            date = new Date();
        }
        LogEditCommand.EditLogDescriptor editLogDescriptor =
                new EditLogDescriptorBuilder(TypicalLogs.LOG_BENSON_TO_FIONA)
                        .withBefriendeeIndex(null)
                        .withVolunteerIndex(null)
                        .withStartDate(date).build();

        assertParseSuccess(parser, "1 t/Icebreaker s/2024-04-08 d/3 r/Was okay.",
                new LogEditCommand(TypicalIndexes.INDEX_FIRST_PERSON, editLogDescriptor));
    }

    @Test
    void parse_someValidArgs_returnsLogEditCommand() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setLenient(false); // to make sure that the date strictly follows the format "yyyy-MM-dd"
        Date date;
        try {
            date = formatter.parse("2024-04-08");
        } catch (java.text.ParseException e) {
            date = new Date();
        }
        LogEditCommand.EditLogDescriptor editLogDescriptor =
                new EditLogDescriptorBuilder(TypicalLogs.LOG_BENSON_TO_FIONA)
                        .withTitle(null)
                        .withBefriendeeIndex(null)
                        .withVolunteerIndex(null)
                        .withStartDate(date).build();

        assertParseSuccess(parser, "1 s/2024-04-08 d/3 r/Was okay.",
                new LogEditCommand(TypicalIndexes.INDEX_FIRST_PERSON, editLogDescriptor));
    }

    @Test
    public void parse_noArgs_throwsParseException() {
        assertParseFailure(parser, "  ",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, LogEditCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_noIndex_throwsParseException() {
        assertParseFailure(parser, "t/Icebreaker s/2024-04-08 d/3 r/Was okay.",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, LogEditCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidDateFormat_throwsParseException() {
        assertParseFailure(parser, "1 t/Icebreaker s/2024/04/08 d/3 r/Was okay.",
                "Invalid date format. Expected format is yyyy-MM-dd.");
    }

    @Test
    public void parse_invalidDurationFormat_throwsParseException() {
        assertParseFailure(parser, "1 t/Icebreaker s/2024-04-08 d/Two r/Was okay.",
                "Invalid number format. Expected a number.");
    }
    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "pe",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, LogEditCommand.MESSAGE_USAGE));
    }
}
