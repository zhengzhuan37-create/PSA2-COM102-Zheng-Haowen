package library;

/**
 * An electronic book. E-books take no shelf space, so the library allows a
 * normal two week loan, but no fine is charged: when the loan ends the file is
 * simply no longer available to the member.
 *
 * @author Zheng Haowen
 * @version 1.0
 * @since 24 Nov 2026
 */
public class EBook extends Resource {

    private static final int LOAN_DAYS = 14;

    private final double fileSizeMb;
    private final String format;

    /**
     * Creates an e-book.
     *
     * @param id         identifier such as R005
     * @param title      title of the e-book
     * @param year       year of publication
     * @param fileSizeMb size of the file in megabytes
     * @param format     PDF or EPUB
     * @throws IllegalArgumentException if a value is not valid
     */
    public EBook(String id, String title, int year, double fileSizeMb, String format) {
        super(id, title, year);
        if (fileSizeMb <= 0 || fileSizeMb > 10000) {
            throw new IllegalArgumentException("File size must be between 0 and 10000 MB.");
        }
        this.fileSizeMb = fileSizeMb;
        String cleaned = requireText(format, "Format").toUpperCase();
        if (!cleaned.equals("PDF") && !cleaned.equals("EPUB")) {
            throw new IllegalArgumentException("Format must be PDF or EPUB.");
        }
        this.format = cleaned;
    }

    @Override
    public String getType() {
        return "EBOOK";
    }

    @Override
    public String getDetails() {
        return format + " file, " + fileSizeMb + " MB";
    }

    @Override
    public int getLoanPeriodDays() {
        return LOAN_DAYS;
    }

    /**
     * E-books are not charged a fine. The access period ends automatically, so
     * there is nothing for the member to return late.
     *
     * @param daysLate days past the due date, ignored for e-books
     * @return always zero
     */
    @Override
    public double calculateFine(int daysLate) {
        return 0.0;
    }

    @Override
    public double getDailyFine() {
        return 0.0;
    }

    @Override
    public String toDataFields() {
        return fileSizeMb + "|" + format;
    }

    public double getFileSizeMb() {
        return fileSizeMb;
    }

    public String getFormat() {
        return format;
    }
}
