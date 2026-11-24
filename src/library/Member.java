package library;

import java.time.LocalDate;

/**
 * A registered library member. Members do not keep a list of their loans:
 * the resource knows who has it, which avoids two copies of the same fact
 * getting out of step with each other.
 *
 * @author Zheng Haowen
 * @version 1.0
 * @since 24 Nov 2026
 */
public class Member {

    private final String id;
    private final String name;
    private final String email;
    private final LocalDate joinDate;

    /**
     * Creates a member and validates the details.
     *
     * @param id       identifier in the form M001
     * @param name     full name of the member
     * @param email    contact email address
     * @param joinDate date the member registered
     * @throws IllegalArgumentException if a value is not valid
     */
    public Member(String id, String name, String email, LocalDate joinDate) {
        if (id == null || !id.matches("M\\d{3,}")) {
            throw new IllegalArgumentException("Member ID must be M followed by at least 3 digits, e.g. M001.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Member name cannot be empty.");
        }
        if (name.contains("|")) {
            throw new IllegalArgumentException("Member name cannot contain the '|' character.");
        }
        if (email == null || !email.contains("@") || !email.contains(".") || email.contains("|")) {
            throw new IllegalArgumentException("Email address does not look valid.");
        }
        if (joinDate == null) {
            throw new IllegalArgumentException("Join date is required.");
        }
        if (joinDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Join date cannot be in the future.");
        }
        this.id = id;
        this.name = name;
        this.email = email;
        this.joinDate = joinDate;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    /**
     * Returns one readable line about the member for the console output.
     *
     * @return the member details
     */
    public String describe() {
        return id + " - " + name + " (" + email + "), joined " + joinDate;
    }

    /**
     * Builds the line written to the save file.
     *
     * @return the member record as text
     */
    public String toDataLine() {
        return "MEMBER|" + id + "|" + name + "|" + email + "|" + joinDate;
    }
}
