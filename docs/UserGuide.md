---
layout: page
title: User Guide
---

## **Introduction - What is Elder Scrolls?**

**Elder Scrolls**, our Volunteer Management System (VMS), simplifies organizing volunteers and befriendees. Developed for efficiency by our team, it streamlines coordination and fosters community connections.

Elder Scrolls, is a desktop application designed for efficient management. Optimized for use via a Command Line Interface (CLI), Elder Scrolls combines the speed of CLI interaction with the benefits of a Graphical User Interface (GUI). Whether you prefer the agility of typing or the convenience of visual interaction, Elder Scrolls ensures that your volunteer management tasks are completed swiftly and seamlessly.

No more cumbersome scheduling or scattered communication. Manage volunteers and befriendees seamlessly in one intuitive platform. Say goodbye to endless emails and spreadsheets – Elder Scrolls centralizes tasks, making them faster and more effective. Experience efficient volunteer management – where organizing, coordinating, and connecting has never been easier.

## **About this User Guide**


Welcome to the user guide for Elder Scrolls! Whether you're new or experienced, this guide has everything you need to make the most of Elder Scrolls:

* Quick Start: Get started with Elder Scrolls quickly and easily.
* Features: Explore all the functionalities of Elder Scrolls.
* Command Summary: Find all the essential commands at a glance.
* FAQs: Get answers to common questions about Elder Scrolls.

Let's dive in and maximize your Elder Scrolls experience!

--------------------------------------------------------------------------------------------------------------------

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **1. Setting Up**

Before getting started with Elder Scrolls, let's ensure everything is set up for Elder Scrolls to run correctly:

1. Ensure you have `Java 11` installed on your computer. This is crucial for Elder Scrolls to function properly.
    * If you're unsure whether Java 11 is installed, follow this short [guide](https://www.baeldung.com/java-check-is-installed) to check.
    * Install Java 11 (if needed): If Java 11 is not installed, follow the provided installation instructions [here](https://docs.oracle.com/en/java/javase/11/install/overview-jdk-installation.html#GUID-8677A77F-231A-40F7-98B9-1FD0B48C346A). If you're using a Mac computer with Apple Silicon, you can download Java 11 from [here](https://www.azul.com/downloads/?version=java-11-lts&os=macos&architecture=arm-64-bit&package=jdk-fx#zulu) instead.
2. Next, download our latest `elderscrolls.jar` release [here](https://github.com/AY2324S2-CS2103T-T09-3/tp/releases).
3. Next, copy the downloaded `elderscrolls.jar` to the desired home folder for Elder Scrolls. Elder Scrolls will store all application files and data in this folder, so it is best to create an empty folder for this purpose.

--------------------------------------------------------------------------------------------------------------------

## **2. Quick Start**

Once you've completed the setup, you're ready to launch Elder Scrolls! Follow these steps to get started:

1. Launch Elder Scrolls: Open a command terminal, `cd` into the folder you put the jar file in, and use the `java -jar elderscrolls.jar` command to run the application.<br>

A GUI similar to the below should appear in a few seconds. If this is your first time launching Elder Scrolls, the application should contain some sample data to get you started!

![Ui](images/Ui.png)

After this, you're all set to begin using Elder Scrolls! Let's make managing volunteers and befriendees a breeze.

Here are some commands to get you started:

   * `find David`: Finds all contacts with names containing `David`.

   * `list` : Lists all befriendee and volunteer contacts.

   * `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01 r/volunteer` : Adds a volunteer named `John Doe` to the Elder Scrolls.

   * `delete 4 r/volunteer ` : Deletes the 4th contact shown in the current list.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

   * Refer to the [Features](#features) below for details of each command.

Great! Now that you're familiar with the fundamental commands and have successfully launched Elder Scrolls, let's delve deeper into its intricacies and explore its advanced functionalities.

--------------------------------------------------------------------------------------------------------------------

## **3. User Interface**

Elder Scrolls has a simple and intuitive user interface. The GUI is divided into two main sections: the `Persons` section and the `Logs` section.


## **4. Features**

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are placeholders to represent parameters that should be supplied by the user.<br>
  e.g. in `add n/NAME`, `NAME` is a parameter which can be used as `add n/John Doe`.

* Items in square brackets are optional.<br>
  e.g `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items with `…`​ after them can be used multiple times including zero times.<br>
  e.g. `[t/TAG]…​` can be used as ` ` (i.e. 0 times), `t/friend`, `t/friend t/family` etc.

* Parameters can be in any order.<br>
  e.g. if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for commands that do not take in parameters (such as `help`, `list`, `exit` and `clear`) will be ignored.<br>
  e.g. if the command specifies `help 123`, it will be interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### 3.1 Volunteer / Befriendee Management

#### Adding a Volunteer or Befriendee: `add`

Adds a volunteer / Befriendee to the address book.

Format: `add n/NAME r/ROLE p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​`
Where `ROLE` must be either `volunteer` or `befriendee` to add a volunteer or befriendee respectively.
<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
A person can have any number of tags (including 0)
</div>

Examples:
* `add n/John Doe r/volunteer p/98765432 e/johnd@example.com a/John street, block 123, #01-01`
* `add n/Betsy Crowe r/befriendee t/friend e/betsycrowe@example.com a/Newgate Prison p/1234567 t/criminal t/homicide`

#### Editing a person : `edit`

Edits an existing person in Elder Scrolls.

Format: `edit INDEX r/ROLE [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`

* Edits the person at the specified `INDEX`. The index refers to the index number shown in the displayed person list. The index **must be a positive integer** 1, 2, 3, …​
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* When editing tags, the existing tags of the person will be removed i.e adding of tags is not cumulative.
* You can remove all the person’s tags by typing `t/` without
    specifying any tags after it.

Examples:
*  `edit 1 r/volunteer p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st person to be `91234567` and `johndoe@example.com` respectively.
*  `edit 2 r/befriendee n/Betsy Crower t/` Edits the name of the 2nd person to be `Betsy Crower` and clears all existing tags.

#### Pairing a befriendee and volunteer : `pair`

Pairs an existing befriendee and volunteer in Elder Scrolls.

Format: `pair BEFRIENDEE_INDEX VOLUNTEER_INDEX`

* The index refers to the index number shown in the displayed person list.
* The Person at `BEFRIENDEE_INDEX` must be a volunteer and the Person at `VOLUNTEER_INDEX` must be a befriendee.
* Neither of the two Persons must be paired, if they are, they must be unpaired before pairing again.

Examples:
*  `pair 1 2` Pairs the befriendee at Index 1 of the befriendee list and the volunteer at Index 2 of the volunteer list.
*  `pair 3 3` Pairs the befriendee at Index 3 of the befriendee list and the volunteer at Index 3 of the volunteer list.

#### Unpairing a befriendee and volunteer : `unpair`

Pairs an existing befriendee and volunteer in Elder Scrolls.

Format: `unpair BEFRIENDEE_INDEX VOLUNTEER_INDEX`

* The index refers to the index number shown in the displayed person list.
* The Person at `BEFRIENDEE_INDEX` must be a volunteer and the Person at `VOLUNTEER_INDEX` must be a befriendee.
* The befriendee and volunteer must be paired with each other before they can be unpaired.

Examples:
*  `unpair 1 2` Unpairs the befriendee at Index 1 of the befriendee list and the volunteer at Index 2 of the volunteer list.
*  `unpair 3 3` Unpairs the befriendee at Index 3 of the befriendee list and the volunteer at Index 3 of the volunteer list.

#### Listing all persons : `list`

Shows a list of all persons in Elder Scrolls.

Format: `list`

* Persons are listed in the order they were added.
* The list command is commonly used with the `find` command to reset the view after a search.


#### Locating persons by name: `find`

Find all persons whose names contain any of the given keywords. The find command also supports searches in the two
separate Volunteer and Befriendee lists, if the role is specified. It also supports search by tags, and by pairing status.
The order in which the role, tags or pair flag is specified does not matter.

Format: `find [r/ROLE] [t/TAG] [--paired]/[--unpaired] KEYWORD [MORE_KEYWORDS]...`

* An alias for the command is `search`.
* The search is **case-insensitive**. The order of the keywords also does not matter. e.g. `hans bo` will match `Bo Hans`
* If the **role** is specified, the search will be limited to the specified respective List. The other list remains unaffected.
* If a **pairing status** is specified, the search will narrow down to either paired or non-paired persons. 
* Keyword only searches on name. Search via tag is also supported, by adding in appropriate tags..
* Only full words will be matched e.g. `Han` will not match `Hans`
* Persons matching at least one keyword will be returned (i.e. `OR` search).
  e.g. `Hans Bo` will return `Hans Gruber`, `Bo Yang`

Examples:
* `find John` returns `john` and `John Doe`
* `find r/volunteer John` returns only the `John Doe` present in the volunteer list. 
* `find t/friend` returns all persons with the tag `friend`.
* `find --paired` returns all persons who are paired.
* Mix and match to customize your search! e.g. `find r/volunteer t/friend --paired John` returns all 
paired volunteers with the tag `friend` and name `John`.
* `find alex david` returns: <br>
  ![result for 'find alex david'](images/findAlexDavidResult.png)

<div markdown="span" class="alert alert-primary">:bulb: **Pro-Tip:**
Use the `list` command to reset your view after using the `find` command.
</div>

#### Deleting a person : `delete`

Deletes the specified person from the address book.

Format: `delete INDEX r/ROLE`

* Deletes the person at the specified `INDEX`.
* The index refers to the index number shown in the displayed person list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `list` followed by `delete 2 r/volunteer` Deletes the 2nd volunteer in the address book.
* `find Betsy` followed by `delete 1 r/befriendee` Deletes the 1st befriendee in the results of the `find` command.

### 3.2 Log Management

#### Adding a log : `logadd`

Adds a log between a pair of befriendee and volunteer.  
If it is the most recent log for a befriendee or volunteer, their `latest log` section will also be updated accordingly.

Format: `logadd BEFRIENDEE_INDEX VOLUNTEER_INDEX t/TITLE s/START_DATE d/DURATION r/REMARKS`

* The index refers to the index number shown in the displayed person list.
* The Person at `BEFRIENDEE_INDEX` must be a befriendee and the Person at `VOLUNTEER_INDEX` must be a volunteer.
* The two Persons must be paired before a log can be added.
* The index **must be a positive integer** 1, 2, 3, …​
* The `START_DATE` must be in the format `YYYY-MM-DD`.
* The `DURATION` **must be a positive integer** 1, 2, 3, …​

Examples:
* `logadd 1 1 t/Movies s/2020-01-09 d/3 r/had popcorn` Adds a log between the befriendee at Index 1 and the volunteer at Index 1 with the title `Movies`, start date `2020-01-09`, duration `3` and remarks `had popcorn`.
* `logadd 2 3 t/Shopping s/2020-09-09 d/2 r/bought groceries` Adds a log between the befriendee at Index 2 and the volunteer at Index 3 with the title `Shopping`, start date `2020-09-09`, duration `2` and remarks `bought groceries`.

#### Editing a log : `logedit`

Edits an existing log in Elder Scrolls.

Format: `logedit INDEX [t/TITLE] [s/START_DATE] [d/DURATION] [r/REMARKS]`

* Edits the log at the specified `INDEX`. The index refers to the index number shown in the displayed log list. The index **must be a positive integer** 1, 2, 3, …​
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* The `START_DATE` must be in the format `YYYY-MM-DD`.
* The `DURATION` **must be a positive integer** 1, 2, 3, …​

Examples:
* `logedit 1 t/Movies s/2020-01-09 d/3 r/had popcorn` Edits the title, start date, duration and remarks of the 1st log to be `Movies`, `2020-01-09`, `3` and `had popcorn` respectively.
* `logedit 2 t/Shopping s/2020-09-09 d/2` Edits the title, start date and duration of the 2nd log to be `Shopping`, `2020-09-09` and `2` respectively.

#### Deleting a log : `logdelete`, `logdel`, `logremove`, `logrm`

Deletes the specified log from the address book.

Format: `delete INDEX`

* Deletes the log at the specified `INDEX`.
* The index refers to the index number shown in the displayed log list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `list` followed by `logdelete 1` deletes the 1st log in the address book.
* `logfind 1 r/befriendee` followed by `logdelete 1` deletes the 1st log in the results of the `logfind` command.

#### Finding a log associated with a person: `logfind`

Find all logs associated with a person.

Format: `logfind INDEX r/ROLE`

* The index refers to the index number shown in the displayed person list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `logfind 1 r/befriendee` returns all logs associated with the befriendee at Index 1.

### 3.3 Undo and Redo commands
#### Undo the most recent command : `undo`

Undo the latest command that made a change to the data stored in Elder Scrolls.
This excludes commands like `list`,`find` and `logfind` which do not mutate the state of any data in Elder Scrolls.

Format: `undo`

* The persons list and logs list will be refreshed to show all entries.
* There must have been a previous command executed that modified data in Elder Scrolls.
* At every launch of the application, there will be no commands to be undone.
* Undo history will be erased when you exit the application.

Example:
* You have just mistakenly executed a `clear` command and cleared every entry in Elder Scrolls. You can simply execute the command `undo` to revert the changes and all your entries in Elder Scrolls will be restored to their previous state, before the `clear` command was executed.
* If you've just performed multiple `delete` commands in sequence, and realised you've deleted the wrong entries. You can execute simultaneous `undo` commands to revert the changes made by the wrongful delete commands.

#### Revert the most recent undo command : `redo`

Reverts the data stored in Elder Scrolls back to its state before the latest undo command was executed.

Format: `redo`

* The persons list and logs list will be refreshed to show all entries.
* When a command that modifies data in Elder Scrolls is executed after an undo command is executed, the redo command will no longer be available.
* There must have been a previous undo command executed in Elder Scrolls.
* Redo history will be erased when you exit the application.

Examples:
* Let's say you've just executed a successful `undo` command. If you were to call any command that modifies the data in Elder Scrolls, such as `delete`, the `redo` command will not longer be available.
* Let's say you've just executed a `add`, and mistakenly executed a `undo` command right after. You can then call `redo` to once again execute the `add` that was previously undone.

### 3.4 Other Commands: Help and Exiting

#### Viewing help : `help`

Shows a message explaning how to access the help page.

![help message](images/helpMessage.png)

Format: `help`

#### Clearing all entries : `clear`

Clears all entries from Elder Scrolls.

Format: `clear`

#### Exiting the program : `exit`

Exits the program.

Format: `exit`

## **4. Saving the data**

Elder Scrolls data are saved in the hard disk automatically after any command that changes the data. There is no need to save manually!

## **5. Editing the data file**

All application data is saved automatically as a JSON file `[JAR file location]/data/datastore.json`. Advanced users are welcome to update data directly by editing that data file.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes to the data file makes its format invalid, Elder Scrolls will discard all data and start with an empty data file at the next run. Hence, it is recommended to take a backup of the file before editing it.<br>
Furthermore, certain edits can cause the Elder Scrolls to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</div>

## **6. FAQ**

**Q**: How do I transfer my data to another Computer?<br>
**A**: Install the app in the other computer and overwrite the empty data file it creates with the file that contains the data of your previous Elder Scrolls home folder.

## **7. Known issues**

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.


## **8. Command summary**

Action | Format, Examples
--------|------------------
**Add** | `add n/NAME r/ROLE p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​` <br> e.g., `add n/James Ho r/volunteer p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 t/friend t/colleague`
**Edit** | `edit INDEX r/ROLE [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`<br> e.g.,`edit 2 n/James Lee e/jameslee@example.com`
**Pair** | `pair BEFRIENDEE_INDEX VOLUNTEER_INDEX`<br> e.g., `pair 1 2`
**Unpair** | `unpair BEFRIENDEE_INDEX VOLUNTEER_INDEX`<br> e.g., `unpair 1 2`
**Delete** | `delete INDEX r/ROLE`<br> e.g., `delete 3 r/befriendee`
**Find** | `find [r/ROLE] [t/TAG] [--paired]/[--unpaired] KEYWORD [MORE_KEYWORDS]...` <br> e.g., `find r/volunteer --paired James`
**List** | `list`
**LogAdd** | `logadd BEFRIENDEE_INDEX VOLUNTEER_INDEX t/TITLE s/START_DATE d/DURATION r/REMARKS`<br> e.g., `logadd 1 2 t/Movies s/2020-01-09 d/3 r/had popcorn`
**LogEdit** | `logedit INDEX [t/TITLE] [s/START_DATE] [d/DURATION] [r/REMARKS]`<br> e.g., `logedit 1 t/Movies s/2020-01-09 d/3 r/had popcorn`
**LogDelete** | `logdelete INDEX`<br> e.g., `logdelete 1`
**LogFind** | `logfind INDEX r/ROLE`<br> e.g., `logfind 1 r/befriendee`
**Clear** | `clear`
**Help** | `help`

## **9. Glossary**
