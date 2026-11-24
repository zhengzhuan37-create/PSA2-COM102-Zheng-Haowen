package library;

/**
 * A DVD. These are the shortest loans and the most expensive fines because the
 * library only owns one or two copies of each title.
 *
 * @author Zheng Haowen
 * @version 1.0
 * @since 24 Nov 2026
 */
public class DVD extends Resource {

    private static final int LOAN_DAYS = 3;
    private static final double DAILY_FINE = 1.00;

    private final String director;
    private final int durationMinutes;

    /**
     * Creates a DVD.
     *
     * @param id              identifier such as R004
     * @param title           title of the film
     * @param year            year of release
     * @param director        name of the director
     * @param durationMinutes running time in minutes
     * @throws IllegalArgumentException if a value is not valid
     */
    public DVD(String id, String title, int year, String director, int durationMinutes) {
        super(id, title, year);
        this.director = requireText(director, "Director");
        if (durationMinutes <= 0 || durationMinutes > 600) {
            throw new IllegalArgumentException("Duration must be between 1 and 600 minutes.");
        }
        this.durationMinutes = durationMinutes;
    }

    @Override
    public String getType() {
        return "DVD";
    }

    @Override
    public String getDetails() {
        return "directed by " + director + ", " + durationMinutes + " min";
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
        return director + "|" + durationMinutes;
    }

    public String getDirector() {
        return director;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }
}
