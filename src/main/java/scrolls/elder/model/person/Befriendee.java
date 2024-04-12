package scrolls.elder.model.person;

import java.util.Optional;
import java.util.Set;

import scrolls.elder.commons.util.ToStringBuilder;
import scrolls.elder.model.tag.Tag;

/**
 * Represents a Befriendee in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Befriendee extends Person {
    /**
     * Creates a befriendee with the data from the relevant parameters
     */
    public Befriendee(Name name, Phone phone, Email email, Address address, Set<Tag> tags,
                      Optional<Name> pairedWithName, Optional<Integer> pairedWithId, int timeServed,
                      Optional<Integer> latestLogId) {
        super(name, phone, email, address, tags, new Role("befriendee"), pairedWithName, pairedWithId,
                timeServed, latestLogId);
    }

    /**
     * Creates a befriendee with the data of {@code p} and corresponding ID.
     */
    public Befriendee(int id, Person p) {
        super(id, p);
    }

    @Override
    public boolean isVolunteer() {
        return false;
    }

    @Override
    public boolean isBefriendee() {
        return true;
    }


    @Override
    public Role getRole() {
        return this.role;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Befriendee)) {
            return false;
        }

        // TODO figure out how to assert equals for date, without GitHub actions acting up, try using LocalDate
        Befriendee otherBefriendee = (Befriendee) other;
        return personId == otherBefriendee.personId
                && name.equals(otherBefriendee.name)
                && phone.equals(otherBefriendee.phone)
                && email.equals(otherBefriendee.email)
                && address.equals(otherBefriendee.address)
                && tags.equals(otherBefriendee.tags)
                && pairedWithName.equals(otherBefriendee.pairedWithName)
                && pairedWithId.equals(otherBefriendee.pairedWithId)
                && timeServed == otherBefriendee.timeServed
                && latestLogId.equals(otherBefriendee.latestLogId);
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
                .add("pairedWithName", pairedWithName.orElse(Name.getNone()))
                .add("pairedWithId", pairedWithId.orElse(-1))
                .add("timeServed", timeServed)
                .add("latestLogId", latestLogId.orElse(-1))
                .toString();
    }
}
