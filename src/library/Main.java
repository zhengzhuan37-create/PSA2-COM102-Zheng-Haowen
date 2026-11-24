package library;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Console front end for the library. It shows the menu, reads what the user
 * types and calls the matching method on {@link Library}.
 *
 * <p>The program loads the saved records when it starts and writes them again
 * when the user picks option 0, so the next run begins with the same data.</p>
 *
 * @author Zheng Haowen
 * @version 1.0
 * @since 24 Nov 2026
 */
public class Main {

    /** Records are stored in this file, relative to the project folder. */
    private static final Path DATA_FILE = Path.of("data", "library-data.txt");

    private final Library library = new Library();
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Starts the application.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        new Main().run();
    }

    /**
     * Loads the saved data and runs the menu until the user chooses to exit.
     */
    private void run() {
        loadData();
        boolean running = true;
        while (running) {
            printMenu();
            int option = readInt("Please select an option: ");
            try {
                switch (option) {
                    case 1 -> addResource();
                    case 2 -> removeResource();
                    case 3 -> registerMember();
                    case 4 -> searchResources();
                    case 5 -> borrowResource();
                    case 6 -> returnResource();
                    case 7 -> showResources();
                    case 8 -> showMembers();
                    case 9 -> showLoanHistory();
                    case 10 -> saveData();
                    case 0 -> {
                        saveData();
                        running = false;
                    }
                    default -> System.out.println("Please choose a number from the menu.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Goodbye.");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("====== Library Management System ======");
        System.out.println("1. Add resource");
        System.out.println("2. Remove resource");
        System.out.println("3. Register member");
        System.out.println("4. Search resources");
        System.out.println("5. Borrow resource");
        System.out.println("6. Return resource");
        System.out.println("7. Show all resources");
        System.out.println("8. Show all members");
        System.out.println("9. Show loan history");
        System.out.println("10. Save data");
        System.out.println("0. Save and exit");
    }

    private void addResource() {
        System.out.println("1. Book");
        System.out.println("2. Magazine");
        System.out.println("3. DVD");
        System.out.println("4. E-book");
        int type = readInt("Choose a type: ");
        switch (type) {
            case 1 -> addBook();
            case 2 -> addMagazine();
            case 3 -> addDvd();
            case 4 -> addEbook();
            default -> throw new IllegalArgumentException("Unknown resource type.");
        }
    }

    private void addBook() {
        String id = readLine("Resource ID (e.g. R006): ");
        String title = readLine("Title: ");
        int year = readInt("Year: ");
        String author = readLine("Author: ");
        String genre = readLine("Genre: ");
        Book book = new Book(id, title, year, author, genre);
        library.addResource(book);
        System.out.println("Added: " + book.describe());
    }

    private void addMagazine() {
        String id = readLine("Resource ID (e.g. R007): ");
        String title = readLine("Title: ");
        int year = readInt("Year: ");
        int issue = readInt("Issue number: ");
        String category = readLine("Category: ");
        Magazine magazine = new Magazine(id, title, year, issue, category);
        library.addResource(magazine);
        System.out.println("Added: " + magazine.describe());
    }

    private void addDvd() {
        String id = readLine("Resource ID (e.g. R008): ");
        String title = readLine("Title: ");
        int year = readInt("Year: ");
        String director = readLine("Director: ");
        int duration = readInt("Duration in minutes: ");
        DVD dvd = new DVD(id, title, year, director, duration);
        library.addResource(dvd);
        System.out.println("Added: " + dvd.describe());
    }

    private void addEbook() {
        String id = readLine("Resource ID (e.g. R009): ");
        String title = readLine("Title: ");
        int year = readInt("Year: ");
        double size = readDouble("File size in MB: ");
        String format = readLine("Format (PDF or EPUB): ");
        EBook ebook = new EBook(id, title, year, size, format);
        library.addResource(ebook);
        System.out.println("Added: " + ebook.describe());
    }

    private void removeResource() {
        String id = readLine("Resource ID to remove: ");
        library.removeResource(id);
        System.out.println("Removed resource " + id + ".");
    }

    private void registerMember() {
        String id = readLine("Member ID (e.g. M004): ");
        String name = readLine("Full name: ");
        String email = readLine("Email: ");
        LocalDate joinDate = readDate("Join date (yyyy-mm-dd, blank for today): ");
        Member member = new Member(id, name, email, joinDate);
        library.registerMember(member);
        System.out.println("Registered: " + member.describe());
    }

    private void searchResources() {
        System.out.println("1. Search by title");
        System.out.println("2. Search by type");
        System.out.println("3. Show available resources");
        int choice = readInt("Choose a search: ");
        List<Resource> results;
        switch (choice) {
            case 1 -> results = library.searchByTitle(readLine("Title contains: "));
            case 2 -> results = library.searchByType(readLine("Type (BOOK, MAGAZINE, DVD or EBOOK): "));
            case 3 -> results = library.availableResources();
            default -> throw new IllegalArgumentException("Unknown search option.");
        }
        if (results.isEmpty()) {
            System.out.println("No matching resources found.");
            return;
        }
        System.out.println(results.size() + " result(s):");
        for (Resource resource : results) {
            System.out.println("  " + resource.describe());
        }
    }

    private void borrowResource() {
        String resourceId = readLine("Resource ID: ");
        String memberId = readLine("Member ID: ");
        library.borrowResource(resourceId, memberId, LocalDate.now());
        Resource resource = library.findResourceById(resourceId);
        System.out.println("Borrowed " + resource.getTitle() + " to " + resource.getBorrowedBy().getName()
                + ". Due back on " + resource.getDueDate() + ".");
    }

    private void returnResource() {
        String resourceId = readLine("Resource ID: ");
        double fine = library.returnResource(resourceId, LocalDate.now());
        if (fine > 0) {
            System.out.println("Returned " + resourceId + ". This item was late, fine due: "
                    + String.format("%.2f", fine) + " GBP.");
        } else {
            System.out.println("Returned " + resourceId + ". No fine is due.");
        }
    }

    private void showResources() {
        if (library.getResources().isEmpty()) {
            System.out.println("The library has no resources yet.");
            return;
        }
        for (Resource resource : library.getResources()) {
            System.out.println(resource.describe());
        }
    }

    private void showMembers() {
        if (library.getMembers().isEmpty()) {
            System.out.println("No members are registered yet.");
            return;
        }
        for (Member member : library.getMembers()) {
            System.out.println(member.describe());
        }
    }

    private void showLoanHistory() {
        System.out.println("1. All loans");
        System.out.println("2. Loans for one member");
        System.out.println("3. Items still on loan");
        int choice = readInt("Choose a view: ");
        List<LoanRecord> records;
        switch (choice) {
            case 1 -> records = library.getLoans();
            case 2 -> records = library.getLoansForMember(readLine("Member ID: "));
            case 3 -> records = library.getOpenLoans();
            default -> throw new IllegalArgumentException("Unknown history option.");
        }
        if (records.isEmpty()) {
            System.out.println("No loan records found.");
            return;
        }
        System.out.println(records.size() + " record(s):");
        for (LoanRecord loan : records) {
            System.out.println("  " + describeLoan(loan));
        }
    }

    /**
     * Builds the two lines shown for one loan record. The member and resource
     * ids are turned into names when the records still exist, but the history
     * is also readable after an item has been removed.
     *
     * @param loan the record to describe
     * @return the text for the console
     */
    private String describeLoan(LoanRecord loan) {
        Resource resource = library.findResourceById(loan.getResourceId());
        String title = resource == null ? "removed item" : resource.getTitle();
        Member member = library.findMemberById(loan.getMemberId());
        String memberText = member == null ? loan.getMemberId()
                : loan.getMemberId() + " " + member.getName();
        String status;
        if (loan.isOpen()) {
            status = "still on loan";
        } else {
            String fineText = loan.getFine() > 0
                    ? String.format("%.2f", loan.getFine()) + " GBP"
                    : "no fine";
            status = "returned " + loan.getReturnDate() + ", " + fineText;
        }
        return loan.getResourceId() + " \"" + title + "\" -> " + memberText
                + "\n    borrowed " + loan.getBorrowDate() + ", due " + loan.getDueDate() + ", " + status;
    }

    private void saveData() {
        try {
            library.saveData(DATA_FILE);
            System.out.println("Saved " + library.getResources().size() + " resources and "
                    + library.getMembers().size() + " members to " + DATA_FILE + ".");
        } catch (IOException e) {
            System.out.println("Could not save the data file: " + e.getMessage());
        }
    }

    private void loadData() {
        if (!Files.exists(DATA_FILE)) {
            System.out.println("No saved data found at " + DATA_FILE + ", starting with an empty library.");
            return;
        }
        try {
            int skipped = library.loadData(DATA_FILE);
            System.out.println("Loaded " + library.getResources().size() + " resources and "
                    + library.getMembers().size() + " members from " + DATA_FILE + ".");
            if (skipped > 0) {
                System.out.println("Warning: " + skipped + " line(s) in the data file could not be read and were skipped.");
            }
        } catch (IOException e) {
            System.out.println("Could not read the data file: " + e.getMessage());
        }
    }

    /**
     * Reads a whole number, asking again if the input is not valid.
     *
     * @param prompt the text shown before the input
     * @return the number entered
     */
    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = nextLine();
            try {
                return Integer.parseInt(text.trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    /**
     * Reads a decimal number, asking again if the input is not valid.
     *
     * @param prompt the text shown before the input
     * @return the number entered
     */
    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = nextLine();
            try {
                return Double.parseDouble(text.trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number, for example 2.5.");
            }
        }
    }

    /**
     * Reads a date, or uses today's date when the line is left empty.
     *
     * @param prompt the text shown before the input
     * @return the date entered
     */
    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = nextLine().trim();
            if (text.isEmpty()) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(text);
            } catch (DateTimeParseException e) {
                System.out.println("Please use the format yyyy-mm-dd, for example 2026-11-24.");
            }
        }
    }

    /**
     * Reads a line that cannot be empty.
     *
     * @param prompt the text shown before the input
     * @return the text entered
     */
    private String readLine(String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = nextLine().trim();
            if (!text.isEmpty()) {
                return text;
            }
            System.out.println("This value cannot be empty.");
        }
    }

    /**
     * Reads one line from the keyboard. If the input has ended (for example
     * when a file is piped in), the data is saved and the program stops
     * instead of looping forever.
     *
     * @return the line that was read
     */
    private String nextLine() {
        if (!scanner.hasNextLine()) {
            System.out.println();
            saveData();
            System.out.println("Input closed, goodbye.");
            System.exit(0);
        }
        return scanner.nextLine();
    }
}
