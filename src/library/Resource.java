package library;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Holds the details that every library item has in common and implements the
 * parts of {@link Borrowable} that do not change between resource types.
 *
 * <p>A resource is either available or on loan to one member. The loan state is
 * private and can only be changed through {@link #borrow(Member, LocalDate)}
 * and {@link #returnItem(LocalDate)}, so the rest of the program cannot put the
 * object into a state that does not make sense.</p>
 *
 * <p>Subclasses supply the loan period, the daily fine rate, the type name and
 * the extra fields that belong to them. The fine calculation itself is written
 * once here, and {@link EBook} overrides it because e-books are never charged.</p>
 *
 * @author Zheng Haowen
 * @version 1.0
 * @since 24 Nov 2026
 */
public abstract class Resource implements Borrowable {

    /** Highest year accepted, so a book cannot be dated in the far future. */
    private static final int OLDEST_YEAR = 1450;

    private final String id;
    private final String title;
    private final int year;

    /** Null while the item is available, otherwise the member holding it. */
    private Member borrowedBy;
    private LocalDate dueDate;

    /**
     * Creates a resource and checks the values that every type shares.
     *
     * @param id    identifier in the form R001
     * @param title title shown to the user
     * @param year  year of publication or release
     * @throws IllegalArgumentException if an id, title or year is not valid
     */
    protected Resource(String id, String title, int year) {
        if (id == null || !id.matches("R\\d{3,}")) {
            throw new IllegalArgumentException("Resource ID must be R followed by at least 3 digits, e.g. R001.");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }
        // The save file uses '|' as a separator, so it cannot appear in a value.
        if (title.contains("|")) {
            throw new IllegalArgumentException("Title cannot contain the '|' character.");
        }
        if (year < OLDEST_YEAR || year > LocalDate.now().getYear() + 1) {
            throw new IllegalArgumentException(
                    "Year must be between " + OLDEST_YEAR + " and " + (LocalDate.now().getYear() + 1) + ".");
        }
        this.id = id;
        this.title = title;
        this.year = year;
    }

    /**
     * Checks a text field used by a subclass and returns it unchanged.
     *
     * @param value the value to check
     * @param field the field name used in the error message
     * @return the same value
     * @throws IllegalArgumentException if the value is empty or contains '|'
     */
    protected static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " cannot be empty.");
        }
        if (value.contains("|")) {
            throw new IllegalArgumentException(field + " cannot contain the '|' character.");
        }
        return value;
    }

    /**
     * Returns the type name used in the menu and in the save file.
     *
     * @return BOOK, MAGAZINE, DVD or EBOOK
     */
    public abstract String getType();

    /**
     * Returns the fields that only this type has, ready to be written to the
     * save file. Values are joined with the '|' separator.
     *
     * @return the type specific fields as text
     */
    public abstract String toDataFields();

    /**
     * Returns a short description of the type specific details.
     *
     * @return details such as "author: ..., genre: ..."
     */
    public abstract String getDetails();

    /**
     * Returns the daily fine charged by this type of resource.
     *
     * @return the fine per late day in pounds
     */
    public abstract double getDailyFine();

    /**
     * {@inheritDoc}
     *
     * <p>The default rule is days late multiplied by the daily rate. The
     * amount is rounded to two decimal places because it is money.</p>
     */
    @Override
    public double calculateFine(int daysLate) {
        if (daysLate <= 0) {
            return 0.0;
        }
        double fine = daysLate * getDailyFine();
        return Math.round(fine * 100.0) / 100.0;
    }

    @Override
    public boolean isAvailable() {
        return borrowedBy == null;
    }

    @Override
    public void borrow(Member member, LocalDate borrowDate) {
        if (member == null) {
            throw new IllegalArgumentException("A member is needed to borrow an item.");
        }
        if (borrowDate == null) {
            throw new IllegalArgumentException("A borrow date is needed.");
        }
        if (!isAvailable()) {
            throw new IllegalStateException(id + " is already on loan to " + borrowedBy.getName() + ".");
        }
        this.borrowedBy = member;
        this.dueDate = borrowDate.plusDays(getLoanPeriodDays());
    }

    @Override
    public double returnItem(LocalDate returnDate) {
        if (returnDate == null) {
            throw new IllegalArgumentException("A return date is needed.");
        }
        if (isAvailable()) {
            throw new IllegalStateException(id + " is not on loan, so it cannot be returned.");
        }
        long daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
        this.borrowedBy = null;
        this.dueDate = null;
        return calculateFine((int) daysLate);
    }

    /**
     * Restores a loan when the save file is read at start up. This skips the
     * availability check on purpose, because the data already describes a loan.
     *
     * @param member  member who has the item, or null for an available item
     * @param dueDate due date of the loan, or null for an available item
     */
    void restoreLoanState(Member member, LocalDate dueDate) {
        this.borrowedBy = member;
        this.dueDate = dueDate;
    }

    /**
     * Builds the single line that represents this resource in the save file.
     *
     * @return the record as text
     */
    public String toDataLine() {
        String borrowerId = borrowedBy == null ? "-" : borrowedBy.getId();
        String due = dueDate == null ? "-" : dueDate.toString();
        return getType() + "|" + id + "|" + title + "|" + year + "|" + borrowerId + "|" + due + "|" + toDataFields();
    }

    /**
     * Reports whether the item is late for the given date.
     *
     * @param today the date to compare against
     * @return true when the item is on loan and the due date has passed
     */
    public boolean isOverdue(LocalDate today) {
        return !isAvailable() && dueDate.isBefore(today);
    }

    /**
     * Returns one readable line about this resource for the console output.
     *
     * @return the description shown in listings and search results
     */
    public String describe() {
        String status;
        if (isAvailable()) {
            status = "available";
        } else {
            status = "on loan to " + borrowedBy.getName() + " (due " + dueDate + ")";
        }
        return getType() + " " + id + " - " + title + " (" + year + ") " + getDetails() + " [" + status + "]";
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public Member getBorrowedBy() {
        return borrowedBy;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }
}
