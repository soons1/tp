package scrolls.elder.logic.parser;

import static scrolls.elder.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Set;
import java.util.stream.Stream;

import scrolls.elder.logic.commands.AddCommand;
import scrolls.elder.logic.parser.exceptions.ParseException;
import scrolls.elder.model.person.Address;
import scrolls.elder.model.person.Email;
import scrolls.elder.model.person.Name;
import scrolls.elder.model.person.Person;
import scrolls.elder.model.person.Phone;
import scrolls.elder.model.person.Volunteer;
import scrolls.elder.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        boolean isVolunteer = false;
        boolean isBefriendee = false;

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, CliSyntax.PREFIX_VOLUNTEER, CliSyntax.PREFIX_BEFRIENDEE,
                        CliSyntax.PREFIX_PHONE,
                        CliSyntax.PREFIX_EMAIL,
                        CliSyntax.PREFIX_ADDRESS,
                        CliSyntax.PREFIX_TAG,

                        // remove this once integrated
                        CliSyntax.PREFIX_NAME);

        // Guard Clause: Check if command invalid due to both PREFIX_V and PREFIX_B used.
        if (arePrefixesPresent(argMultimap, CliSyntax.PREFIX_VOLUNTEER, CliSyntax.PREFIX_BEFRIENDEE)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        // Check for other Prefixes
        if (!arePrefixesPresent(argMultimap, CliSyntax.PREFIX_ADDRESS,
                CliSyntax.PREFIX_PHONE, CliSyntax.PREFIX_EMAIL)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        Name name = null;

        // Either PREFIX_V or PREFIX_B is used
        if (arePrefixesPresent(argMultimap, CliSyntax.PREFIX_VOLUNTEER)) {
            argMultimap.verifyNoDuplicatePrefixesFor(CliSyntax.PREFIX_VOLUNTEER);
            isVolunteer = true;

        } else if (arePrefixesPresent(argMultimap, CliSyntax.PREFIX_BEFRIENDEE)) {
            argMultimap.verifyNoDuplicatePrefixesFor(CliSyntax.PREFIX_BEFRIENDEE);
            isBefriendee = true;

        }

        // remove once integrated. @jerrong
        else if (arePrefixesPresent(argMultimap, CliSyntax.PREFIX_NAME)) {
            argMultimap.verifyNoDuplicatePrefixesFor(CliSyntax.PREFIX_PHONE, CliSyntax.PREFIX_NAME,
                    CliSyntax.PREFIX_EMAIL, CliSyntax.PREFIX_ADDRESS);
            name = ParserUtil.parseName(argMultimap.getValue(CliSyntax.PREFIX_NAME).get());
        }
        // remove once integrated. @jerrong

        else {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }


        argMultimap.verifyNoDuplicatePrefixesFor(CliSyntax.PREFIX_PHONE,
                CliSyntax.PREFIX_EMAIL, CliSyntax.PREFIX_ADDRESS);

        if (isVolunteer) {
            name = ParserUtil.parseName(argMultimap.getValue(CliSyntax.PREFIX_VOLUNTEER).get());
        } else if (isBefriendee) {
            name = ParserUtil.parseName(argMultimap.getValue(CliSyntax.PREFIX_BEFRIENDEE).get());
        }

        /* add once integrated @jerrong
            else {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
            }
        */

        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(CliSyntax.PREFIX_PHONE).get());
        Email email = ParserUtil.parseEmail(argMultimap.getValue(CliSyntax.PREFIX_EMAIL).get());
        Address address = ParserUtil.parseAddress(argMultimap.getValue(CliSyntax.PREFIX_ADDRESS).get());
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(CliSyntax.PREFIX_TAG));

        // temporary solution, delete after merging
        Person person = new Volunteer(name, phone, email, address, tagList);




        return new AddCommand(person);
    }

}