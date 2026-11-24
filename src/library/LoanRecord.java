package library;

import java.time.LocalDate;
import java.util.Locale;

/**
 * One entry in the borrowing history.
 *
 * <p>A record is created when an item is borrowed and completed when it comes
 * back, which is where the return date and the fine are filled in. The history
 * is kept separately from {@link Resource} because a resource goes back on the
 * shelf and forgets its loan, while the library still has to remember who had
 * it and what was charged.</p>
 *
 * @author Zheng Haowen
 * @version 1.1
 * @since 24 Nov 2026
 */
public class LoanRecord {

    private final String resourceId;
    private final String memberId;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;

    /** Null while the item is still out. */
    private LocalDate returnDate;
    private double fine;

    /**
     * Starts a new loan record.
     *
     * @param resourceId id of the borrowed resource
     * @param memberId   id of the member
     * @param borrowDate date of the loan
     * @param dueDate    date the item should come back
     * @throws IllegalArgumentException if an id or date is not valid
     */
    public LoanRecord(String resourceId, String memberId, LocalDate borrowDate, LocalDate dueDate) {
        if (resourceId == null || !resourceId.matches("R\\d{3,}")) {
            throw new IllegalArgumentException("Loan record needs a resource ID such as R001.");
        }
        if (memberId == null || !memberId.matches("M\\d{3,}")) {
            throw new IllegalArgumentException("Loan record needs a member ID such as M001.");
        }
        if (borrowDate == null || dueDate == null) {
            throw new IllegalArgumentException("A loan record needs a borrow date and a due date.");
        }
        if (dueDate.isBefore(borrowDate)) {
            throw new IllegalArgumentException("Due date cannot be before the borrow date.");
        }
        this.resourceId = resourceId;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }

    /**
     * Reports whether the item is still out.
     *
     * @return true when the record has no return date yet
     */
    public boolean isOpen() {
        return returnDate == null;
    }

    /**
     * Closes the record with the date it came back and the fine charged.
     *
     * @param returnDate the date of the return
     * @param fine       the fine for this return, zero when it was on time
     * @throws IllegalArgumentException if the date is missing or the fine is negative
     * @throws IllegalStateException    if the record has already been closed
     */
    void complete(LocalDate returnDate, double fine) {
        if (returnDate == null) {
            throw new IllegalArgumentException("A return date is needed to close a loan.");
        }
        if (fine < 0) {
            throw new IllegalArgumentException("A fine cannot be negative.");
        }
        if (!isOpen()) {
            throw new IllegalStateException("This loan has already been closed.");
        }
        this.returnDate = returnDate;
        this.fine = fine;
    }

    /**
     * Restores a closed loan when the save file is read at start up.
     *
     * @param returnDate the saved return date
     * @param fine       the saved fine
     */
    void restoreReturnState(LocalDate returnDate, double fine) {
        this.returnDate = returnDate;
        this.fine = fine;
    }

    /**
     * Builds the line written to the save file.
     *
     * @return the record as text
     */
    public String toDataLine() {
        return "LOAN|" + resourceId + "|" + memberId + "|" + borrowDate + "|" + dueDate + "|"
                + (returnDate == null ? "-" : returnDate) + "|"
                + String.format(Locale.ROOT, "%.2f", fine);
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getMemberId() {
        return memberId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public double getFine() {
        return fine;
    }
}
