package scrolls.elder.logic.parser;

import static scrolls.elder.logic.parser.CommandParserTestUtil.assertParseFailure;
import static scrolls.elder.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.*;

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
    public void parse_validArgsFindAllPersons_returnsFindCommand() {
        // no leading and trailing whitespaces
        FindCommand expectedFindCommand =
                new FindCommand(new NameContainsKeywordsPredicate(Arrays.asList("Alice", "Bob")), emptyTagPredicate,
                        true, true);

        assertParseSuccess(parser, "Alice Bob", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Alice \n \t Bob  \t", expectedFindCommand);
    }

    @Test
    public void parse_validArgsFindVolunteer_returnsFindCommand() {

        // no leading and trailing whitespaces
        FindCommand expectedFindCommand =
                new FindCommand(new NameContainsKeywordsPredicate(Arrays.asList("Alice", "Bob")), emptyTagPredicate,
                        true, false);

        assertParseSuccess(parser, CommandTestUtil.ROLE_DESC_VOLUNTEER + " " + "Alice Bob", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, "r/volunteer \n Alice \n \t Bob  \t", expectedFindCommand);
    }

    @Test
    public void parse_validArgsFindBefriendee_returnsFindCommand() {
        // no leading and trailing whitespaces
        FindCommand expectedFindCommand =
                new FindCommand(new NameContainsKeywordsPredicate(Arrays.asList("Alice", "Bob")), emptyTagPredicate,
                        false, true);

        assertParseSuccess(parser, CommandTestUtil.ROLE_DESC_BEFRIENDEE + " " + "Alice Bob", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, "r/befriendee \n Alice \n \t Bob  \t", expectedFindCommand);
    }

    @Test
    public void parse_onlyTagFindAll_returnsFindCommand() {
        NameContainsKeywordsPredicate namePredicate = new NameContainsKeywordsPredicate(Collections.emptyList());
        TagListContainsTagsPredicate tagPredicate = prepareTagPredicate("friends");

        FindCommand expectedFindCommand =
                new FindCommand(namePredicate, tagPredicate, true, true);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, "\n t/friends \n \t", expectedFindCommand);
        assertParseSuccess(parser, "t/friends", expectedFindCommand);
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
