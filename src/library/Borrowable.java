package library;

import java.time.LocalDate;

/**
 * Defines what a member of the library can do with a borrowable item.
 *
 * <p>Both the loan period and the way an overdue fine is worked out depend on
 * the kind of resource, so each implementing class has to provide its own
 * rules. The console application only talks to this interface when it borrows
 * or returns an item, which means new resource types can be added without
 * changing the menu code.</p>
 *
 * @author Zheng Haowen
 * @version 1.0
 * @since 24 Nov 2026
 */
public interface Borrowable {

    /**
     * Reports whether the item is currently sitting on the shelf.
     *
     * @return true if the item can be borrowed right now
     */
    boolean isAvailable();

    /**
     * Returns the number of days a member may keep the item.
     *
     * @return the loan period in days
     */
    int getLoanPeriodDays();

    /**
     * Works out the fine for returning an item a number of days after it was due.
     *
     * @param daysLate days past the due date, zero or negative when on time
     * @return the amount to pay in pounds, zero when the item is on time
     */
    double calculateFine(int daysLate);

    /**
     * Gives the item to a member and sets a new due date.
     *
     * @param member     the member taking the item
     * @param borrowDate the date of the loan
     * @throws IllegalStateException    if the item is already on loan
     * @throws IllegalArgumentException if the member or date is missing
     */
    void borrow(Member member, LocalDate borrowDate);

    /**
     * Takes the item back and charges a fine if the return is late.
     *
     * @param returnDate the date the item comes back
     * @return the fine for this return, zero when it was on time
     * @throws IllegalStateException if the item is not on loan
     */
    double returnItem(LocalDate returnDate);
}
