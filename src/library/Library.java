package library;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Keeps the resources and members of the library and provides the operations
 * offered by the console menu.
 *
 * <p>Both collections are {@link ArrayList}s. The library is small (a few dozen
 * items at most) and almost every operation is either a search that has to look
 * at each item or a listing, so a list is a good fit and keeps the order the
 * items were added in, which makes the console output easier to follow.</p>
 *
 * <p>This class is also responsible for reading and writing the text file that
 * stores the records between runs of the program.</p>
 *
 * @author Zheng Haowen
 * @version 1.0
 * @since 24 Nov 2026
 */
public class Library {

    /** A member may hold this many items at the same time. */
    public static final int MAX_ITEMS_PER_MEMBER = 3;

    private final List<Resource> resources = new ArrayList<>();
    private final List<Member> members = new ArrayList<>();

    /**
     * Adds a resource to the library.
     *
     * @param resource the resource to add
     * @throws IllegalArgumentException if the resource is null or its id is already used
     */
    public void addResource(Resource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("No resource was provided.");
        }
        if (findResourceById(resource.getId()) != null) {
            throw new IllegalArgumentException("A resource with ID " + resource.getId() + " already exists.");
        }
        resources.add(resource);
    }

    /**
     * Removes a resource that is not on loan.
     *
     * @param id the id of the resource
     * @throws IllegalArgumentException if no resource has that id
     * @throws IllegalStateException    if the resource is currently on loan
     */
    public void removeResource(String id) {
        Resource resource = requireResource(id);
        if (!resource.isAvailable()) {
            throw new IllegalStateException(
                    resource.getId() + " is on loan and cannot be removed until it is returned.");
        }
        resources.remove(resource);
    }

    /**
     * Registers a new member.
     *
     * @param member the member to register
     * @throws IllegalArgumentException if the member is null or the id is already used
     */
    public void registerMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("No member was provided.");
        }
        if (findMemberById(member.getId()) != null) {
            throw new IllegalArgumentException("A member with ID " + member.getId() + " already exists.");
        }
        members.add(member);
    }

    /**
     * Finds a resource by its id.
     *
     * @param id the id to look for
     * @return the matching resource, or null when there is no match
     */
    public Resource findResourceById(String id) {
        if (id == null) {
            return null;
        }
        for (Resource resource : resources) {
            if (resource.getId().equalsIgnoreCase(id)) {
                return resource;
            }
        }
        return null;
    }

    /**
     * Finds a member by their id.
     *
     * @param id the id to look for
     * @return the matching member, or null when there is no match
     */
    public Member findMemberById(String id) {
        if (id == null) {
            return null;
        }
        for (Member member : members) {
            if (member.getId().equalsIgnoreCase(id)) {
                return member;
            }
        }
        return null;
    }

    /**
     * Lends a resource to a member.
     *
     * @param resourceId id of the resource
     * @param memberId   id of the member
     * @param today      the date of the loan
     * @throws IllegalArgumentException if the resource or member does not exist
     * @throws IllegalStateException    if the resource is on loan or the member already has the maximum
     */
    public void borrowResource(String resourceId, String memberId, LocalDate today) {
        Resource resource = requireResource(resourceId);
        Member member = requireMember(memberId);
        if (!resource.isAvailable()) {
            throw new IllegalStateException(
                    resource.getId() + " is already on loan to " + resource.getBorrowedBy().getName() + ".");
        }
        if (countActiveLoans(member) >= MAX_ITEMS_PER_MEMBER) {
            throw new IllegalStateException(member.getName() + " already has " + MAX_ITEMS_PER_MEMBER
                    + " items on loan. One must be returned first.");
        }
        resource.borrow(member, today);
    }

    /**
     * Takes a resource back and returns the fine for the return.
     *
     * @param resourceId id of the resource
     * @param today      the date of the return
     * @return the fine, zero when the item is on time
     * @throws IllegalArgumentException if the resource does not exist
     * @throws IllegalStateException    if the resource is not on loan
     */
    public double returnResource(String resourceId, LocalDate today) {
        Resource resource = requireResource(resourceId);
        if (resource.isAvailable()) {
            throw new IllegalStateException(resource.getId() + " is not on loan, so it cannot be returned.");
        }
        return resource.returnItem(today);
    }

    /**
     * Counts how many items a member currently has.
     *
     * @param member the member to check
     * @return the number of resources on loan to that member
     */
    public int countActiveLoans(Member member) {
        int count = 0;
        for (Resource resource : resources) {
            if (!resource.isAvailable() && resource.getBorrowedBy().getId().equals(member.getId())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Searches for resources whose title contains the given text. The search is
     * not case sensitive, so "clean" and "Clean" find the same book.
     *
     * @param text the text to look for
     * @return every match, in the order the resources were added
     */
    public List<Resource> searchByTitle(String text) {
        List<Resource> matches = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return matches;
        }
        String wanted = text.toLowerCase();
        for (Resource resource : resources) {
            if (resource.getTitle().toLowerCase().contains(wanted)) {
                matches.add(resource);
            }
        }
        return matches;
    }

    /**
     * Searches for resources of one type, for example DVD.
     *
     * @param type the type name, not case sensitive
     * @return every resource of that type
     */
    public List<Resource> searchByType(String type) {
        List<Resource> matches = new ArrayList<>();
        if (type == null || type.isBlank()) {
            return matches;
        }
        for (Resource resource : resources) {
            if (resource.getType().equalsIgnoreCase(type)) {
                matches.add(resource);
            }
        }
        return matches;
    }

    /**
     * Returns every resource that is currently available.
     *
     * @return the available resources
     */
    public List<Resource> availableResources() {
        List<Resource> available = new ArrayList<>();
        for (Resource resource : resources) {
            if (resource.isAvailable()) {
                available.add(resource);
            }
        }
        return available;
    }

    /**
     * Returns a read only view of the resources.
     *
     * @return the resources in the library
     */
    public List<Resource> getResources() {
        return Collections.unmodifiableList(resources);
    }

    /**
     * Returns a read only view of the members.
     *
     * @return the registered members
     */
    public List<Member> getMembers() {
        return Collections.unmodifiableList(members);
    }

    /**
     * Writes every member and resource to a text file, one record per line.
     * Members are written first so they can be found again while the resources
     * are being read back.
     *
     * @param path the file to write
     * @throws IOException if the file cannot be written
     */
    public void saveData(Path path) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Member member : members) {
            lines.add(member.toDataLine());
        }
        for (Resource resource : resources) {
            lines.add("RESOURCE|" + resource.toDataLine());
        }
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    /**
     * Reads the records from a text file and adds them to the library.
     *
     * <p>Members are read first because a resource line only stores the id of
     * the member who has it. Lines that cannot be understood are skipped and
     * counted, so one damaged line does not stop the program from starting.</p>
     *
     * @param path the file to read
     * @return the number of lines that were skipped because they were not valid
     * @throws IOException if the file cannot be read
     */
    public int loadData(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        int skipped = 0;

        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            if (line.startsWith("MEMBER|")) {
                try {
                    members.add(parseMember(line));
                } catch (RuntimeException e) {
                    // A bad number or date throws an unchecked exception; skip the record.
                    skipped++;
                }
            }
        }

        for (String line : lines) {
            if (line.isBlank() || !line.startsWith("RESOURCE|")) {
                continue;
            }
            try {
                resources.add(parseResource(line.substring("RESOURCE|".length())));
            } catch (RuntimeException e) {
                // Same as above: one unreadable record should not stop the program.
                skipped++;
            }
        }
        return skipped;
    }

    /**
     * Builds a member from one line of the save file.
     *
     * @param line the MEMBER line
     * @return the member
     * @throws IllegalArgumentException if the line is malformed
     */
    private Member parseMember(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 5) {
            throw new IllegalArgumentException("Bad member line: " + line);
        }
        return new Member(parts[1], parts[2], parts[3], LocalDate.parse(parts[4]));
    }

    /**
     * Builds a resource from one line of the save file, including the loan
     * details when the item is out.
     *
     * @param line the line without the RESOURCE prefix
     * @return the resource
     * @throws IllegalArgumentException if the line is malformed
     */
    private Resource parseResource(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 8) {
            throw new IllegalArgumentException("Bad resource line: " + line);
        }
        String type = parts[0];
        String id = parts[1];
        String title = parts[2];
        int year = Integer.parseInt(parts[3]);
        String borrowerId = parts[4];
        String dueDate = parts[5];

        Resource resource;
        switch (type) {
            case "BOOK":
                resource = new Book(id, title, year, parts[6], parts[7]);
                break;
            case "MAGAZINE":
                resource = new Magazine(id, title, year, Integer.parseInt(parts[6]), parts[7]);
                break;
            case "DVD":
                resource = new DVD(id, title, year, parts[6], Integer.parseInt(parts[7]));
                break;
            case "EBOOK":
                resource = new EBook(id, title, year, Double.parseDouble(parts[6]), parts[7]);
                break;
            default:
                throw new IllegalArgumentException("Unknown resource type: " + type);
        }

        if (!borrowerId.equals("-")) {
            Member borrower = findMemberById(borrowerId);
            if (borrower == null) {
                throw new IllegalArgumentException("Resource " + id + " refers to unknown member " + borrowerId);
            }
            resource.restoreLoanState(borrower, LocalDate.parse(dueDate));
        }
        return resource;
    }

    /**
     * Finds a resource or reports that it does not exist.
     *
     * @param id the id to look for
     * @return the resource
     * @throws IllegalArgumentException if there is no resource with that id
     */
    private Resource requireResource(String id) {
        Resource resource = findResourceById(id);
        if (resource == null) {
            throw new IllegalArgumentException("No resource found with ID " + id + ".");
        }
        return resource;
    }

    /**
     * Finds a member or reports that they do not exist.
     *
     * @param id the id to look for
     * @return the member
     * @throws IllegalArgumentException if there is no member with that id
     */
    private Member requireMember(String id) {
        Member member = findMemberById(id);
        if (member == null) {
            throw new IllegalArgumentException("No member found with ID " + id + ".");
        }
        return member;
    }
}
