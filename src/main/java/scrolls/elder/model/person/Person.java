package scrolls.elder.model.person;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import scrolls.elder.commons.util.CollectionUtil;
import scrolls.elder.commons.util.ToStringBuilder;
import scrolls.elder.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public abstract class Person {
    private static final int PLACEHOLDER_ID = -1;

    // Identity fields
    protected final int personId;
    protected final Name name;
    protected final Phone phone;
    protected final Email email;
    protected final Role role;

    // Data fields
    protected final Address address;
    protected final Set<Tag> tags = new HashSet<>();
    protected final Optional<Name> pairedWithName;
    protected final Optional<Integer> pairedWithId;
    protected final int timeServed;
    protected final Optional<Integer> latestLogId;

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags, Role role,
                  Optional<Name> pairedWithName, Optional<Integer> pairedWithId, int timeServed,
                  Optional<Integer> latestLogId) {
        CollectionUtil.requireAllNonNull(name, phone, email, address, tags, role, pairedWithName, pairedWithId);
        this.personId = PLACEHOLDER_ID;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);
        this.role = role;
        this.pairedWithName = pairedWithName;
        this.pairedWithId = pairedWithId;
        assert timeServed >= 0 : "Time served must be non-negative";
        this.timeServed = timeServed;
        this.latestLogId = latestLogId;
    }

    /**
     * Creates a person with the given ID and data of {@code p}.
     */
    public Person(int personId, Person p) {
        this.personId = personId;
        this.name = p.getName();
        this.phone = p.getPhone();
        this.email = p.getEmail();
        this.address = p.getAddress();
        this.tags.addAll(p.getTags());
        this.role = p.getRole();
        this.pairedWithName = p.getPairedWithName();
        this.pairedWithId = p.getPairedWithId();
        this.timeServed = p.getTimeServed();
        this.latestLogId = p.getLatestLogId();
    }

    public int getPersonId() {
        return personId;
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public Optional<Name> getPairedWithName() {
        return pairedWithName;
    }

    public Optional<Integer> getPairedWithId() {
        return pairedWithId;
    }
    public int getTimeServed() {
        return timeServed;
    }

    public Optional<Integer> getLatestLogId() {
        return latestLogId;
    }

    public boolean isPairPresent(Person person) {
        return person.getPairedWithName().isPresent();
    }

    public boolean isPaired() {
        return pairedWithName.isPresent();
    }

    public boolean isPairedWith(Person otherPerson) {
        return pairedWithId.isPresent() && pairedWithId.get() == otherPerson.getPersonId();
    }

    /**
     * Returns true if both persons have the same name.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null && otherPerson.getPersonId() == this.getPersonId();
    }

    /**
     * Returns true if all fields of the latest log are present
     */
    public boolean isLatestLogPresent() {
        return latestLogId.isPresent();
    }

    /**
    * Returns true if person is a volunteer, and false if person is not a volunteer
    */
    public abstract boolean isVolunteer();

    /**
    * Returns true if person is a befriendee, and false if person is not a befriendee
    */
    public abstract boolean isBefriendee();

    public abstract Role getRole();

    //// Overrides

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person)) {
            return false;
        }

        // TODO figure out how to assert equals for date, without GitHub actions acting up, try using LocalDate
        Person otherPerson = (Person) other;
        return personId == otherPerson.personId
                && name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
                && tags.equals(otherPerson.tags)
                && role.equals(otherPerson.role)
                && pairedWithName.equals(otherPerson.pairedWithName)
                && pairedWithId.equals(otherPerson.pairedWithId)
                && timeServed == otherPerson.timeServed
                && latestLogId.equals(latestLogId);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects
                .hash(personId, name, phone, email, address, tags, role, pairedWithName, pairedWithId,
                        timeServed, latestLogId);
    }

    // TODO potential issues with date
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("id", personId)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("tags", tags)
                .add("role", role)
                .add("pairedWithName", pairedWithName.orElse(Name.getNone()))
                .add("pairedWithId", pairedWithId.orElse(-1))
                .add("timeServed", timeServed)
                .add("latestLogId", latestLogId.orElse(-1))
                .toString();
    }

}
