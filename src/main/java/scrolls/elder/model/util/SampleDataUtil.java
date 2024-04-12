package scrolls.elder.model.util;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import scrolls.elder.model.Datastore;
import scrolls.elder.model.LogStore;
import scrolls.elder.model.PersonStore;
import scrolls.elder.model.ReadOnlyDatastore;
import scrolls.elder.model.log.Log;
import scrolls.elder.model.person.Address;
import scrolls.elder.model.person.Befriendee;
import scrolls.elder.model.person.Email;
import scrolls.elder.model.person.Name;
import scrolls.elder.model.person.Person;
import scrolls.elder.model.person.Phone;
import scrolls.elder.model.person.Volunteer;
import scrolls.elder.model.tag.Tag;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {

    private static final Optional<Name> pairedWithNone = Optional.empty();
    private static final Optional<Integer> pairedWithNoID = Optional.empty();
    private static final int sampleTimeServed = 0;
    private static final Optional<Integer> latestLogNoId = Optional.empty();

    public static Person[] getSamplePersons() {

        // First Pair 1, 1: Alex Yeoh and David Li
        final Name alexYeohName = new Name("Alex Yeoh");
        final Name davidLiName = new Name("David Li");

        // Second Pair 2, 2: Bernice Yu and Irfan Ibrahim
        final Name berniceYuName = new Name("Bernice Yu");
        final Name irfanIbrahimName = new Name("Irfan Ibrahim");

        return new Person[]{
            new Volunteer(alexYeohName, new Phone("87438807"), new Email("alexyeoh@example.com"),
                    new Address("Blk 30 Geylang Street 29, #06-40"),
                    getTagSet("experienced"), Optional.of(davidLiName), Optional.of(3), 3,
                    Optional.of(1)),
            new Volunteer(berniceYuName, new Phone("99272758"), new Email("berniceyu@example.com"),
                    new Address("Blk 30 Lorong 3 Serangoon Gardens, #07-18"),
                    getTagSet("new", "student"), Optional.of(irfanIbrahimName), Optional.of(4), 3,
                    Optional.of(2)),
            new Volunteer(new Name("Charlotte Oliveiro"), new Phone("93210283"), new Email("charlotte@example.com"),
                    new Address("Blk 11 Ang Mo Kio Street 74, #11-04"), Collections.<Tag>emptySet(),
                    pairedWithNone, pairedWithNoID, sampleTimeServed,
                    latestLogNoId),


            new Befriendee(davidLiName, new Phone("91031282"), new Email("lidavid@example.com"),
                    new Address("Blk 436 Serangoon Gardens Street 26, #16-43"),
                    getTagSet("handicapped"), Optional.of(alexYeohName), Optional.of(0), 3,
                    Optional.of(1)),
            new Befriendee(irfanIbrahimName, new Phone("92492021"), new Email("irfan@example.com"),
                    new Address("Blk 47 Tampines Street 20, #17-35"),
                    getTagSet("livesAlone"), Optional.of(berniceYuName), Optional.of(1), 3,
                    Optional.of(2)),
            new Befriendee(new Name("Roy Balakrishnan"), new Phone("92624417"), new Email("royb@example.com"),
                    new Address("Blk 45 Aljunied Street 85, #11-31"),
                    getTagSet("diabetic"), pairedWithNone, pairedWithNoID, sampleTimeServed,
                    latestLogNoId)
        };
    }

    public static Log[] getSampleLogs(ReadOnlyDatastore datastore) {

        Calendar calendar = new GregorianCalendar();

        calendar.set(2024, Calendar.APRIL, 1);
        final Date aprilFirst = calendar.getTime();
        final Log logAlexToDavid1 = new Log(datastore, "First Visit",
                0, 3, 2, aprilFirst, "Was great! Alex and David had a good time.");

        calendar.set(2024, Calendar.APRIL, 4);
        final Date aprilFourth = calendar.getTime();
        final Log logAlexToDavid2 = new Log(datastore, "Routine Check In",
                0, 3, 1, aprilFourth, "Alex dropped by to chat with David.");
        final Log logBerniceToIrfan = new Log(datastore, "First Visit",
                1, 4, 3, aprilFourth, "Bernice and Irfan warmed up to each other.");


        return new Log[]{
            logAlexToDavid1,
            logAlexToDavid2,
            logBerniceToIrfan
        };

    }


    public static ReadOnlyDatastore getSampleDatastore() {
        Datastore sampleDs = new Datastore();

        PersonStore sampleAb = sampleDs.getMutablePersonStore();
        for (Person samplePerson : getSamplePersons()) {
            sampleAb.addPerson(samplePerson);
        }

        LogStore sampleLb = sampleDs.getMutableLogStore();
        for (Log sampleLog : getSampleLogs(sampleDs)) {
            sampleLb.addLog(sampleLog);
        }

        return sampleDs;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
