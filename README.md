# PSA2-COM102-Zheng-Haowen

COM102 Object Oriented Programming — Practical Skills Assessment 2.
Scenario B: Library Management System.

A console application in Java for a small library. It manages books, magazines,
DVDs and e-books, registers members, lends and returns items, and stores every
record in a text file between runs.

Author: Haowen Zheng · Student ID: STUDENT_ID · Ulster University, 2026/27.

## Features

- Add and remove resources of four types: Book, Magazine, DVD and EBook.
- Register members and list them.
- Search by title (case-insensitive), by type, or show only available items.
- Borrow and return items, with a due date on every loan.
- Fines per type: book 0.25 GBP/day, magazine 0.10, DVD 1.00, e-book none.
- A member may hold at most three items at a time.
- Validation with readable error messages: wrong IDs, duplicate IDs, bad years,
  borrowing an item that is already out, removing an item that is on loan.
- Save on exit and load on start from `data/library-data.txt`. A damaged line in
  the file is skipped and counted instead of stopping the program.
- Twelve JUnit 5 tests covering normal, boundary and error cases.

## Running the program

Open the folder in IntelliJ IDEA (JDK 21) and run `library.Main`.

From a terminal:

```bash
javac -d out src/library/*.java
java -cp out library.Main
```

The program reads and writes `data/library-data.txt` relative to the folder it
is started from, so run it from the project root.

## Running the tests

```bash
javac -d out src/library/*.java
javac -cp "lib/junit-platform-console-standalone-1.11.3.jar:out" -d out-test test/library/*.java
java -jar lib/junit-platform-console-standalone-1.11.3.jar execute \
  -cp "out:out-test" --select-class library.LibraryTest --details=tree
```

On Windows use `;` instead of `:` in the classpath. In IntelliJ, right-click
`LibraryTest` and choose Run.

## Save file format

One record per line, fields separated by `|`.

```
MEMBER|M001|Alice Chen|alice.chen@example.com|2026-09-14
RESOURCE|BOOK|R001|Clean Code|2008|-|-|Robert C. Martin|Programming
RESOURCE|DVD|R004|Inception|2010|M002|2026-11-21|Christopher Nolan|148
```

`-` means the item is available. Members are written first, then resources,
because a resource record stores only the borrower's ID.

## Project layout

```
src/library        application classes
test/library       JUnit 5 tests
lib                JUnit 5 console runner
data               sample data and the save file
docs               design, testing and source PDFs, UML, screenshots, vodcast script
```

## Documents

- `docs/COM102_Design_HaowenZheng.pdf` — design and development document (547 words).
- `docs/COM102_Testing_HaowenZheng.pdf` — testing and implementation document.
- `docs/source-pdfs/` — PDF copies of every Java source file.
- `docs/uml-class-diagram.svg` — class diagram used in the design document.
- `docs/Vodcast_Script_HaowenZheng.pdf` — script for the five minute walkthrough.

## Known limitations

- No loan history and no record of fines paid; the fine is printed and discarded.
- No reservations for items that are already on loan.
- Saves overwrite the data file directly, without a temporary file and rename.
- One record is one copy; two copies of a book need two IDs.
- Members cannot be removed, and dates come from the system clock, so a loan
  cannot be backdated from the menu.
