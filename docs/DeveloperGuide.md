---
layout: page
title: Developer Guide
---

## **Table of Contents**

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------
[//]: # (Page Break:)
<div style="page-break-after: always;"> </div> 

## **Acknowledgements**

* Elder Scrolls is based on the [AddressBook-Level3 project](https://nus-cs2103-ay2324s2.github.io/tp/) created by the [SE-EDU initiative](https://se-education.org).
* The design of the Elder Scrolls user interface takes cues from Google's [Material Design system](https://m3.material.io/).

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams in this document `docs/diagrams` folder. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.
</div>

[//]: # (Page Break:)
<div style="page-break-after: always;"> </div> 

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete 1 r/volunteer`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `LogListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` and `Log` objects that reside in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete 1 r/volunteer")` API call as an example.

![Interactions Inside the Logic Component for the `delete 1 r/volunteer` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</div>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/AY2324S2-CS2103T-T09-3/tp/src/main/java/scrolls/elder/model/Model.java)

<img src="images/ModelClassDiagram.png" width="600" />


The `Model` component,

* stores all in-memory application data (i.e., `Datastore` and `UserPref` objects)
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* stores a `Datastore` object that represents the functional data in an application. This is exposed to the outside as a `ReadOnlyDatastore` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

#### Datastore

<img src="images/DatastoreClassDiagram.png" width="600" />

Contains the `PersonStore`:

* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.

Contains the `LogStore`:

* stores the currently 'selected' `Log` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Log>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.

<div markdown="span" class="alert alert-info">:information_source: **Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>


### Storage component

**API** : [`Storage.java`](https://github.com/AY2324S2-CS2103T-T09-3/tp/master/src/main/java/scrolls/elder/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,
* can save both application data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `DatastoreStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `scrolls.elder.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Add Person feature

#### Implementation

**`AddCommand` Class:** <br>

The `AddCommand` class is responsible for adding a new `Person` into the application and the `AddCommandParser` class is responsible for parsing the user input to create a `AddCommand` object.

The following sequence diagram shows how an add operation goes through the `Logic` component:

![AddSequenceDiagram](images/AddSequenceDiagram-Logic.png)

<div style="text-align:center;">
  <img src="images/AddSequenceDiagram2.png" alt="AddSequenceDiagram2" width="600">
</div>


#### Design considerations:

**Aspect: Should the name attribute be case-sensitive?:**

* **Alternative 1 (current choice):** Name attribute is not case-sensitive.
    * Pros: Prevents accidental capitalization or de-capitalization of names.
    * Cons: In cases where two people have the same name with different capitalization, it is not accepted.

* **Alternative 2:** Name attribute is case-sensitive.
  itself.
    * Pros: Two people who have the same name with different capitalization is allowed in the application.
    * Cons: Accidental capitalization can cause duplicate entries in the application.

### Edit Person feature

#### Implementation

**`EditCommand` Class:** <br>

The `EditCommand` class is responsible for editing the attributes of an existing `Person` in the application and the `EditCommandParser` class is responsible for parsing the user input to create a `EditCommand` object.

The following sequence diagram shows how an edit operation goes through the `Logic` component:

![EditSequenceDiagram](images/EditSequenceDiagram-Logic.png)

<div style="text-align:center;">
  <img src="images/EditSequenceDiagram2.png" alt="EditSequenceDiagram2" width="600">
</div>


#### Design considerations:

**Aspect: How should the name in latest logs and log list be updated if name is edited:**

* **Alternative 1 (current choice):** Logs are updated during the execution of EditCommand.
    * Pros: Edited name is reflected in the latest logs and log list immediately.
    * Cons: Increases the complexity of implementation of the EditCommand.

* **Alternative 2:** Logs are updated in a subsequent command
  itself.
    * Pros: Simplifies the implementation of the EditCommand and a separation of log and person commands.
    * Cons: Real-time information in logs may not be updated.


### Delete Person feature

#### Implementation

**`DeleteCommand` Class:** <br>

The `DeleteCommand` class is responsible for removing an existing `Person` from the application and the `DeleteCommandParser` class is responsible for parsing the user input to create a `DeleteCommand` object.

The following sequence diagram shows how a delete operation goes through the `Logic` component:

![DeleteSequenceDiagram](images/DeleteSequenceDiagram-Logic.png)

<div style="text-align:center;">
  <img src="images/DeleteSequenceDiagram2.png" alt="DeleteSequenceDiagram2" width="600">
</div>


#### Design considerations:

**Aspect: Should users be allowed to delete a person with an existing pair and logs:**

* **Alternative 1 (current choice):** Only allow deletion of a person if they are not paired with anyone and have no logs.
    * Pros: Prevents accidental deletion who is still a part of the volunteer programme.
    * Cons: To delete a person with logs, the user must first delete logs associated with that person.
  
* **Alternative 2:** Deletion of a person with an existing pair and logs is allowed.
  itself.
    * Pros: Simplifies the deletion process.
    * Cons: Accidental deletion of a person who is still a part of the volunteer programme is possible.


### Find feature

#### Implementation

**`FindCommandParser` Class:** <br>
The `FindCommandParser` class is responsible for parsing user input and creating a corresponding `FindCommand` object for execution. The implementation involves several key steps:
* **Role Parsing**: The parser first identifies the roles specified in the user input, such as "volunteer" or "befriendee", using predefined flags (`SEARCH_VOLUNTEER_FLAG` and `SEARCH_BEFRIENDEE_FLAG`).
It determines whether the search should be restricted to a specific role, both roles, or neither.
* **Pair Flag Parsing**: Next, the parser extracts any flags indicating whether the search should include paired or unpaired individuals.
It determines whether the search should include paired individuals, unpaired individuals, both, or neither.
* **Tag Parsing**: The parser then parses any tags specified in the user input, which are prefixed with a tag indicator (`t/`). Tags are extracted and used to create a `TagListContainsTagsPredicate` object for filtering.
* **Name Parsing**: After extracting roles, pair flags, and tags, the parser processes the remaining input as potential name keywords for filtering.
* **Search Criteria Combination**: The parser combines the parsed search criteria (roles, pair flags, tags, and name keywords) into a single `FindCommand` object.
* **Handling Search Exceptions**: The parser checks for any invalid command formats or missing input parameters and throws a `ParseException` if necessary.
* **Return `FindCommand` Object**: Finally, the parser returns the constructed `FindCommand` object, encapsulating the parsed search criteria, for further execution.

**`FindCommand` Class:** <br>
The find command finds all persons whose names contain any of the given keywords, supports searches in separate Volunteer and Befriendee lists, search by tags, and by pairing status. The `FindCommand` class is responsible for executing the find operation based on parsed user input.

* **Command Execution:** The `execute()` method overrides the parent class Command method to perform the actual find operation.
It retrieves the PersonStore from the model to access the list of persons.
* **Search Criteria:** The class contains boolean flags (`isSearchingVolunteer`, `isSearchingBefriendee`) to determine whether to search volunteers, befriendees, or both.
Similar flags (`isSearchingPaired`, `isSearchingUnpaired`) are used to filter persons based on their pairing status.
* **Search Methods:*** `searchAllPersons()`, `searchVolunteerOnly()`, and `searchBefriendeeOnly()` methods are used to perform searches based on the specified criteria.
These methods update the filtered person list in the PersonStore based on the search predicates.
* **Result Handling:** The search results are wrapped in a `CommandResult` object, which contains a message indicating the number of persons found.
The message is formatted using the Messages class constants.

The following sequence diagram shows how a Find operation goes through the `Logic` component:

![FindSequenceDiagram1](images/FindSequenceDiagram.png)

<div style="text-align:center;">
  <img src="images/FindSequenceDiagram2.png" alt="FindSequenceDiagram2" width="600">
</div>



#### Design Considerations

**Aspect: Tag Parsing in FindCommandParser:** <br>

* **Alternative 1: (Current Choice):** Parsing tags separately from other search criteria.
  * Pros: Allows users to specify additional search criteria based on tags.
  * Cons: Requires additional parsing logic and potentially increases complexity in handling multiple search parameters.

* **Alternative 2:** Incorporating tags into the main search query without separate parsing.
  * Pros: Simplifies parsing logic by integrating tags directly into the search query.
  * Cons: May limit flexibility in specifying tag-based search criteria or require more sophisticated parsing algorithms.

**Aspect: Handling Search Criteria in FindCommand:** <br>

* **Alternative 1 (Current Choice)**: Implementing separate predicates for name and tag search criteria.
  * Pros: Offers flexibility in defining and combining different search parameters.
  * Cons: May lead to redundant filtering or increased complexity in managing multiple predicates.

* **Alternative 2**: Consolidating search criteria into a single unified predicate.
  * Pros: Simplifies filtering logic by reducing the number of separate predicates.
  * Cons: May limit the flexibility to apply different search parameters independently or require more complex predicate structures.



### Pair/Unpair feature

#### Implementation

**`PairCommand` & `UnpairCommand` Class:** <br>

The `PairCommand` class is responsible for pairing two persons, and the `UnpairCommand` class is responsible for unpairing two persons. The `PairCommand` and `UnpairCommand` classes are executed by the `Logic` component.

The pair and unpair commands are facilitated by updating the `pairedWithName` and `pairedWithId` fields of the `Person` object. Two paired `Person`s will have their `pairedWithName` and `pairedWithId` fields updated to reflect the `name` and `personId` of the `Person` they are paired with, or removed when they are unpaired

The `name` is saved to facilitate easy identification of the paired `Person` when displaying the `Person` object. The `personId` is saved to facilitate easy retrieval of the paired `Person` object when needed.

The following sequence diagram shows how a pair operation goes through the `Logic` component:

![PairSequenceDiagram](images/PairSequenceDiagram-Logic.png)

<div style="text-align:center;">
  <img src="images/PairSequenceDiagram2.png" alt="PairSequenceDiagram2" width="600">
</div>

For the unpair operation, the sequence diagram is similar to the pair operation, with the `UnpairCommand` class executing the unpair operation.

#### Design considerations:

**Aspect: What attribute(s) should be saved in the `Person` object:**

* **Alternative 1 (current choice):** Save the `name` and `personId` of the paired `Person`.
    * Pros: Might need extra steps to search for the paired `Person` as only the `name` and `personId` are saved.
    * Cons: Uses less memory.

* **Alternative 2:** Save the paired `Person` within the `Person` object.
  itself.
    * Pros: Easy to access and manipulate the paired `Person`.
    * Cons: Uses more memory and includes redundant information.

### Add Log feature

#### Implementation

The Add Log feature allows users to add a new log entry to the application.

The `LogEditCommand` class is responsible for creating a new log entry, and the `LogAddCommandParser` class is responsible for parsing the user input to create a `LogAddCommand` object.

The following sequence diagram shows how a LogAdd operation goes through the `Logic` component:

![LogAddSequenceDiagram](images/LogAddSequenceDiagram-Logic.png)

<div style="text-align:center;">
  <img src="images/LogAddSequenceDiagram2.png" alt="LogAddSequenceDiagram2" width="600">
</div>

#### Design Considerations

**Aspect: Whether to use unique `logId` for each `Log` object:**

* **Alternative 1 (current choice):** The `logId` is automatically generated for each `Log` object, ensuring that each log entry has a unique identifier.
    * Pros: Ensures that each log entry is uniquely identified.
    * Cons: Adds complexity to the implementation of the `LogStore` (e.g., maintaining a `logIdSequence` to generate unique `logId` values)

* **Alternative 2:** The `logId` is not used, and the `Log` object is identified by its position in the list of logs.
    * Pros: Simplifies the process of adding a log entry.
    * Cons: May lead to confusion if log entries are deleted or reordered.

Here's an example of how you can use the Add Log feature:

1. To add a log entry, use the `addlog` command followed by the `volunteerId`, `befriendeeId`, `startdate`, `duration` and `remarks`. For example, `addlog v/1 b/2 s/2022-03-01-10:00 d/2 r/Visited Mr. Tan`.

2. The application will create a new log entry with the provided details and add it to the list of logs.

3. If the command is successful, the application will display a message indicating that the log entry has been added.

Please note that the `volunteerId` and `befriendeeId` must correspond to existing volunteers and befriendees in the application. The `startdate` should be in the format `YYYY-MM-DD` and the `duration` should be the number of hours the visit lasted. The `remarks` field is optional and can be used to add any additional notes about the visit.

### Edit log feature

#### Implementation

The edit log feature allows user to modify the details of an existing log entry in the address book. The index of the log entry has to be edited is specified by the user in order to execute the `LogEditCommand`.

The `LogEditCommand` class is responsible for editing the details of a log entry,
and the `LogEditCommandParser` class is responsible for parsing the user input to create a `LogEditCommand` object.

The following sequence diagram shows how a LogEdit operation goes through the `Logic` component:

![LogEditSequenceDiagram](images/LogEditSequenceDiagram-Logic.png)

<div style="text-align:center;">
  <img src="images/LogEditSequenceDiagram2.png" alt="LogEditSequenceDiagram2" width="600">
</div>

#### Design Considerations

**Aspect: What attribute(s) can be edited in the `Log` object:**

* **Alternative 1 (current choice):** The `volunteerId`, `befriendeeId`, `startdate`, `duration` and `remarks` of the
    `Log` can be edited.
    * Pros: Provides flexibility for users to update various attributes of a log entry.
    * Cons: Accidental edits when editing contact ids in `Log` may lead to cascading effects in other
      attributes displayed, such as timeServed in volunteer contacts.
* **Alternative 2:** The `startdate`, `duration` and `remarks` of the `Log` can be edited, 
    while the `volunteerId`, `befriendeeId` is kept immutable.
    * Pros: Prevents cascading modifications of attributes displayed in `Person` contact.
    * Cons: Restricts the flexibility of the `LogEdit` feature

### Delete Log feature

#### Implementation

The Delete Log feature allows users to remove an existing log entry from the application.

The `LogDeleteCommand` class is responsible for deleting an existing log entry, and the `LogDeleteCommandParser` class is responsible for parsing the user input to create a `LogDeleteCommand` object.

The following sequence diagram shows how a LogDelete operation goes through the `Logic` component:

![LogDeleteSequenceDiagram](images/LogDeleteSequenceDiagram-Logic.png)

<div style="text-align:center;">
  <img src="images/LogDeleteSequenceDiagram2.png" alt="LogDeleteSequenceDiagram2" width="600">
</div>

#### Design Considerations

**Aspect: How to update the `latestLogId` attribute of Persons when log is deleted:**

* **Alternative 1 (current choice):** A helper function that finds the `latestLogId` attribute of the `Person` objects when a log is deleted, in order to update the latest log if necessary.
    * Pros: Ensures that `latestLogId`is accurately updated and displayed when logs are deleted.
    * Cons: Two new `Person` objects are created whenever a log is deleted

* **Alternative 2:** Leave the updating of the latest log to the `Model` component
    * Pros: Simplifies the process of deleting a log entry.
    * Cons: Another component of the application must be responsible for updating the `latestLogId` attribute of the `Person` objects.


### Find Log feature

#### Implementation

The Find Log feature allows users to find a particular log entry that is displayed in the log list in the application.

The `LogFindCommand` class is responsible for finding an existing log entry, and the `LogFindCommandParser` class is responsible for parsing the user input to create a `LogFindCommand` object.

The following sequence diagram shows how a LogFind operation goes through the `Logic` component:

![LogFindSequenceDiagram](images/LogFindSequenceDiagram-Logic.png)

<div style="text-align:center;">
  <img src="images/LogFindSequenceDiagram2.png" alt="LogFindSequenceDiagram2" width="600">
</div>

#### Design Considerations

**Aspect: How to specify what logs to find:**

* **Alternative 1 (current choice):** Logs of a specified person are displayed in the log list.
    * Pros: Allows for the display of a particular person's logs
    * Cons: Cannot search for a particular log

* **Alternative 2:** Search via the attributes of a log entry.
    * Pros: Allows for searching of logs with a particular attribute
    * Cons: Does not allow for the display of a particular person's logs


### Undo/Redo feature

#### Implementation

The undo/redo mechanism is facilitated by `DatastoreVersionStorage`. It is a data structure storing the undo/redo history of the datastores stored in the model, stored internally as an `datastoreVersions` and `currentStatePointer`. Additionally, it implements the following operations:

* `DatastoreVersionStorage#commitDatastore()` — Saves the current datastore state in its history.
* `DatastoreVersionStorage#executeUndo()` — Returns the previous address book state from its history, for the model to reset the datastore
* `DatastoreVersionStorage#executeRedo()` — Returns a previously undone address book state from its history, for the model to reset the datastore

`DatastoreVersionStorage#commitDatastore()` is exposed in the `Model` interface as `Model#commitDatastore()` while `Model#undoChanges()` and `Model#redoChanges()` call `DatastoreVersionStorage#executeUndo()` and `DatastoreVersionStorage#executeRedo()` respectively to carry out the undo and redo actions.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `DatastoreVersionStorage` will be initialized with the initial datastore state, and the `currentStatePointer` pointing to that single datastore state.

![UndoRedoState0](images/UndoRedoState0.png)

Step 2. The user executes `delete 5 r/volunteer` command to delete the 5th volunteer in the datastore. The `delete` command calls `Model#commitDatastore()`, causing the modified state of the datastore after the `delete 5 r/volunteer` command executes to be saved in the `datastoreVersions`, and the `currentStatePointer` is shifted to the newly inserted datastore state.

![UndoRedoState1](images/UndoRedoState1.png)

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitDatastore()`, causing another modified datastore state to be saved into the `datastoreVersions`.

![UndoRedoState2](images/UndoRedoState2.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not call `Model#commitDatastore()`, so the datastore state will not be saved into the `datastoreVersions`.

</div>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command.
The `undo` command will call `Model#undoChanges()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous datastore state, and restores the datastore to that state.

![UndoRedoState3](images/UndoRedoState3.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index 0, pointing to the initial Datastore state, then there are no previous Datastore states to restore. The `undo` command uses `Model#canUndoDatastore()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</div>

The following sequence diagram shows how an undo operation goes through the `Logic` component:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Logic.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</div>

Similarly, how an undo operation goes through the `Model` component is shown below:

![UndoSequenceDiagram](images/UndoSequenceDiagram-Model.png)

The `redo` command does the opposite — it calls `Model#redoChanges()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the datastore to that state.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index `datastoreVersions.size() - 1`, pointing to the latest datastore state, then there are no undone Datastore states to restore. The `redo` command uses `Model#canRedoDatastore()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</div>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the datastore, such as `list`, will usually not call `Model#commitDatastore()`, `Model#undoChanges()` or `Model#redoChanges()`. Thus, the `datastoreVersions` remains unchanged.

![UndoRedoState4](images/UndoRedoState4.png)

Step 6. The user executes `clear`, which calls `Model#commitDatastore()`. Since the `currentStatePointer` is not pointing at the end of the `datastoreVersions`, all address book states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

![UndoRedoState5](images/UndoRedoState5.png)

The following activity diagram summarizes what happens when a user executes a new command:

<img src="images/CommitActivityDiagram.png" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire address book.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix A: Requirements**

### Product scope

**Target user profile**:

* is a manager of a befriending volunteer organisation
* has a need to keep track of many befriendees/elderly of the volunteer programme
* has a need to keep track of volunteers in the organisation
* has a need to keep track of logs of activities/visits made by volunteers to befriendees
* prefer desktop apps over other types
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: manage the volunteer organisation faster than a typical mouse/GUI driven app. Our application aims to provide fast access to all contacts with intuitive search and filtering, enable pairing of volunteers to elderly befriendees, show multiple lists to enable multitasking and support efficient adding of logs between pairs of volunteers and befriendees.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​      | I want to …​                                                                | So that I can…​                                            |
|----------|-----------------|--------------------------------------------------------------------------------|---------------------------------------------------------------|
| `* * *`  | manager         | add volunteers’ and elderly befriendees’ contacts                              |                                                               |
| `* * *`  | manager         | edit volunteers’ and elderly befriendees’ details                              |                                                               |
| `* * *`  | manager         | delete volunteer and elderly befriendee contacts                               |                                                               |
| `* * *`  | manager         | view list of volunteers and elderly befriendees                                | keep track of the roster                                      |
| `* * *`  | manager         | tag elderly befriendees with relevant details                                  | accommodate for any special circumstances                     |
| `* * *`  | manager         | pair and unpair volunteers with befriendee contacts                            | assign the pairings                                           |
| `* * *`  | manager         | add log entries for each visit made by a volunteer to an elderly befriendee    | keep track of the visits                                      |
| `* * *`  | manager         | search for specific elderly befriendee or volunter befriender                  | find and view the details of befriendee or volunteer          |
| `* * *`  | manager         | search log entries based on a befriendee or volunteer                          | find and view the details of relevant log entries             |
| `* *`    | manager         | have a one-stop view of all important information regarding elderly and volunteers | contact each person easily                                |
| `* *`    | manager         | undo and redo changes made to the contacts, pairings, and logs                 | revert changes made in error                                  |
| `* *`    | first-time user | access a help page                                                             | refer to the features that come along with the application    |
| `* *`    | first-time user | see sample befriendee and volunteer profiles                                   | try out the features with pre-loaded data                     |
| `*`      | first-time user | follow a guided tour                                                           | be aware of how to use and access features in the application |


### Use cases

(For all use cases below, the **System** is the `Elder Scrolls` and the **Actor** is the
`volunteer organisation manager`, unless specified otherwise)

**Use case: UC01 - Add a contact**

**MSS**

1. User requests to add contact, entering contact information
2. Elder Scrolls adds the contact
3. Elder Scrolls displays the details of the contact added

    Use case ends.

**Extensions**

* 2a. The given contact details are invalid.

  * 2a1. Elder Scrolls shows an error message.

    Use case ends.

* 3a. The given contact details are already in the contact book

    * 3a1. Elder scrolls shows an error message.

     Use case ends.

**Use case: UC02 - Delete a contact**

**MSS**

1.  User requests to list all contacts
2.  Elder Scrolls shows a list of all contacts
3.  User requests to delete a specific contact.
4.  Elder Scrolls deletes the specified contact.

    Use case ends.

**Extensions**

* 3a. The specific contact given by the user is invalid.

    * 3a1. Elder Scrolls shows an error message.

      Use case resumes at step 2.

* 4a. The contact requested to be deleted is still paired.

    * 4a1. Elder Scrolls shows an error message.
    
      Use case resumes at step 2.

**Use case: UC03 - List all contacts and logs**

**MSS**

1. User requests to list all contacts and logs
2. Elder Scrolls shows a list of all contacts and logs

   Use case ends.

**Use case: UC04 - Pair a volunteer and befriendee**

**MSS**

1.  User <u>requests to list all contacts and logs (UC03)</u>.
2.  Elder Scrolls shows a list of contacts and logs.
3.  User requests to pair a specific volunteer and befriendee in the list.
4.  Elder Scrolls pairs the specified volunteer and befriendee.

    Use case ends.

**Extensions**

* 3a. One or both of the specified persons are invalid.

    * 3a1. Elder Scrolls shows an error message indicating that the specified person(s) given are invalid.

      Use case resumes at step 2.

* 3b. One or both of the specified persons are already paired.

    * 3b1. Elder Scrolls shows an error message indicating that the specified person(s) are already paired.

      Use case resumes at step 2.

**Use case: UC05 - Edit field for a specific contact**

**MSS**

1.  User <u>requests to list all contacts and logs (UC03)</u>.
2.  Elder Scrolls shows a list of contacts and logs.
3.  User requests to edit a field for a specific contact, with a new specified value.
4.  Elder Scrolls edits the field for the specified person in the list with the new specified value.

    Use case ends.

**Extensions**

* 3a. The specific contact given by the user is invalid.
    * 3a1. Elder Scrolls shows an error message.
      Use case resumes at step 2.
  
* 3b. The field that the user wants to edit is not specified
    * 3b1. Elder Scrolls shows an error message to prompt the user to specify a field to edit.
      Use case resumes at step 2.
  
* 3c. The new specified value is not provided by the user
    * 3c1. Elder Scrolls shows an error message to prompt the user to specify a new value for the field the user wishes to edit.
      Use case resumes at step 2. 


**Use case: UC06 - Add a log entry for volunteer-befriendee pairs**

**MSS**

1. User <u>requests to list all contacts and logs (UC03)</u>.
2. Elder Scrolls shows a list of contacts and logs.
3. User requests to add a log entry for two paired individuals, and enters details of the activity log, including date, time, and remarks.
4. Elder Scrolls records the log entry for the selected pair.

   Use case ends.

**Extensions**

* 3a. The selected pair is not currently paired.
    * 3a1. Elder Scrolls displays an error message indicating that the selected pair is not currently paired.
      Use case resumes at step 2.

* 3b. The entered details for the log are incomplete or invalid.
    * 3b1. Elder Scrolls shows an error message to prompt user to correct the incomplete or invalid details.
      Use case resumes at step 2.

**Use case: UC07 - Find a person based on name keyword**

**MSS**

1. User requests to find a person based on a name keyword.
2. Elder Scrolls displays a list of contacts matching the name keyword.

   Use case ends.

**Extensions**

* 1a. User wants to find person specifically amongst the volunteer.
    * 1a1. User specifies the role as volunteer.
    * 1a2. Elder Scrolls displays a list of volunteers matching the name keyword.
      Use case ends.
* 1b. User wants to find persons that are paired.
    * 1b1. User specifies the pairing status as paired.
    * 1b2. Elder Scrolls displays a list of paired persons matching the name keyword.
      Use case ends.
* 1c. User wants to find persons that are tagged with a specific tag.
    * 1c1. User specifies the tag as a property.
    * 1c2. Elder Scrolls displays a list of persons tagged with the specified tag.
      Use case ends.

**Use case: UC08 - Find associated logs to a specific person**

**MSS**

1. User requests to find logs associated with a specific person.
2. Elder Scrolls displays a list of logs associated with the specific person.

   Use case ends.

**Use case: UC09 - Edit field for a specific log**

**MSS**

1.  User <u>requests to list all contacts and logs (UC03)</u>.
2.  Elder Scrolls shows a list of contacts and logs.
3.  User requests to edit a field in a specific log, with a new specified value.
4.  Elder Scrolls edits that field for that specific log in the list with the new specified value.
    
    Use case ends.

**Extensions**

* 3a. The given specific log is invalid.
    * 3a1. Elder Scrolls shows an error message.
      Use case resumes at step 2.

* 3b. The field that the user wants to edit is not specified
    * 3b1. Elder Scrolls shows an error message to prompt the user to specify a field to edit.
      Use case resumes at step 2.

**Use case: UC10 - Undo a command**

**MSS**

1. User requests to execute a command.
2. Elder Scrolls executes the command.
3. User requests to undo the command.
4. Elder Scrolls undoes the command.
   
   Use case ends

**Extensions**

* 3a. The command that was previously executed does not mutate data in the application
    * 1a1. Elder Scrolls shows an error message to tell the user there is no previous operation to be undone.
      Use case resumes at step 2.

**Use case: UC11 - Redo a command**

**MSS**

1. User <u>requests to undo a command (UC10)</u>.
2. Elder Scrolls undoes the command.
3. User requests to revert the effects of the undo command.
4. Elder Scrolls reverts the effect of the undo command.

   Use case ends.

### Non-Functional Requirements

1. Should work on any _mainstream OS_ as long as it has Java `11` or above installed.
2. Should be able to hold up to 1000 persons without a noticeable sluggishness in performance for typical usage.
3. A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4. Should have a simple menu structure with clear labels, guiding users to key actions without extensive tutorials.
5. Should have graceful error handling with clear human-readable messages to the user to guide them in fixing their command.
6. Should have a robust data storage mechanism that can handle data corruption and large data sets.

### Glossary

* **Volunteer**: An individual who offers their time and services to social service agencies or causes without financial compensation, in this context they carry out befriending activities with the beneficiaries.
* **Befriendee**: An individual who receives support, companionship, or assistance from volunteers, in this context they are the beneficiaries of the befriending activities.
* **Elder Scrolls**: The Volunteer Management System (VMS) developed by our team for efficient management and bookkeeping of volunteers, befriendees, and their interactions.
* **Tagging**: Adding an arbitrary detail(s) to a volunteer or befriendee profile to aid in identifying special conditions
* **Index**: The position or number assigned to each item in a list, used for reference when performing actions such as editing or deleting entries in Elder Scrolls.
* **Pairing**: The process of associating a volunteer with a befriendee in Elder Scrolls, allowing them to work together on activities or support services.
* **Logs**: Records of interactions, activities, or events between volunteers and befriendees in Elder Scrolls, used for tracking service hours, progress, and communication.
* **Command Line Interface (CLI)**: A text-based interface used for interacting with Elder Scrolls through commands typed into a terminal or command prompt.
* **Graphical User Interface (GUI)**: A visual interface used for interacting with Elder Scrolls, providing intuitive controls and displays for managing volunteers, befriendees and logs.


--------------------------------------------------------------------------------------------------------------------

## **Appendix B: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown
1. Initial launch
   1. Download the jar file and copy into an empty folder
   1. Launch Elder Scrolls: Open a command terminal, `cd` into the folder you put the jar file in, and use the `java -jar elderscrolls.jar` command to run the application. Expected: Shows the GUI with a set of sample contacts and logs. The window size may not be optimum.

1. Saving window preferences
   1. Resize the window to an optimum size. Move the window to a different location. Close the window.
   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

### View sample Volunteer and Befriendee profiles and sample Logs
1. Close Elder Scrolls.
2. Delete the file `./data/datastore.json` if it exists.
3. Launch Elder Scrolls.
4. Click on the `Volunteers` tab.
   * Expected: Lists of sample volunteers, befriendees and logs are shown.

### Find a specific person based on name keywords, pairing status, or tags
1. Prerequisites: Starting with sample data. Refer to the previous test case to load sample data. Use `list` to reset the view before each testcase.
1. Find a volunteer by name keywords
   1. Test case: `find`
      Expected: No change in list. Error displayed show an invalid command format, where at least one of the optional parameters must be entered. 
   1. Test case: `find David`
      Expected: Shows the list of volunteers and befriendees with the name "David".
   1. Test case: `find david alex`
      Expected: Shows the list of volunteers and befriendees with either the name "Alice" or "David".

2. Find a person by pairing status
   1. Test case: `find --paired`
      Expected: Shows a list of all paired volunteers and befriendees. The list may contain multiple paired persons.
   1. Test case: `find --unpaired`
      Expected: Shows a list of all unpaired volunteers and befriendees. The list may contain multiple unpaired persons.

3. Find a person by tags
   1. Test case: `find t/student`
      Expected: Shows a list of all volunteers and befriendees with the tag "student".

4. Restrict find operation to volunteers or befriendees
   1. Test case: `find r/volunteer --paired`
      Expected: Shows list of all volunteers that are paired. Befriendee list is untouched.
   1. Test case: `find r/befriendee t/handicapped`
      Expected: Shows list of all befriendees that have tag `handicapped`. Volunteer list is untouched.

### Adding a person
1. Adding a person while all befriendees and volunteers are being shown

    1. Prerequisites: List all persons using the `list` command. Multiple persons in both the befriendees and volunteers list.

    1. Test case: `add n/Michelle r/volunteer p/98909221 e/mich@gmail.com a/Wow street, blk 123, t/smart`<br>
       Expected: Contact with the above details is added to the volunteer list. Details of the added contact shown in the status message.

    1. Test case: `add n/Giggs p/88221143 e/gig@hotmail.com a/Hello street, block 4, #04-01`<br>
       Expected: No person is added. Error details indicating "Invalid command format!" shown in the status message. Status bar remains the same.

    1. Other incorrect add commands to try: `add`, `add n/`<br>
       Expected: Similar to previous.
   
2. Adding a person where the name already exists in the volunteer list

    1. Prerequisites: List all persons using the `list` command. There exists a volunteer contact where the name is "Michelle".

    1. Test case: `add n/Michelle r/volunteer p/98909221 e/mich@gmail.com a/Wow street, blk 123, t/smart`<br>
       Expected: No person is added. Error details shown in the status message. Status bar remains the same.

### Editing a person
1. Editing a person while all befriendees and volunteers are being shown

    1. Prerequisites: List all persons using the `list` command. The person indicated to be edited

    1. Test case: `edit 1 r/befriendee p/91225454 e/zhuoran@example.com`<br>
       Expected: Details of the first befriendee on the befriendee list is edited. Updated details of the edited contact shown in the status message.

    1. Test case: `edit 1 n/Josh`<br>
       Expected: No contact is edited. Error details indicating "Role must be specified..." shown in the status message. Status bar remains the same.

    1. Test case: `edit 0`<br>
       Expected: No contact is edited. Error details indicating "Invalid command format!" shown in the status message. Status bar remains the same.

   1. Other incorrect edit command to try: `edit x r/volunteer n/James` (where x is larger than the volunteer list size)<br>
      Expected: No contact is edited. Error details indicating "The person index provided is invalid" shown in the status message. Status bar remains the same.


### Deleting a person
1. Deleting a person while all befriendees and volunteers are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in both the befriendees and volunteers list. The index indicated exists, the contact at that index not paired with anyone and does not have any logs.

   1. Test case: `delete 3 r/volunteer`<br>
      Expected: Third contact is deleted from the volunteer list. Details of the deleted contact shown in the status message.

   1. Test case: `delete 1`<br>
      Expected: No person is deleted. Error details indicating "Role must be specified..." shown in the status message. Status bar remains the same.

   1. Test case: `delete 0 r/volunteer`<br>
      Expected: No person is deleted. Error details indicating "Index is not a non-zero" shown in the status message. Status bar remains the same.

2. Deleting a person where the contact is paired

   1. Prerequisites: List all persons using the `list` command. Multiple persons in both the befriendees and volunteers list. The befriendee at index 1 of the befriendee list is paired with the volunteer at index 1 of the volunteer list.

   1. Test case: `delete 1 r/volunteer`<br>
      Expected: No person is deleted. Error details indicating "Unable to delete contact: Contact is paired..." shown in the status message. Status bar remains the same.

3. Deleting a person where the contact has a log

   1. Prerequisites: List all persons using the `list` command. Multiple persons in both the befriendees and volunteers list. The befriendee at index 2 of the befriendee list exists, is not paired but has a log in the log list.

   1. Test case: `delete 2 r/befriendee`<br>
      Expected: No person is deleted. Error details indicating "Unable to delete contact: Contact has a log in Elder Scrolls..." shown in the status message. Status bar remains the same.


### Pairing two persons
1. Pairing two persons while all befriendees and volunteers are being shown

    1. Prerequisites: List all persons using the `list` command. Multiple persons in both the befriendees and volunteers list. The contacts at the indicated indices exists and are not paired.

    1. Test case: `pair 1 1`<br>
       Expected: The befriendee at index 1 of the befriendee list and the volunteer at index 1 of the volunteer list are paired. Details of the two persons shown in the status message.

    1. Test case: `pair`<br>
       Expected: No person is paired. Error details indicating "Invalid command format!" shown in the status message. Status bar remains the same.

   1. Other incorrect pair command to try: `pair 1 x` (where x is larger than the volunteer list size)<br>
      Expected: No person is paired. Error details indicating "The person index provided is invalid" shown in the status message. Status bar remains the same.

2. Pairing two persons where one or both of the persons are already paired

    1. Prerequisites: List all persons using the `list` command. Multiple persons in both the befriendees and volunteers list. The contacts at the indicated indices exists and one or both are already paired.

    1. Test case: `pair 1 2`<br>
       Expected: No person is paired. Error details indicating "One or both of the persons are already paired..." shown in the status message. Status bar remains the same.


### Unpairing two persons
1. Unpairing two persons while all befriendees and volunteers are being shown

    1. Prerequisites: List all persons using the `list` command. Multiple persons in both the befriendees and volunteers list. The contacts at the indicated indices exists and are paired with each other.

    1. Test case: `unpair 1 1`<br>
       Expected: The befriendee at index 1 of the befriendee list and the volunteer at index 1 of the volunteer list are unpaired. Details of the two persons shown in the status message.

    1. Test case: `unpair 3`<br>
       Expected: No person is unpaired. Error details indicating "Invalid command format!" shown in the status message. Status bar remains the same.

    1. Other incorrect unpair command to try: `unpair 1 x` (where x is larger than the volunteer list size)<br>
       Expected: No person is unpaired. Error details indicating "The person index provided is invalid" shown in the status message. Status bar remains the same.

2. Unpairing two persons where the persons are not paired with each other

    1. Prerequisites: List all persons using the `list` command. Multiple persons in both the befriendees and volunteers list. The contacts at the indicated indices exists and are not paired with each other.

    1. Test case: `unpair 2 3`<br>
       Expected: No person is unpaired. Error details indicating "The two persons are not paired..." shown in the status message. Status bar remains the same.


### Adding a log
1. Adding a log while all befriendees and volunteers are being shown

    1. Prerequisites: List all persons using the `list` command. Multiple persons in both the befriendees and volunteers list. The first index refers to the befriendee and the second index refers to the volunteer.

    1. Test case: `logadd 1 1 t/Icebreaker session s/2022-03-05 d/2 r/got to know more about befriendee`<br>
       Expected: A log is added for the paired befriendee at index 1 of the befriendee list and the volunteer at index 1 of the volunteer list.

    1. Test case: `logadd 1 1 t/Icebreaker session d/2 `<br>
       Expected: No log is added. Error details indicating "Invalid command format!" shown in the status message. Status bar remains the same.

    1. Other incorrect logadd command to try: `logadd 1 x t/Icebreaker session s/2022-03-05 d/2 r/got to know more about befriendee` (where x is larger than the volunteer list size)<br>
       Expected: No log is added. Error details indicating "The person index provided is invalid." shown in the status message. Status bar remains the same.


### Editing a log
1. Editing a log while all logs are being shown

    1. Prerequisites: List all logs using the `list` command. Multiple logs in logs list.

    1. Test case: `logedit 1 t/Cinema visit`<br>
       Expected: The log at index 1 of the log list is edited. Updated details of the edited log shown in the status message.

    1. Test case: `logedit 1`<br>
       Expected: No log is edited. Error details indicating "At least one field to edit must be provided." shown in the status message. Status bar remains the same.

    1. Other incorrect logedit command to try: `logedit x t/Cinema visit` (where x is larger than the log list size)<br>
       Expected: No log is edited. Error details indicating "The log index provided is invalid." shown in the status message. Status bar remains the same.

### Deleting a log
1. Deleting a log while all logs are being shown

    1. Prerequisites: List all logs using the `list` command. Multiple logs in logs list.

    1. Test case: `logdelete 3`<br>
       Expected: The log at index 3 of the log list is deleted. Details of the deleted log shown in the status message.

    1. Test case: `logdelete`<br>
       Expected: No log is deleted. Error details indicating "Invalid command format!" shown in the status message. Status bar remains the same.

    1. Other incorrect logdelete command to try: `logdelete x` (where x is larger than the log list size)<br>
       Expected: No log is deleted. Error details indicating "Unable to delete log: The log index provided is invalid." shown in the status message. Status bar remains the same.


### Finding Logs associated with a person
1. Prerequisites: Starting with sample data. Refer to the previous test case to load sample data. Use `list` to reset the view before each testcase.
    1. Test case: `findlog`
      Expected: No change in list. Error displayed show an invalid command format, where at least one of the optional parameters must be entered.
   2. Test case: `findlog 1 r/volunteer`
      Expected: Shows the list of logs associated with the volunteer at the index 1.
   3. Test case: `findlog 3 r/befriendee`
      Expected: Shows the list of logs associated with the befriendee at the index 1.

### Undo previously executed commands
1. Undo a command that mutates the data in Elder Scrolls (`add`, `edit`, `delete`, `pair`, `unpair`, `logadd`, `logedit`, `logdelete`, `clear`)
   1. **Prerequisites:** Starting with sample data. Refer to the previous test case to load sample data. Use `list` to reset the view before each testcase.
   2. **Test case:** `delete 3 r/volunteer` followed by `undo` <br>
      **Expected:** After the first command, the third contact in the volunteer list is deleted. <br>
      Subsequently after the second command is executed, the deletion of the third contact in the volunteer list is undone.
      The status bar displays that the previous operation has been undone. 
   3. **Test case:** `edit 1 r/volunteer n/alex` followed by `undo` <br>
      Expected: After the first command is executed, the first person in the volunteer list has their name edited to "alex", the logs previously associated with "Alex Yeoh" now display "alex" as the volunteer associated with them. <br>
      Subsequently, after the second command is executed, the edit made on the name of the first volunteer in the volunteer list is undone, and their name is back to being "Alex Yeoh".
      The status bar displays that the previous operation has been undone.
      <br><br>
   
2. Executing an undo command when the application is newly launched
   1. **Prerequisites:** Exit the application and launch it again, making sure to not execute any commands before the testcase.
   2. **Test case:** `undo` <br>
      **Expected:** No operation is undone. Error details indicating "No previous operation to be undone" shown in the status message. Status bar remains the same.
      <br><br>
   
3. Executing an undo command after an operation that does not modify data in Elder scrolls is executed
   1. **Prerequisites:** Exit the application and launch it again, making sure to not execute any commands before the testcase. Start with sample data. Refer to the previous test cases on how to load sample data.
   2. **Test case:** `list` followed by `undo` <br>
      **Expected:** All persons should be listed after the first command, with a success message displayed in the status message. <br>
      Subsequently, after executing the second command, no operation is undone, the user's view of the volunteer, befriendee and log lists remains the same. 
      Error details indicating "No previous operation to be undone" shown in the status message. Status bar remains the same. 
   3. **Test case:** `find --paired` followed by `undo` <br>
      **Expected:** After the first command, lists of all paired befriendees and volunteers should be shown. <br>
      Subsequently, after executing the second command, no operation is undone, the user's view of the volunteer, befriendee and log lists remains the same.
      Error details indicating "No previous operation to be undone" shown in the status message. Status bar remains the same.
   4. **Test case:** `findlog 1 r/volunteer` followed by `undo` <br>
      **Expected:** After the first command, the list of logs associated with the volunteer at index 1 should be shown. <br>
      Subsequently, after executing the second command, no operation is undone, the user's view of the volunteer, befriendee and log lists remains the same.
      Error details indicating "No previous operation to be undone" shown in the status message. Status bar remains the same.

### Redo changes made by previous Undo command
1. Redo a command that has been undone
   1. **Prerequisites:** Start with the sample data. Refer to the previous test case to load sample data. Use `list` to reset the view before each testcase.
   2. **Test case:** `delete 3 r/volunteer` followed by `undo` followed by `redo` <br>
      **Expected:** After the first command, the third volunteer in the volunteer list is deleted. After the second command, the deletion of the third volunteer is undone. <br>
      Subsequently, after the `redo` command is executed, the third volunteer in the volunteer is once again deleted, reverting the effects of the undo command that was executed.
      The status bar displays that the previous undo operation has been reversed.
   3. **Test case:** `edit 1 r/volunteer n/alex` followed by `undo` followed by `redo` <br>
      **Expected:** After the first command, the first volunteer in the volunteer list will have their name edited to "alex" from "Alex Yeoh", after the second command, the edit made on the first volunteer's name will be undone, and their name will be reverted back to "Alex Yeoh". <br>
      Subsequently, after the `redo` command is executed, the first volunteer in the volunteer list will once again have their name edited to "alex", reverting the effects of the undo command that was executed.
      The status bar displays that the previous undo operation has been reversed.
      <br><br>

2. Executing a redo command when the application is newly launched
   1. **Prerequisites:** Exit the application and launch it again, making sure to not execute any commands before the testcase.
   2. **Test case:** `redo` <br>
      **Expected:** No undo operation is reversed. Error details indicating "No previous undo operation to be reversed" shown in the status message. Status bar remains the same.
      <br><br>

3. Executing a redo command after executing an undo command followed by a command mutating the data in Elder Scrolls.
   1. **Prerequisites:** Start with the sample data. Refer to the previous test cases to load sample data. Execute `delete 3 r/volunteer` followed by `undo`
      as done in the test case for the `undo` command
   2. **Test case:** `edit 1 r/volunteer n/alex` followed by `redo` <br>
      **Expected:** After the first command, the first volunteer in the volunteer list will have their name edited to "alex" from "Alex Yeoh". <br>
      Subsequently, when the `redo` command is executed, the `undo` operation of the `delete 3 r/volunteer` command will not be reversed. 
      Error details indicating "No previous undo operation to be reversed" shown in the status message. Status bar remains the same.


### Clearing Elder Scrolls data
1. Clearing all data
   1. **Prerequisites:** At least one volunteer or one befriendee is present in the lists.
   1. **Test case:** `clear`
      **Expected:** The lists of befriendees, volunteers and logs are cleared. The lists are empty.

### Exiting Elder Scrolls
1. Exiting the application
   1. **Test case:** `exit`
      **Expected:** The application window closes.

### Opening the Help Window
1. Accessing the help window
   1. **Test case:** `help`
      **Expected:** The help window opens.


## **Appendix C: Effort**

### Difficulty Estimation

Overall, the team believes that the features we have chosen to undertake are fairly complex, as they involve fundamental changes to the application state, as is the case with `undo` and `redo`. Additionally, the addition of `log` features and separation of person types meant that the existing relationships between entities inherited from AB3 had to be restructured significantly.

The team also made significant changes to the UI, in an attempt to modernise the look and feel of the application. With meticulous reference to modern design systems, the team made significant changes to the styling of the application. These changes were challenging as they required a good understanding of JavaFX and the ability to work within the confines of the existing codebase.

### Challenges Faced

1. **Global ID vs Indexed ID**: We initially decided on using a global ID for each person, but later realised upon further exploration of the codebase that refactoring all instances of list indexing would be an outsized task. Thus, the team compromised by using global IDs for identifications, but still using list indices for client-facing commands.
2. **Adding Logs**: The team faced challenges in adding logs to the application, as it required a significant restructuring of the existing codebase. Given that logs were an entirely new set of entities to be stored in the application, where AB3 had only a single entity, we quickly realised that the existing structure of the `model` was not sufficient to handle the new requirements. This led to a significant amount of refactoring and restructuring of the `Model` and `Storage` classes.
3. **Relationships between entities**: Given that the new features included two different classes of `Person` as well as a `Log` class associated with both, the team spent a significant amount of time planning and testing the correctness of the relations between these entities. Through defensive programming and good test coverage, we were able to ensure that all user operations modifying these relationships were correctly reflected in the application state.

### Effort Required

Overall, over the thousands of lines of code changed, the team estimates that a few dozen man-hours were spent on the project. The team met regularly to discuss the project, and each member contributed significantly to the project through an even distribution of work over the main areas: adding commands, restructuring logic and backend, UI. The team also spent a significant amount of time on testing, as the new features added to the application were complex and required a significant amount of additional test code to ensure that they worked as expected.

As a result, we were able to deliver on planned features while increasing our code coverage over the inherited project.

### Achievements

A robust and extensible application was delivered, with a significant amount of new features added to the application. The team was able to deliver on planned features, and the performance of new features, such as `log` operations, were significantly improved over old features of the inherited project.

## **Appendix D: Planned Enhancements**

Our current version of Elder Scrolls enables users to efficiently manage volunteers, befriendees, and their interactions. However, we have identified planned enhancements that will further improve the user experience and functionality of the application. 

* Team size: 5. (Max 10 planned enhancements)

1. **Even more flexible Log Entry Editing**: The current `logedit` command does not allow users to reassign the volunteer or befriendee involved in a log, with the only way being to delete and re-add a new log. This could be inconvenient if someone accidentally inputs the wrong pair of indices resulting in a wrong pair being referenced. Hence, we plan to make `logedit` support reassignment of logs to other, valid pairs. 

2. **Enhanced find feature**: The current `find` command returns valid entries based on full matching of user inputs. We plan to enhance our `find` command with partial name keyword/tag search functionality, enabling users to find contacts based on partial name/tag matches. Improves search efficiency and usability in case a user may not fully remember a contact's name or tag.

3. **Better find command format**: While the current `find` command supports a more concise and naturalised search, users may feel that the parameter format is not standardised across all commands, leading to inconvenience for those with bad memory. We plan to implement and support standardised `find` command parameters (such as `n/NAME_KEYWORD`, `p/PAIR_STATUS` etc.) that would help streamline the formatting across all our commands. This would also naturally include appropriate exception handling for invalid input parameters as well.

4. **Add customizability to constraints of logs added**: Currently, logs added can have dates in the future. While this may be desirable (for e.g. to log a planned visit with todos, and subsequently update remarks after the visit), it may also be undesirable (for e.g. to prevent accidental future date inputs). We plan to add a configuration option to allow users to set constraints on the dates of logs added, such that users can customize the behavior to suit their needs.

5. **Flexible Grouping of Volunteer and Befriendee**: Currently, our system only supports 1-1 pairing of volunteer and befriendee. We recognise that expanding beyond will bring stronger flexibility and generalizability, and hence plan to expand the current 1-to-1 pairing system to allow for more flexible grouping of volunteers and befriendees. Specifically, this would allow multiple volunteers to be paired with a single befriendee, vice versa, or allow a group of volunteers to be paired with a group of befriendees. This would be consistent with logadd, logedit, and logdelete commands.

6. **More Informative Pairing and Unpairing Outputs**: Currently, the `pair` and `unpair` commands do not provide exact and detailed feedback during failure of the operation (e.g. person already paired, or persons are not paired together). While the user can thereafter search to find these details using the `find` command, we plan to enhance these commands to provide stronger feedback, including which person is paired, whom each person is currently paired with, and provide shortcuts for a user to easily unpair that person (e.g. by providing copy-paste formatted commands in the output).

7. **More Customizable UI for output**: Currently, while the entire window is adjustable with each list scaling individually, the output message window is not height adjustable. To improve user experience, we plan to make the output message window height adjustable, allowing users to adjust the height of the output message window to their preference.

8. **More Specific Error Messages showing specific parameter absence/invalidity**: While the current error messages are informative, they do not provide specific details on which parameter is missing or invalid. We plan to enhance the error messages to provide more specific details on which parameter is missing or invalid, allowing users to quickly identify and rectify the issue. (E.g. for `add` command, the error message will specify which parameter is missing or invalid).

9. **Better duplicate person detection**: Currently, our application disallows duplicate people without case sensitivity, meaning 2 contacts with names `John Doe` and `john doe` respectively are considered duplicate people. However, extra white spaces in the name will cause contacts to be considered different people, even if everything else is equal. Thus, we plan to enhance the duplicate detection in our application to warn the user when inputting such near match cases. This will give users the ability to confirm if they intend to add a new contact resembling an existing one, with extra white spaces.





