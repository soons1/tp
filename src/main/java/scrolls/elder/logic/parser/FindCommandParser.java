package scrolls.elder.logic.parser;

import static scrolls.elder.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static scrolls.elder.logic.parser.CliSyntax.PREFIX_ROLE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import scrolls.elder.logic.commands.FindCommand;
import scrolls.elder.logic.parser.exceptions.ParseException;
import scrolls.elder.model.person.NameContainsKeywordsPredicate;
import scrolls.elder.model.person.TagListContainsTagsPredicate;
import scrolls.elder.model.tag.Tag;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    enum FindType {
        SEARCH_VOLUNTEER_ONLY,
        SEARCH_BEFRIENDEE_ONLY,
        SEARCH_BOTH,

        SEARCH_PAIRED,
        SEARCH_UNPAIRED
    }

    public static final String PAIRED_FLAG = "--paired";
    public static final String UNPAIRED_FLAG = "--unpaired";

    public static final String SEARCH_VOLUNTEER_FLAG = PREFIX_ROLE + "volunteer";
    public static final String SEARCH_BEFRIENDEE_FLAG = PREFIX_ROLE + "befriendee";

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        // Handle Role Parsing
        FindType findTypeRole = parseForRoles(trimmedArgs.split("\\s+"));
        String args2 = args.replace(SEARCH_VOLUNTEER_FLAG, "").replace(SEARCH_BEFRIENDEE_FLAG, "");
        String trimmedArgs2 = args2.trim();
        if (trimmedArgs2.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        // Handle Pair Flag Parsing
        FindType findTypePairStatus = parsePairFlag(trimmedArgs2.split("\\s+"));
        String args3 = trimmedArgs2.replace(PAIRED_FLAG, "").replace(UNPAIRED_FLAG, "");
        String trimmedArgs3 = args3.trim();

        // Handle Tag Parsing
        String[] keywordsWithoutRoles = trimmedArgs3.split("\\s+");
        TagListContainsTagsPredicate tagPredicate = parseForTags(keywordsWithoutRoles);;

        // Handle Name Parsing
        List<String> nameKeywordList = new ArrayList<>();
        for (String keyword : keywordsWithoutRoles) {
            if (!isValidTagName(keyword) && !keyword.trim().equals("")) {
                nameKeywordList.add(keyword.trim());
            }
        }
        NameContainsKeywordsPredicate namePredicate = new NameContainsKeywordsPredicate(nameKeywordList);

        boolean isSearchingVolunteer = true;
        boolean isSearchingBefriendee = true;
        boolean isSearchingPaired = true;
        boolean isSearchingUnpaired = true;

        if (findTypeRole.equals(FindType.SEARCH_BEFRIENDEE_ONLY)) {
            isSearchingVolunteer = false;
        }
        if (findTypeRole.equals(FindType.SEARCH_VOLUNTEER_ONLY)) {
            isSearchingBefriendee = false;
        }


        if (findTypePairStatus.equals(FindType.SEARCH_UNPAIRED)) {
            isSearchingPaired = false;
        }
        if (findTypePairStatus.equals(FindType.SEARCH_PAIRED)) {
            isSearchingUnpaired = false;
        }

        return new FindCommand(namePredicate, tagPredicate,
                isSearchingVolunteer, isSearchingBefriendee,
                isSearchingPaired, isSearchingUnpaired);

    }

    private static FindType parseForRoles(String[] nameKeywords) throws ParseException {
        boolean isSearchingVolunteer = false;
        boolean isSearchingBefriendee = false;

        // If both Volunteer and Befriendee roles are present, show error.
        if (Arrays.asList(nameKeywords).contains(SEARCH_VOLUNTEER_FLAG)
                && Arrays.asList(nameKeywords).contains(SEARCH_BEFRIENDEE_FLAG)) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        if (Arrays.asList(nameKeywords).contains(SEARCH_VOLUNTEER_FLAG)) {
            isSearchingVolunteer = true;
            isSearchingBefriendee = false;
        }

        if (Arrays.asList(nameKeywords).contains(SEARCH_BEFRIENDEE_FLAG)) {
            isSearchingBefriendee = true;
            isSearchingVolunteer = false;
        }

        if (isSearchingVolunteer) {
            return FindType.SEARCH_VOLUNTEER_ONLY;
        } else if (isSearchingBefriendee) {
            return FindType.SEARCH_BEFRIENDEE_ONLY;
        } else {
            return FindType.SEARCH_BOTH;
        }

    }

    private static FindType parsePairFlag(String[] nameKeywords) throws ParseException {
        boolean isSearchingPaired = true;
        boolean isSearchingUnpaired = true;

        // If both pair and unpair flags are present, show error.
        if (Arrays.asList(nameKeywords).contains(PAIRED_FLAG)
                && Arrays.asList(nameKeywords).contains(UNPAIRED_FLAG)) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        if (Arrays.asList(nameKeywords).contains(PAIRED_FLAG)) {
            isSearchingUnpaired = false;
        }

        if (Arrays.asList(nameKeywords).contains(UNPAIRED_FLAG)) {
            isSearchingPaired = false;
        }

        if (isSearchingPaired && isSearchingUnpaired) {
            return FindType.SEARCH_BOTH;
        } else if (isSearchingPaired) {
            return FindType.SEARCH_PAIRED;
        } else {
            return FindType.SEARCH_UNPAIRED;
        }
    }

    private static TagListContainsTagsPredicate parseForTags(String[] tagKeywords) {
        Set<Tag> tagList = new HashSet<>();

        for (String tagName : tagKeywords) {
            if (isValidTagName(tagName)) {
                String pureTagName = tagName.substring(2);
                tagList.add(new Tag(pureTagName));
            }
        }

        return new TagListContainsTagsPredicate(tagList);
    }

    private static boolean isValidTagName(String test) {
        final String tagValidationRegex = "^t/.+$";
        return test.matches(tagValidationRegex);
    }

}
