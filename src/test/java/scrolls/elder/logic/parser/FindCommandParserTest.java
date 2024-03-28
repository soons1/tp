package scrolls.elder.logic.parser;

import static scrolls.elder.logic.parser.CommandParserTestUtil.assertParseFailure;
import static scrolls.elder.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import scrolls.elder.logic.Messages;
import scrolls.elder.logic.commands.CommandTestUtil;
import scrolls.elder.logic.commands.FindCommand;
import scrolls.elder.model.person.NameContainsKeywordsPredicate;
import scrolls.elder.model.person.TagListContainsTagsPredicate;
import scrolls.elder.model.tag.Tag;

public class FindCommandParserTest {

    private static final TagListContainsTagsPredicate emptyTagPredicate =
            new TagListContainsTagsPredicate(Collections.emptySet());
    private static final NameContainsKeywordsPredicate emptyNamePredicate =
            new NameContainsKeywordsPredicate(Collections.emptyList());
    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_onlyRoleArg_throwsParseException() {
        assertParseFailure(parser, CommandTestUtil.ROLE_DESC_BEFRIENDEE,
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));

        assertParseFailure(parser, CommandTestUtil.ROLE_DESC_VOLUNTEER,
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_bothRoleArg_throwsParseException() {
        assertParseFailure(parser,
                CommandTestUtil.ROLE_DESC_BEFRIENDEE + CommandTestUtil.ROLE_DESC_VOLUNTEER + " Alice Bob",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));

    }

    @Test
    public void parse_bothPairFlags_throwsParseException() {
        assertParseFailure(parser,
                FindCommandParser.PAIRED_FLAG + " " + FindCommandParser.UNPAIRED_FLAG,
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));

    }

    @Test
    public void parse_validArgsFind_returnsFindCommand() {
        // All Persons
        FindCommand expectedFindCommand =
                new FindCommand(new NameContainsKeywordsPredicate(Arrays.asList("Alice", "Bob")), emptyTagPredicate,
                        true, true, true, true);
        // no leading and trailing whitespaces
        assertParseSuccess(parser, "Alice Bob", expectedFindCommand);
        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Alice \n \t Bob  \t", expectedFindCommand);

        // Volunteers
        FindCommand expectedFindVCommand =
                new FindCommand(new NameContainsKeywordsPredicate(Arrays.asList("Alice", "Bob")), emptyTagPredicate,
                        true, false, true, true);
        assertParseSuccess(parser, CommandTestUtil.ROLE_DESC_VOLUNTEER + " " + "Alice Bob", expectedFindVCommand);
        assertParseSuccess(parser, "r/volunteer \n Alice \n \t Bob  \t", expectedFindVCommand);

        // Befriendees
        FindCommand expectedFindBCommand =
                new FindCommand(new NameContainsKeywordsPredicate(Arrays.asList("Alice", "Bob")), emptyTagPredicate,
                        false, true, true, true);
        assertParseSuccess(parser, CommandTestUtil.ROLE_DESC_BEFRIENDEE + " " + "Alice Bob", expectedFindBCommand);
        assertParseSuccess(parser, "r/befriendee \n Alice \n \t Bob  \t", expectedFindBCommand);
    }

    @Test
    public void parse_onlyTagFindAll_returnsFindCommand() {
        TagListContainsTagsPredicate tagPredicate = prepareTagPredicate("friends");
        FindCommand expectedFindCommand =
                new FindCommand(emptyNamePredicate, tagPredicate,
                        true, true, true, true);
        assertParseSuccess(parser, "\n t/friends \n \t", expectedFindCommand);
        assertParseSuccess(parser, "t/friends", expectedFindCommand);
    }

    @Test
    public void parse_onlyPairFlagFindAll_returnsFindCommand() {
        FindCommand expectedFindPairedCommand =
                new FindCommand(emptyNamePredicate, emptyTagPredicate,
                        true, true, true, false);
        assertParseSuccess(parser, FindCommandParser.PAIRED_FLAG + "\n \t", expectedFindPairedCommand);
        assertParseSuccess(parser, FindCommandParser.PAIRED_FLAG, expectedFindPairedCommand);

        FindCommand expectedFindUnpairedCommand =
                new FindCommand(emptyNamePredicate, emptyTagPredicate,
                        true, true, true, false);
        assertParseSuccess(parser, FindCommandParser.UNPAIRED_FLAG + "\n \t", expectedFindPairedCommand);
        assertParseSuccess(parser, FindCommandParser.UNPAIRED_FLAG, expectedFindPairedCommand);
    }

    private TagListContainsTagsPredicate prepareTagPredicate(String userInput) {
        Set<Tag> tagList = new HashSet<>();
        String[] tagsArray = userInput.split("\\s+");

        for (String tag : tagsArray) {
            tagList.add(new Tag(tag));
        }

        return new TagListContainsTagsPredicate(tagList);
    }


}
