---
layout: page
title: Ger Teck's Project Portfolio Page
---

### Project: Elder Scrolls

Elder Scrolls is a Volunteer Management System (VMS) designed to streamline the coordination of volunteers and befriendees, with a particular focus on elderly befriending programs. It is a brownfield project based on AddressBook-Level3, which is a desktop address book application used for teaching Software Engineering principles. The user interacts with it using a CLI, and it has a GUI created with JavaFX. It is written in Java. After our development, our project now has about 23 kLoC.

Given below are my contributions to the project.

* **New Features**:
  * _Added support for adding befriendee & volunteer persons._
      * What it does: This feature enables users to add both befriendee and volunteer persons to the system.
      * Justification: By allowing the addition of both types of persons, the system becomes more versatile and can accommodate a wider range of users' needs.
      * Highlights: Implementation involved significant design analysis and modification of existing commands. Challenges were encountered during implementation due to the need for changes in existing functionalities.
  * _Added new `logfind` feature to support searching for logs_:
    * What it does: This feature allows users to search for specific logs within the system.
    * Justification: By implementing a search functionality for logs, users can quickly retrieve relevant information, enhancing the usability and efficiency of the system.
    * Highlights: Testing was crucial to ensure accurate and reliable search results.

* **Enhancements to existing features**:
  * Wrote additional tests for existing features to increase coverage [\#40](https://github.com/AY2324S2-CS2103T-T09-3/tp/pull/40), [\#88](https://github.com/AY2324S2-CS2103T-T09-3/tp/pull/88), among others.
  * Refactored Pair and Unpair commands to enforce immutability [\#62](https://github.com/AY2324S2-CS2103T-T09-3/tp/pull/62)
  * Extended functionality to Find Command to support searching in separate role lists, search by tags, search by pair status and search by name [\#89](https://github.com/AY2324S2-CS2103T-T09-3/tp/pull/89), [\#96](https://github.com/AY2324S2-CS2103T-T09-3/tp/pull/96)
  * Updated the GUI color scheme, added relevant SampleData shown upon startup [\#119](https://github.com/AY2324S2-CS2103T-T09-3/tp/pull/119)

* **Documentation**:
  * _User Guide_:
    * Updated main landing page, UG structure and features to reflect new features [\#87](https://github.com/AY2324S2-CS2103T-T09-3/tp/pull/87)
    * Authored User Interface section of the UG [\#119](https://github.com/AY2324S2-CS2103T-T09-3/tp/pull/119)
    * Added documentation for the features `logfind` and `find`
    * Added FAQs section to the UG.
    * Formatted the UG for better readability and navigation when printing to PDF.
    
  * _Developer Guide_:
    * Updated medium priority user stories and relevant use cases. [\#24](https://github.com/AY2324S2-CS2103T-T09-3/tp/pull/24)
    * Updated PUML diagrams to reflect implementation changes [\#53](https://github.com/AY2324S2-CS2103T-T09-3/tp/pull/53)
    * Added implementation details of the `find`, `logfind` features.
    * Added manual testing section for the `list`, `find`, `logfind`, `clear` features.
    * Added planned enhancements for the project.

* **Project management**:
  * Updated release `v1.2` on GitHub with added features and screenshots (Release [\v1.2](https://github.com/AY2324S2-CS2103T-T09-3/tp/releases/tag/v1.2))

* **Community**:
  * PRs reviewed (with non-trivial review comments): ([\#93](https://github.com/AY2324S2-CS2103T-T09-3/tp/pull/93), [\#91](https://github.com/AY2324S2-CS2103T-T09-3/tp/pull/91), among others)
  * Contributed to forum discussions (examples: [1](https://github.com/nus-cs2103-AY2324S2/forum/issues/251))
  * Reported bugs and suggestions for other teams in the class (examples: [1](https://github.com/AY2324S2-CS2103-F15-4/tp/issues/174), [2](https://github.com/AY2324S2-CS2103-F15-4/tp/issues/167), [3](https://github.com/AY2324S2-CS2103-F15-4/tp/issues/172))

  
* **Code contributed**: [RepoSense link](https://nus-cs2103-ay2324s2.github.io/tp-dashboard/?search=gerteck&breakdown=true)

