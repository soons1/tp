package scrolls.elder.logic.commands;

import static java.util.Objects.requireNonNull;
import static scrolls.elder.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static scrolls.elder.logic.parser.CliSyntax.PREFIX_EMAIL;
import static scrolls.elder.logic.parser.CliSyntax.PREFIX_NAME;
import static scrolls.elder.logic.parser.CliSyntax.PREFIX_PHONE;
import static scrolls.elder.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import scrolls.elder.commons.core.index.Index;
import scrolls.elder.commons.util.CollectionUtil;
import scrolls.elder.commons.util.ToStringBuilder;
import scrolls.elder.logic.Messages;
import scrolls.elder.logic.commands.exceptions.CommandException;
import scrolls.elder.model.Model;
import scrolls.elder.model.person.Address;
import scrolls.elder.model.person.Befriendee;
import scrolls.elder.model.person.Email;
import scrolls.elder.model.person.Name;
import scrolls.elder.model.person.Person;
import scrolls.elder.model.person.Phone;
import scrolls.elder.model.person.Role;
import scrolls.elder.model.person.Volunteer;
import scrolls.elder.model.tag.Tag;


/**
 * Edits the details of an existing person in the address book.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the person identified "
            + "by the index number used in the displayed person list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_PHONE + "91234567 "
            + PREFIX_EMAIL + "johndoe@example.com";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Person: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";
    public static final String MESSAGE_NO_ROLE = "Role must be specified when editing a person.";

    private final Index index;
    private final EditPersonDescriptor editPersonDescriptor;

    /**
     * @param index                of the person in the filtered person list to edit
     * @param editPersonDescriptor details to edit the person with
     */
    public EditCommand(Index index, EditPersonDescriptor editPersonDescriptor) {
        requireNonNull(index);
        requireNonNull(editPersonDescriptor);

        this.index = index;
        this.editPersonDescriptor = new EditPersonDescriptor(editPersonDescriptor);
    }

    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(Person personToEdit, EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        Address updatedAddress = editPersonDescriptor.getAddress().orElse(personToEdit.getAddress());
        Set<Tag> updatedTags = editPersonDescriptor.getTags().orElse(personToEdit.getTags());
        Role role = editPersonDescriptor.getRole().orElse(personToEdit.getRole());
        Optional<Name> pairedWithName = personToEdit.getPairedWithName();
        Optional<Integer> pairedWithId = personToEdit.getPairedWithId();
        int timeServed = personToEdit.getTimeServed();

        Person p;
        if (role.isVolunteer()) {
            p = new Volunteer(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedTags,
                    pairedWithName, pairedWithId, timeServed);
        } else {
            p = new Befriendee(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedTags,
                    pairedWithName, pairedWithId, timeServed);
        }
        p.setId(personToEdit.getId());
        return p;
    }

    private static Person createEditedPair(Person editedPerson, Person originalPair) {
        assert editedPerson != null;

        // Will check before calling this function that the editedPerson is paired
        Name updatedName = originalPair.getName();
        Phone updatedPhone = originalPair.getPhone();
        Email updatedEmail = originalPair.getEmail();
        Address updatedAddress = originalPair.getAddress();
        Set<Tag> updatedTags = originalPair.getTags();
        Role role = originalPair.getRole();
        Optional<Name> updatedPairedWithName = Optional.of(editedPerson.getName());
        Optional<Integer> updatedPairedWithID = Optional.of(editedPerson.getId());
        int timeServed = originalPair.getTimeServed();

        Person p;
        if (role.isVolunteer()) {
            p = new Volunteer(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedTags,
                    updatedPairedWithName, updatedPairedWithID, timeServed);
        } else {
            p = new Befriendee(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedTags,
                    updatedPairedWithName, updatedPairedWithID, timeServed);
        }

        p.setId(originalPair.getId());
        return p;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (editPersonDescriptor.getRole().isEmpty()) {
            throw new CommandException(MESSAGE_NO_ROLE);
        }

        List<Person> lastShownList;
        if (editPersonDescriptor.getRole().get().isVolunteer()) {
            lastShownList = model.getFilteredVolunteerList();
        } else {
            lastShownList = model.getFilteredBefriendeeList();
        }

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person editedPerson = createEditedPerson(personToEdit, editPersonDescriptor);

        if (!personToEdit.isSamePerson(editedPerson) && model.hasPerson(editedPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        if (editedPerson.isPaired()) {
            Person pairedWith = model.getPersonFromID(editedPerson.getPairedWithId().get());
            Person pairedWithUpdated = createEditedPair(editedPerson, pairedWith);
            model.setPerson(pairedWith, pairedWithUpdated);
        }

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EditCommand)) {
            return false;
        }

        EditCommand otherEditCommand = (EditCommand) other;
        return index.equals(otherEditCommand.index)
                && editPersonDescriptor.equals(otherEditCommand.editPersonDescriptor);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("editPersonDescriptor", editPersonDescriptor)
                .toString();
    }

    /**
     * Stores the details to edit the person with. Each non-empty field value will replace the
     * corresponding field value of the person.
     */
    public static class EditPersonDescriptor {
        private Name name;
        private Phone phone;
        private Email email;
        private Address address;
        private Set<Tag> tags;
        private Role role;

        public EditPersonDescriptor() {
        }

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditPersonDescriptor(EditPersonDescriptor toCopy) {
            setName(toCopy.name);
            setPhone(toCopy.phone);
            setEmail(toCopy.email);
            setAddress(toCopy.address);
            setTags(toCopy.tags);
            setRole(toCopy.role);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, phone, email, address, tags);
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Email> getEmail() {
            return Optional.ofNullable(email);
        }

        public void setEmail(Email email) {
            this.email = email;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        public Optional<Role> getRole() {
            return Optional.ofNullable(role);
        }

        public void setRole(Role role) {
            this.role = role;
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditPersonDescriptor)) {
                return false;
            }

            EditPersonDescriptor otherEditPersonDescriptor = (EditPersonDescriptor) other;
            return Objects.equals(name, otherEditPersonDescriptor.name)
                    && Objects.equals(phone, otherEditPersonDescriptor.phone)
                    && Objects.equals(email, otherEditPersonDescriptor.email)
                    && Objects.equals(address, otherEditPersonDescriptor.address)
                    && Objects.equals(tags, otherEditPersonDescriptor.tags)
                    && Objects.equals(role, otherEditPersonDescriptor.role);
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .add("name", name)
                    .add("phone", phone)
                    .add("email", email)
                    .add("address", address)
                    .add("tags", tags)
                    .add("role", role)
                    .toString();
        }
    }
}
