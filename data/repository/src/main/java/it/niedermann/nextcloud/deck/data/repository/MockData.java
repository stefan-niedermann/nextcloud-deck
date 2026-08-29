package it.niedermann.nextcloud.deck.data.repository;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import it.niedermann.nextcloud.deck.domain.model.Attachment;
import it.niedermann.nextcloud.deck.domain.model.AttachmentType;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Capabilities;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Color;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.Comment;
import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.model.Version;

public interface MockData {

    Capabilities MOCK_CAPABILITIES = new Capabilities(
            new Version("1.11.0", 1, 11, 0),
            Color.decode("#0082c9"),
            true,
            true
    );

    Color[] MOCK_COLORS = new Color[]{
            Color.decode("#b6469d"),
            Color.decode("#bf678b"),
            Color.decode("#c98879"),
            Color.decode("#ddcb55"),
            Color.decode("#a5b872"),
            Color.decode("#6ea68f"),
            Color.decode("#3794ac"),
            Color.decode("#0082c9"),
            Color.decode("#2d73be"),
            Color.decode("#5b64b3"),
            Color.decode("#8855a8")
    };

    User[] MOCK_USERS = new User[]{
            new User(new User.ID("jdoe"), "John Doe"),
            new User(new User.ID("smith"), "MR. Smith"),
            new User(new User.ID("admin"), "Administrator"),
            new User(new User.ID("guest"), "Guest User")
    };

    Board[] MOCK_BOARDS = new Board[]{
            board(1, "Personal Tasks", MOCK_COLORS[0], true, true, true, true),
            board(2, "Read-only Archive", MOCK_COLORS[1], true, false, false, false),
            board(3, "Project Beta (Contributor)", MOCK_COLORS[2], true, true, false, false),
            board(4, "Project Gamma (Manager)", MOCK_COLORS[3], true, true, true, false),
            board(5, "Community (Shared Viewer)", MOCK_COLORS[4], true, false, false, true),
            board(6, "Work: Core App", MOCK_COLORS[5], true, true, true, true),
            board(7, "Work: API Docs", MOCK_COLORS[6], true, true, true, false),
            board(8, "Family Tasks", MOCK_COLORS[7], true, true, false, true),
            board(9, "Holiday 2026", MOCK_COLORS[8], true, true, true, true),
            board(10, "Software Roadmap", MOCK_COLORS[9], true, true, true, true)
    };

    Label[] MOCK_LABELS = new Label[]{
            // Board 1 Labels
            label(101, 1, "Urgent", MOCK_COLORS[0]),
            label(102, 1, "Prio Medium", MOCK_COLORS[3]),
            label(103, 1, "Low Prio", MOCK_COLORS[5]),
            // Board 6 Labels
            label(601, 6, "Bug", MOCK_COLORS[0]),
            label(602, 6, "Feature", MOCK_COLORS[7]),
            label(603, 6, "Refactoring", MOCK_COLORS[10]),
            // Board 10 Labels
            label(1001, 10, "Frontend", MOCK_COLORS[8]),
            label(1002, 10, "Backend", MOCK_COLORS[9]),
            label(1003, 10, "Infrastructure", MOCK_COLORS[2])
    };

    Column[] MOCK_COLUMNS = new Column[]{
            // Board 1: 3 columns
            column(11, 1, "To Do", 1),
            column(12, 1, "Doing", 2),
            column(13, 1, "Done", 3),
            // Board 2: 1 column
            column(21, 2, "Archived", 1),
            // Board 3: 2 columns
            column(31, 3, "Inbox", 1),
            column(32, 3, "Review", 2),
            // Board 6: 4 columns
            column(61, 6, "Analysis", 1),
            column(62, 6, "Development", 2),
            column(63, 6, "Testing", 3),
            column(64, 6, "Deployed", 4),
            // Board 10: 3 columns
            column(101, 10, "Backlog", 1),
            column(102, 10, "Sprint", 2),
            column(103, 10, "Finished", 3),
            // Other boards
            column(41, 4, "Tasks", 1),
            column(51, 5, "Public", 1),
            column(71, 7, "Docs", 1),
            column(81, 8, "Shopping List", 1),
            column(91, 9, "Itinerary", 1)
    };

    List<Card> MOCK_CARDS = Stream.of(
            // Board 1: 30 cards
            generateCards(1, 11, 10),
            generateCards(1, 12, 10),
            generateCards(1, 13, 10),
            // Board 6: 40 cards
            generateCards(6, 61, 10),
            generateCards(6, 62, 10),
            generateCards(6, 63, 10),
            generateCards(6, 64, 10),
            // Board 10: 30 cards
            generateCards(10, 101, 15),
            generateCards(10, 102, 10),
            generateCards(10, 103, 5),
            // Specific cards
            Stream.of(
                    card(2101, 21, 1, "Legacy Feature X", "This card was archived.", Set.of(), Set.of()),
                    card(3101, 31, 1, "New Feature Request", "Implement dark mode in all apps.", Set.of(), Set.of(new User.ID("jdoe"))),
                    card(4101, 41, 1, "Permissions Check", "Ensure only managers can see this.", Set.of(), Set.of(new User.ID("smith")))
            )
    ).flatMap(s -> s).collect(Collectors.toList());

    Comment[] MOCK_COMMENTS = new Comment[]{
            comment(1, 11100, "jdoe", "I've started working on this task."),
            comment(2, 11100, "smith", "Great, let me know if you need help!"),
            comment(3, 66100, "admin", "Critical bug report, please investigate ASAP.")
    };

    Attachment[] MOCK_ATTACHMENTS = new Attachment[]{
            new Attachment(new Attachment.ID(1), new Card.ID(11100), AttachmentType.FILE, "Architecture_v1.png", OffsetDateTime.now(), new User.ID("jdoe"), new Attachment.FileSize(1024 * 512), "image/png"),
            new Attachment(new Attachment.ID(2), new Card.ID(66100), AttachmentType.FILE, "Logs.txt", OffsetDateTime.now(), new User.ID("admin"), new Attachment.FileSize(2048), "text/plain")
    };

    // Helper methods for cleaner initialization
    static Board board(long id, String title, Color color, boolean read, boolean edit, boolean manage, boolean share) {
        return new Board(new Board.ID(id), title, color, new Board.Permissions(read, edit, manage, share));
    }

    static Column column(long id, long boardId, String title, int order) {
        return new Column(new Column.ID(id), new Board.ID(boardId), title, order);
    }

    static Label label(long id, long boardId, String title, Color color) {
        return new Label(new Label.ID(id), new Board.ID(boardId), title, color);
    }

    static Card card(long id, long columnId, int order, String title, String description, Set<Label.ID> labels, Set<User.ID> assignees) {
        return new Card(
                new Card.ID(id),
                new Card.RemoteID(id + 1000000),
                new Column.ID(columnId),
                OffsetDateTime.now(),
                order,
                title,
                description,
                labels,
                assignees,
                Collections.emptyList(),
                false,
                false,
                0,
                0
        );
    }

    static Comment comment(long id, long cardId, String userId, String text) {
        return new Comment(new Comment.ID(id), new Card.ID(cardId), new User.ID(userId), OffsetDateTime.now(), text);
    }

    static Stream<Card> generateCards(long boardId, long columnId, int count) {
        return IntStream.range(0, count).mapToObj(i -> {
            long id = boardId * 10000 + columnId * 100 + i;
            String title = "Task " + (i + 1) + " on Board " + boardId;
            String desc = "This is a detailed description for task " + id + ".\n\n" +
                    "- [ ] Subtask A\n" +
                    "- [" + (i % 2 == 0 ? "x" : " ") + "] Subtask B\n" +
                    "- [ ] Subtask C";

            final Set<Label.ID> labels = (i % 2 == 0) ? Set.of(new Label.ID(boardId * 100 + (i % 3 + 1))) : Collections.emptySet();
            final Set<User.ID> assignees = (i % 5 == 0) ? Set.of(new User.ID("jdoe")) :
                                    (i % 5 == 1) ? Set.of(new User.ID("smith")) :
                                    Collections.emptySet();

            return new Card(
                    new Card.ID(id),
                    new Card.RemoteID(id + 100000),
                    new Column.ID(columnId),
                    OffsetDateTime.now(),
                    i,
                    title,
                    desc,
                    labels,
                    assignees,
                    Collections.emptyList(),
                    false,
                    false,
                    0,
                    (i % 7 == 0) ? 3 : 0
            );
        });
    }
}
