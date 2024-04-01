package scrolls.elder.logic.parser;

import static scrolls.elder.logic.parser.CommandParserTestUtil.assertParseFailure;
import static scrolls.elder.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import scrolls.elder.logic.Messages;
import scrolls.elder.logic.commands.CommandTestUtil;
import scrolls.elder.logic.commands.LogFindCommand;
import scrolls.elder.model.person.Role;
import scrolls.elder.testutil.TypicalIndexes;

public class LogFindCommandParserTest {

    private LogFindCommandParser parser = new LogFindCommandParser();
    private final Role volunteerRole = new Role(CommandTestUtil.VALID_ROLE_VOLUNTEER);
    private final Role befriendeeRole = new Role(CommandTestUtil.VALID_ROLE_BEFRIENDEE);

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, LogFindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_onlyRoleArg_throwsParseException() {
        assertParseFailure(parser, CommandTestUtil.ROLE_DESC_BEFRIENDEE,
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, LogFindCommand.MESSAGE_USAGE));

        assertParseFailure(parser, CommandTestUtil.ROLE_DESC_VOLUNTEER,
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, LogFindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_onlyIndexArg_throwsParseException() {
        assertParseFailure(parser, "1",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, LogFindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_bothRoleArg_throwsParseException() {
        assertParseFailure(parser, "1 "
                        + CommandTestUtil.ROLE_DESC_BEFRIENDEE
                        + CommandTestUtil.ROLE_DESC_VOLUNTEER,
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_ROLE));

    }

    @Test
    public void parse_validArgsFind_returnsLogFindCommand() {
        LogFindCommand expectedLogFindCommand =
                new LogFindCommand(TypicalIndexes.INDEX_FIRST_PERSON, befriendeeRole);
        assertParseSuccess(parser, "1 " + CommandTestUtil.ROLE_DESC_BEFRIENDEE,
                expectedLogFindCommand);

        expectedLogFindCommand =
                new LogFindCommand(TypicalIndexes.INDEX_FIRST_PERSON, volunteerRole);
        assertParseSuccess(parser, "1 " + CommandTestUtil.ROLE_DESC_VOLUNTEER,
                expectedLogFindCommand);
    }


}
