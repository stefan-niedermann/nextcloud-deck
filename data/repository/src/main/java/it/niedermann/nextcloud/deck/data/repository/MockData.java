package it.niedermann.nextcloud.deck.data.repository;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import it.niedermann.nextcloud.deck.domain.model.Attachment;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.Comment;
import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.deck.domain.model.User;

public interface MockData {

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

    List<Card> MOCK_CARDS = List.of(
            new Card(0, 0, 0, 0, LocalDateTime.now(), LocalDateTime.now(), 0, "Card-Title #0", "Card-Description 0 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), Collections.emptyList(), null, LocalDateTime.now(), null, null, Collections.emptySet(), false, false, 0, 3),
            new Card(1, 0, 0, 0, LocalDateTime.now(), LocalDateTime.now(), 0, "Card-Title #1", "", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), Collections.emptyList(), null, LocalDateTime.now(), null, null, Collections.emptySet(), false, false, 0, 0),
            new Card(2, 0, 1, 1, LocalDateTime.now(), LocalDateTime.now(), 0, "Card-Title #2", "- [ ] Check 1\n- [x] Check 2\n- [ ] Check 3", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), Collections.emptyList(), null, LocalDateTime.now(), null, null, Collections.emptySet(), false, false, 0, 4),
            new Card(3, 0, 1, 1, LocalDateTime.now(), LocalDateTime.now(), 0, "Card-Title #3", "Card-Description 3 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), List.of(new Comment(
                    3,
                    new User("sample", "Sampson Sample 1"),
                    LocalDateTime.now(),
                    "This is a creative comment.",
                    Optional.empty()), new Comment(
                    4,
                    new User("sample", "Sampson Sample 2"),
                    LocalDateTime.now(),
                    "This is a creative comment.",
                    Optional.empty())), Collections.emptyList(), null, LocalDateTime.now(), null, null, Collections.emptySet(), false, false, 0, 6),
            new Card(4, 0, 1, 2, LocalDateTime.now(), LocalDateTime.now(), 0, "Card-Title #4", "Card-Description 4 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), Collections.emptyList(), null, LocalDateTime.now(), null, null, Collections.emptySet(), false, false, 0, 0),
            new Card(5, 0, 1, 2, LocalDateTime.now(), LocalDateTime.now(), 0, "Card-Title #5", "Card-Description 5 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), Collections.emptyList(), null, LocalDateTime.now(), null, null, Collections.emptySet(), false, false, 0, 1),
            new Card(6, 0, 1, 3, LocalDateTime.now(), LocalDateTime.now(), 0, "Card-Title #6", "Card-Description 6 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), Collections.emptyList(), null, LocalDateTime.now(), null, null, Collections.emptySet(), false, false, 0, 0),
            new Card(7, 0, 1, 3, LocalDateTime.now(), LocalDateTime.now(), 0, "Card-Title #7", "Card-Description 7 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), Collections.emptyList(), null, LocalDateTime.now(), null, null, Collections.emptySet(), false, false, 0, 0),
            new Card(8, 0, 2, 4, LocalDateTime.now(), LocalDateTime.now(), 0, "Card-Title #8", "Card-Description 8 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), Collections.emptyList(), null, LocalDateTime.now(), null, null, Collections.emptySet(), false, false, 0, 6),
            new Card(9, 0, 2, 4, LocalDateTime.now(), LocalDateTime.now(), 0, "Card-Title #9", "Card-Description 9 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), Collections.emptyList(), null, LocalDateTime.now(), null, null, Collections.emptySet(), false, false, 0, 0)
    );

    Column[] MOCK_COLUMNS = new Column[]{
            new Column(1, "ToDo"),
            new Column(2, "WiP"),
            new Column(3, "Done"),
            new Column(4, "Erste Spalte"),
            new Column(5, "Zweite Spalte"),
            new Column(6, "Dritte Spalt"),
            new Column(7, "One"),
            new Column(8, "Two"),
            new Column(9, "Three"),
    };

    Board[] MOCK_BOARDS = new Board[]{
            new Board(1, "Board #1", MOCK_COLORS[1], Arrays.asList(Arrays.copyOfRange(MOCK_COLUMNS, 0, 2))),
            new Board(2, "Board #2", MOCK_COLORS[2], Arrays.asList(Arrays.copyOfRange(MOCK_COLUMNS, 3, 5))),
            new Board(3, "Board #3", MOCK_COLORS[3], Arrays.asList(Arrays.copyOfRange(MOCK_COLUMNS, 6, 8))),
            new Board(4, "Board #4", MOCK_COLORS[4], Collections.emptyList()),
            new Board(5, "Board #5", MOCK_COLORS[5], Collections.emptyList()),
            new Board(6, "Board #6", MOCK_COLORS[6], Collections.emptyList()),
            new Board(7, "Board #7", MOCK_COLORS[7], Collections.emptyList()),
            new Board(8, "Board #8", MOCK_COLORS[8], Collections.emptyList()),
            new Board(9, "Board #9", MOCK_COLORS[9], Collections.emptyList()),
            new Board(10, "Board #10", MOCK_COLORS[10], Collections.emptyList())
    };

    Attachment[] MOCK_ATTACHMENTS = new Attachment[]{
            new Attachment(1, "Sample File", LocalDateTime.now(), new User("sample", "Sampson Sample"), Optional.empty(), 310_340, "image/png", Optional.empty(), Optional.empty()),
            new Attachment(1, "Sample Image", LocalDateTime.now().minusDays(1).minusHours(8).minusMinutes(38), new User("sample", "Sampson Sample"), Optional.empty(), 140_000_000, "image/png", Optional.empty(), Optional.empty()),
            new Attachment(1, "Another image", LocalDateTime.now().minusDays(10).minusHours(2).minusMinutes(17), new User("sample", "Sampson Sample"), Optional.empty(), 340_509_000, "image/jpg", Optional.empty(), Optional.empty()),
            new Attachment(2, "Sample File", LocalDateTime.now(), new User("sample", "Sampson Sample"), Optional.empty(), 310_340, "image/png", Optional.empty(), Optional.empty()),
            new Attachment(2, "Sample Image", LocalDateTime.now().minusDays(1).minusHours(8).minusMinutes(38), new User("sample", "Sampson Sample"), Optional.empty(), 140_000_000, "image/png", Optional.empty(), Optional.empty()),
            new Attachment(2, "Another image", LocalDateTime.now().minusDays(10).minusHours(2).minusMinutes(17), new User("sample", "Sampson Sample"), Optional.empty(), 340_509_000, "image/jpg", Optional.empty(), Optional.empty()),
            new Attachment(3, "Sample File", LocalDateTime.now(), new User("sample", "Sampson Sample"), Optional.empty(), 310_340, "image/png", Optional.empty(), Optional.empty()),
            new Attachment(3, "Sample Image", LocalDateTime.now().minusDays(1).minusHours(8).minusMinutes(38), new User("sample", "Sampson Sample"), Optional.empty(), 140_000_000, "image/png", Optional.empty(), Optional.empty()),
            new Attachment(3, "Another image", LocalDateTime.now().minusDays(10).minusHours(2).minusMinutes(17), new User("sample", "Sampson Sample"), Optional.empty(), 340_509_000, "image/jpg", Optional.empty(), Optional.empty()),
            new Attachment(4, "Sample File", LocalDateTime.now(), new User("sample", "Sampson Sample"), Optional.empty(), 310_340, "image/png", Optional.empty(), Optional.empty()),
            new Attachment(4, "Sample Image", LocalDateTime.now().minusDays(1).minusHours(8).minusMinutes(38), new User("sample", "Sampson Sample"), Optional.empty(), 140_000_000, "image/png", Optional.empty(), Optional.empty()),
            new Attachment(4, "Another image", LocalDateTime.now().minusDays(10).minusHours(2).minusMinutes(17), new User("sample", "Sampson Sample"), Optional.empty(), 340_509_000, "image/jpg", Optional.empty(), Optional.empty()),
            new Attachment(5, "Sample File", LocalDateTime.now(), new User("sample", "Sampson Sample"), Optional.empty(), 310_340, "image/png", Optional.empty(), Optional.empty()),
            new Attachment(5, "Sample Image", LocalDateTime.now().minusDays(1).minusHours(8).minusMinutes(38), new User("sample", "Sampson Sample"), Optional.empty(), 140_000_000, "image/png", Optional.empty(), Optional.empty()),
            new Attachment(5, "Another image", LocalDateTime.now().minusDays(10).minusHours(2).minusMinutes(17), new User("sample", "Sampson Sample"), Optional.empty(), 340_509_000, "image/jpg", Optional.empty(), Optional.empty()),
            new Attachment(6, "Sample File", LocalDateTime.now(), new User("sample", "Sampson Sample"), Optional.empty(), 310_340, "image/png", Optional.empty(), Optional.empty()),
            new Attachment(6, "Sample Image", LocalDateTime.now().minusDays(1).minusHours(8).minusMinutes(38), new User("sample", "Sampson Sample"), Optional.empty(), 140_000_000, "image/png", Optional.empty(), Optional.empty()),
            new Attachment(6, "Another image", LocalDateTime.now().minusDays(10).minusHours(2).minusMinutes(17), new User("sample", "Sampson Sample"), Optional.empty(), 340_509_000, "image/jpg", Optional.empty(), Optional.empty()),
            new Attachment(7, "Sample File", LocalDateTime.now(), new User("sample", "Sampson Sample"), Optional.empty(), 310_340, "image/png", Optional.empty(), Optional.empty()),
            new Attachment(7, "Sample Image", LocalDateTime.now().minusDays(1).minusHours(8).minusMinutes(38), new User("sample", "Sampson Sample"), Optional.empty(), 140_000_000, "image/png", Optional.empty(), Optional.empty()),
            new Attachment(7, "Another image", LocalDateTime.now().minusDays(10).minusHours(2).minusMinutes(17), new User("sample", "Sampson Sample"), Optional.empty(), 340_509_000, "image/jpg", Optional.empty(), Optional.empty()),
    };

    Label[] MOCK_LABELS = new Label[]{
            new Label(1, "Sample Label", Color.GREEN),
            new Label(2, "Work in Progress", Color.PINK),
            new Label(3, "Done", Color.CYAN),
            new Label(4, "Important", Color.MAGENTA),
            new Label(5, "Staffing", Color.DARK_GRAY),
            new Label(6, "Prio 1", Color.RED),
            new Label(7, "Prio 2", Color.ORANGE),
            new Label(8, "Prio 3", Color.YELLOW),
            new Label(9, "System: A", Color.GRAY),
            new Label(10, "System: B", Color.GRAY),
            new Label(11, "System: C", Color.GRAY),
            new Label(12, "System: D", Color.GRAY),
            new Label(13, "System: E", Color.GRAY),
            new Label(14, "System: F", Color.GRAY),
    };

    User[] MOCK_USERS = new User[]{
            new User("user1", "John Doe"),
            new User("user2", "MR. Smith")
    };
}
