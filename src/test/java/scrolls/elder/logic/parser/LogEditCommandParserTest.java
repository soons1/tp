package scrolls.elder.logic.parser;

import static scrolls.elder.logic.parser.CommandParserTestUtil.assertParseFailure;

import org.junit.jupiter.api.Test;

import scrolls.elder.logic.Messages;
import scrolls.elder.logic.commands.LogEditCommand;

class LogEditCommandParserTest {

    private LogEditCommandParser parser = new LogEditCommandParser();

    //TODO Implement test cases to create same LogEditCommand object as in LogEditCommandParser
    @Test
    void parse() {
        //LogEditCommand.EditLogDescriptor editPersonDescriptor = new EditCommand.EditPersonDescriptorBuilder();
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "pe",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, LogEditCommand.MESSAGE_USAGE));
    }
}
