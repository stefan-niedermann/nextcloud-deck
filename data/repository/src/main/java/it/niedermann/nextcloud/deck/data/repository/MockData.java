package it.niedermann.nextcloud.deck.data.repository;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import it.niedermann.nextcloud.deck.domain.model.Account;
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

    User[] MOCK_USERS = new User[]{
            new User(new User.ID("user1"), "John Doe"),
            new User(new User.ID("user2"), "MR. Smith")
    };

    Comment[] MOCK_COMMENTS = new Comment[]{
            new Comment(new Comment.ID(1), MOCK_USERS[0], LocalDateTime.now(), "This is a creative comment.", Optional.empty()),
            new Comment(new Comment.ID(2), MOCK_USERS[1], LocalDateTime.now(), "This is a creative comment.", Optional.empty()),
    };

    List<Card> MOCK_CARDS = List.of(
            new Card(new Card.ID(0), new Account.ID(0), new Board.ID(0), new Column.ID(0), LocalDateTime.now(), 0, "Card-Title #0", "Card-Description 0 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), LocalDateTime.now(), null, null, null, false, false, 0, 3),
            new Card(new Card.ID(1), new Account.ID(0), new Board.ID(0), new Column.ID(0), LocalDateTime.now(), 0, "Card-Title #1", "", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), LocalDateTime.now(), null, null, null, false, false, 0, 0),
            new Card(new Card.ID(2), new Account.ID(0), new Board.ID(1), new Column.ID(1), LocalDateTime.now(), 0, "Card-Title #2", "- [ ] Check 1\n- [x] Check 2\n- [ ] Check 3", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), LocalDateTime.now(), null, null, null, false, false, 0, 4),
            new Card(new Card.ID(3), new Account.ID(0), new Board.ID(1), new Column.ID(1), LocalDateTime.now(), 0, "Card-Title #3", "Card-Description 3 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Arrays.stream(MOCK_COMMENTS).map(Comment::id).collect(Collectors.toList()), Collections.emptyList(), Collections.emptyList(), LocalDateTime.now(), null, null, null, false, false, 0, 6),
            new Card(new Card.ID(4), new Account.ID(0), new Board.ID(1), new Column.ID(2), LocalDateTime.now(), 0, "Card-Title #4", "Card-Description 4 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), LocalDateTime.now(), null, null, null, false, false, 0, 0),
            new Card(new Card.ID(5), new Account.ID(0), new Board.ID(1), new Column.ID(2), LocalDateTime.now(), 0, "Card-Title #5", "Card-Description 5 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), LocalDateTime.now(), null, null, null, false, false, 0, 1),
            new Card(new Card.ID(6), new Account.ID(0), new Board.ID(1), new Column.ID(3), LocalDateTime.now(), 0, "Card-Title #6", "Card-Description 6 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), LocalDateTime.now(), null, null, null, false, false, 0, 0),
            new Card(new Card.ID(7), new Account.ID(0), new Board.ID(1), new Column.ID(3), LocalDateTime.now(), 0, "Card-Title #7", "Card-Description 7 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), LocalDateTime.now(), null, null, null, false, false, 0, 0),
            new Card(new Card.ID(8), new Account.ID(0), new Board.ID(2), new Column.ID(4), LocalDateTime.now(), 0, "Card-Title #8", "Card-Description 8 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), LocalDateTime.now(), null, null, null, false, false, 0, 6),
            new Card(new Card.ID(9), new Account.ID(0), new Board.ID(2), new Column.ID(4), LocalDateTime.now(), 0, "Card-Title #9", "Card-Description 9 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), LocalDateTime.now(), null, null, null, false, false, 0, 0)
    );

    Column[] MOCK_COLUMNS = new Column[]{
            new Column(new Column.ID(1), "ToDo"),
            new Column(new Column.ID(2), "WiP"),
            new Column(new Column.ID(3), "Done"),
            new Column(new Column.ID(4), "Erste Spalte"),
            new Column(new Column.ID(5), "Zweite Spalte"),
            new Column(new Column.ID(6), "Dritte Spalt"),
            new Column(new Column.ID(7), "One"),
            new Column(new Column.ID(8), "Two"),
            new Column(new Column.ID(9), "Three"),
    };

    Board[] MOCK_BOARDS = new Board[]{
            new Board(new Board.ID(1), "Board #1", MOCK_COLORS[1], Arrays.asList(Arrays.copyOfRange(MOCK_COLUMNS, 0, 2)), Collections.emptySet()),
            new Board(new Board.ID(2), "Board #2", MOCK_COLORS[2], Arrays.asList(Arrays.copyOfRange(MOCK_COLUMNS, 3, 5)), Collections.emptySet()),
            new Board(new Board.ID(3), "Board #3", MOCK_COLORS[3], Arrays.asList(Arrays.copyOfRange(MOCK_COLUMNS, 6, 8)), Collections.emptySet()),
            new Board(new Board.ID(4), "Board #4", MOCK_COLORS[4], Collections.emptyList(), Collections.emptySet()),
            new Board(new Board.ID(5), "Board #5", MOCK_COLORS[5], Collections.emptyList(), Collections.emptySet()),
            new Board(new Board.ID(6), "Board #6", MOCK_COLORS[6], Collections.emptyList(), Collections.emptySet()),
            new Board(new Board.ID(7), "Board #7", MOCK_COLORS[7], Collections.emptyList(), Collections.emptySet()),
            new Board(new Board.ID(8), "Board #8", MOCK_COLORS[8], Collections.emptyList(), Collections.emptySet()),
            new Board(new Board.ID(9), "Board #9", MOCK_COLORS[9], Collections.emptyList(), Collections.emptySet()),
            new Board(new Board.ID(10), "Board #10", MOCK_COLORS[10], Collections.emptyList(), Collections.emptySet())
    };

    Attachment[] MOCK_ATTACHMENTS = new Attachment[]{
            new Attachment(new Attachment.ID(1), new Card.ID(1), "Sample File", LocalDateTime.now(), new User(new User.ID("sample"), "Sampson Sample"), Optional.empty(), 310_340, "image/png", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(1), new Card.ID(1), "Sample Image", LocalDateTime.now().minusDays(1).minusHours(8).minusMinutes(38), new User(new User.ID("sample"), "Sampson Sample"), Optional.empty(), 140_000_000, "image/png", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(1), new Card.ID(1), "Another image", LocalDateTime.now().minusDays(10).minusHours(2).minusMinutes(17), new User(new User.ID("sample"), "Sampson Sample"), Optional.empty(), 340_509_000, "image/jpg", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(2), new Card.ID(1), "Sample File", LocalDateTime.now(), new User(new User.ID("sample"), "Sampson Sample"), Optional.empty(), 310_340, "image/png", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(2), new Card.ID(1), "Sample Image", LocalDateTime.now().minusDays(1).minusHours(8).minusMinutes(38), new User(new User.ID("sample"), "Sampson Sample"), Optional.empty(), 140_000_000, "image/png", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(2), new Card.ID(1), "Another image", LocalDateTime.now().minusDays(10).minusHours(2).minusMinutes(17), new User(new User.ID("sample"), "Sampson Sample"), Optional.empty(), 340_509_000, "image/jpg", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(3), new Card.ID(1), "Sample File", LocalDateTime.now(), new User(new User.ID("sample"), "Sampson Sample"), Optional.empty(), 310_340, "image/png", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(3), new Card.ID(1), "Sample Image", LocalDateTime.now().minusDays(1).minusHours(8).minusMinutes(38), new User(new User.ID("sample"), "Sampson Sample"), Optional.empty(), 140_000_000, "image/png", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(3), new Card.ID(1), "Another image", LocalDateTime.now().minusDays(10).minusHours(2).minusMinutes(17), new User(new User.ID("sample"), "Sampson Sample"), Optional.empty(), 340_509_000, "image/jpg", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(4), new Card.ID(1), "Sample File", LocalDateTime.now(), new User(new User.ID("sample"), "Sampson Sample"), Optional.empty(), 310_340, "image/png", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(4), new Card.ID(1), "Sample Image", LocalDateTime.now().minusDays(1).minusHours(8).minusMinutes(38), new User(new User.ID("sample"), "Sampson Sample"), Optional.empty(), 140_000_000, "image/png", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(4), new Card.ID(1), "Another image", LocalDateTime.now().minusDays(10).minusHours(2).minusMinutes(17), new User(new User.ID("sample"), "Sampson Sample"), Optional.empty(), 340_509_000, "image/jpg", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(5), new Card.ID(1), "Sample File", LocalDateTime.now(), new User(new User.ID("sample"), "Sampson Sample"), Optional.empty(), 310_340, "image/png", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(5), new Card.ID(1), "Sample Image", LocalDateTime.now().minusDays(1).minusHours(8).minusMinutes(38), new User(new User.ID("sample"), "Sampson Sample"), Optional.empty(), 140_000_000, "image/png", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(5), new Card.ID(1), "Another image", LocalDateTime.now().minusDays(10).minusHours(2).minusMinutes(17), new User(new User.ID("sample"), "Sampson Sample"), Optional.empty(), 340_509_000, "image/jpg", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(6), new Card.ID(1), "Sample File", LocalDateTime.now(), new User(new User.ID("sample"), "Sampson Sample"), Optional.empty(), 310_340, "image/png", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(6), new Card.ID(1), "Sample Image", LocalDateTime.now().minusDays(1).minusHours(8).minusMinutes(38), new User(new User.ID("sample"), "Sampson Sample"), Optional.empty(), 140_000_000, "image/png", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(6), new Card.ID(1), "Another image", LocalDateTime.now().minusDays(10).minusHours(2).minusMinutes(17), new User(new User.ID("sample"), "Sampson Sample"), Optional.empty(), 340_509_000, "image/jpg", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(7), new Card.ID(1), "Sample File", LocalDateTime.now(), new User(new User.ID("sample"), "Sampson Sample"), Optional.empty(), 310_340, "image/png", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(7), new Card.ID(1), "Sample Image", LocalDateTime.now().minusDays(1).minusHours(8).minusMinutes(38), new User(new User.ID("sample"), "Sampson Sample"), Optional.empty(), 140_000_000, "image/png", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(7), new Card.ID(1), "Another image", LocalDateTime.now().minusDays(10).minusHours(2).minusMinutes(17), new User(new User.ID("sample"), "Sampson Sample"), Optional.empty(), 340_509_000, "image/jpg", Optional.empty(), Optional.empty()),
    };

    Label[] MOCK_LABELS = new Label[]{
            new Label(new Label.ID(1), "Sample Label", Color.GREEN),
            new Label(new Label.ID(2), "Work in Progress", Color.PINK),
            new Label(new Label.ID(3), "Done", Color.CYAN),
            new Label(new Label.ID(4), "Important", Color.MAGENTA),
            new Label(new Label.ID(5), "Staffing", Color.DARK_GRAY),
            new Label(new Label.ID(6), "Prio 1", Color.RED),
            new Label(new Label.ID(7), "Prio 2", Color.ORANGE),
            new Label(new Label.ID(8), "Prio 3", Color.YELLOW),
            new Label(new Label.ID(9), "System: A", Color.GRAY),
            new Label(new Label.ID(10), "System: B", Color.GRAY),
            new Label(new Label.ID(11), "System: C", Color.GRAY),
            new Label(new Label.ID(12), "System: D", Color.GRAY),
            new Label(new Label.ID(13), "System: E", Color.GRAY),
            new Label(new Label.ID(14), "System: F", Color.GRAY),
    };
}
