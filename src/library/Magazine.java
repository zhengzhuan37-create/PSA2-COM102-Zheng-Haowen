package library;

/**
 * A magazine issue. Issues are borrowed for one week, which is shorter than a
 * book because they go out of date quickly and other members usually want them.
 *
 * @author Zheng Haowen
 * @version 1.0
 * @since 24 Nov 2026
 */
public class Magazine extends Resource {

    private static final int LOAN_DAYS = 7;
    private static final double DAILY_FINE = 0.10;

    private final int issueNumber;
    private final String category;

    /**
     * Creates a magazine issue.
     *
     * @param id          identifier such as R003
     * @param title       name of the magazine
     * @param year        year of the issue
     * @param issueNumber issue number printed on the cover
     * @param category    subject area, for example Science
     * @throws IllegalArgumentException if a value is not valid
     */
    public Magazine(String id, String title, int year, int issueNumber, String category) {
        super(id, title, year);
        if (issueNumber <= 0) {
            throw new IllegalArgumentException("Issue number must be greater than zero.");
        }
        this.issueNumber = issueNumber;
        this.category = requireText(category, "Category");
    }

    @Override
    public String getType() {
        return "MAGAZINE";
    }

    @Override
    public String getDetails() {
        return "issue " + issueNumber + ", " + category;
    }

    @Override
    public int getLoanPeriodDays() {
        return LOAN_DAYS;
    }

    @Override
    public double getDailyFine() {
        return DAILY_FINE;
    }

    @Override
    public String toDataFields() {
        return issueNumber + "|" + category;
    }

    public int getIssueNumber() {
        return issueNumber;
    }

    public String getCategory() {
        return category;
    }
}
