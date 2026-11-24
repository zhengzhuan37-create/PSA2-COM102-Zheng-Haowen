package library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the library rules: borrowing, fines, search, removal and saving.
 *
 * <p>The date is fixed so the expected due dates and fines do not change from
 * one day to the next.</p>
 *
 * @author Zheng Haowen
 * @version 1.0
 * @since 24 Nov 2026
 */
class LibraryTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 24);

    private Library library;

    @BeforeEach
    void setUp() {
        library = new Library();
        library.registerMember(new Member("M001", "Alice Chen", "alice@example.com", LocalDate.of(2026, 9, 1)));
        library.registerMember(new Member("M002", "Ben Wu", "ben@example.com", LocalDate.of(2026, 9, 10)));
        library.addResource(new Book("R001", "Clean Code", 2008, "Robert C. Martin", "Programming"));
        library.addResource(new DVD("R002", "Inception", 2010, "Christopher Nolan", 148));
        library.addResource(new EBook("R003", "Effective Java", 2018, 3.4, "PDF"));
        library.addResource(new Magazine("R004", "National Geographic", 2026, 12, "Science"));
    }

    @Test
    void borrowingAnItemMakesItUnavailable() {
        library.borrowResource("R001", "M001", TODAY);

        Resource book = library.findResourceById("R001");
        assertFalse(book.isAvailable());
        assertEquals("M001", book.getBorrowedBy().getId());
        assertEquals(TODAY.plusDays(14), book.getDueDate());
    }

    @Test
    void anItemOnLoanCannotBeBorrowedAgain() {
        library.borrowResource("R002", "M001", TODAY);

        assertThrows(IllegalStateException.class, () -> library.borrowResource("R002", "M002", TODAY));
    }

    @Test
    void aMemberCannotHoldMoreThanThreeItems() {
        library.borrowResource("R001", "M001", TODAY);
        library.borrowResource("R002", "M001", TODAY);
        library.borrowResource("R003", "M001", TODAY);

        assertThrows(IllegalStateException.class, () -> library.borrowResource("R004", "M001", TODAY));
        assertEquals(3, library.countActiveLoans(library.findMemberById("M001")));
    }

    // Boundary: due date itself is not late, one day after is.
    @Test
    void noFineOnTheDueDateButAFineTheDayAfter() {
        library.borrowResource("R002", "M001", TODAY);
        double onTime = library.returnResource("R002", TODAY.plusDays(3));
        assertEquals(0.0, onTime, 0.001);

        library.borrowResource("R002", "M001", TODAY);
        double oneDayLate = library.returnResource("R002", TODAY.plusDays(4));
        assertEquals(1.0, oneDayLate, 0.001);
    }

    @Test
    void dvdFineIsChargedForEachLateDay() {
        library.borrowResource("R002", "M001", TODAY);
        double fine = library.returnResource("R002", TODAY.plusDays(7));

        assertEquals(4.0, fine, 0.001);
    }

    @Test
    void ebooksAreNeverChargedAFine() {
        library.borrowResource("R003", "M001", TODAY);
        double fine = library.returnResource("R003", TODAY.plusDays(40));

        assertEquals(0.0, fine, 0.001);
    }

    @Test
    void returningAnItemThatIsNotOnLoanIsRejected() {
        assertThrows(IllegalStateException.class, () -> library.returnResource("R001", TODAY));
    }

    @Test
    void searchByTitleIgnoresCaseAndHandlesNoMatch() {
        List<Resource> matches = library.searchByTitle("clean");

        assertEquals(1, matches.size());
        assertEquals("R001", matches.get(0).getId());
        assertTrue(library.searchByTitle("nothing like this").isEmpty());
    }

    @Test
    void duplicateResourceIdIsRejected() {
        Book copy = new Book("R001", "Clean Code", 2008, "Robert C. Martin", "Programming");

        assertThrows(IllegalArgumentException.class, () -> library.addResource(copy));
    }

    @Test
    void anItemOnLoanCannotBeRemoved() {
        library.borrowResource("R001", "M001", TODAY);

        assertThrows(IllegalStateException.class, () -> library.removeResource("R001"));
        assertThrows(IllegalArgumentException.class, () -> library.removeResource("R999"));
    }

    @Test
    void savedDataIsLoadedBackWithTheLoan(@TempDir Path tempDir) throws IOException {
        library.borrowResource("R001", "M001", TODAY);
        Path file = tempDir.resolve("data.txt");

        library.saveData(file);

        Library loaded = new Library();
        int skipped = loaded.loadData(file);
        assertEquals(0, skipped);
        assertEquals(4, loaded.getResources().size());
        assertEquals(2, loaded.getMembers().size());
        Resource book = loaded.findResourceById("R001");
        assertFalse(book.isAvailable());
        assertEquals("M001", book.getBorrowedBy().getId());
        assertEquals(TODAY.plusDays(14), book.getDueDate());
        assertNull(loaded.findResourceById("R999"));
    }

    @Test
    void aDamagedLineIsSkippedInsteadOfStoppingTheLoad(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("data.txt");
        Files.write(file, List.of(
                "MEMBER|M001|Alice Chen|alice@example.com|not-a-date",
                "MEMBER|M002|Ben Wu|ben@example.com|2026-09-10",
                "RESOURCE|BOOK|R001|Clean Code|2008|-|-|Robert C. Martin|Programming"
        ), StandardCharsets.UTF_8);

        Library loaded = new Library();
        int skipped = loaded.loadData(file);

        assertEquals(1, skipped);
        assertEquals(1, loaded.getResources().size());
        assertEquals(1, loaded.getMembers().size());
    }
}
