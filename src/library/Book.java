package library;

/**
 * A printed book. Books have the longest loan period in the library and a
 * fairly low daily fine.
 *
 * @author Zheng Haowen
 * @version 1.0
 * @since 24 Nov 2026
 */
public class Book extends Resource {

    /** Two weeks is the normal loan length for a book. */
    private static final int LOAN_DAYS = 14;
    private static final double DAILY_FINE = 0.25;

    private final String author;
    private final String genre;

    /**
     * Creates a book.
     *
     * @param id     identifier such as R001
     * @param title  title of the book
     * @param year   year of publication
     * @param author name of the author
     * @param genre  genre such as Programming or History
     * @throws IllegalArgumentException if a value is not valid
     */
    public Book(String id, String title, int year, String author, String genre) {
        super(id, title, year);
        this.author = requireText(author, "Author");
        this.genre = requireText(genre, "Genre");
    }

    @Override
    public String getType() {
        return "BOOK";
    }

    @Override
    public String getDetails() {
        return "by " + author + ", " + genre;
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
        return author + "|" + genre;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }
}
