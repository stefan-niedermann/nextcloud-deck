package it.niedermann.nextcloud.deck.data.repository;

import java.awt.Color;
import java.time.LocalDateTime;
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

    User[] MOCK_USERS = new User[]{
            new User(new User.ID("jdoe"), "John Doe"),
            new User(new User.ID("smith"), "MR. Smith")
    };

    Board[] MOCK_BOARDS = new Board[]{
            new Board(new Board.ID(1), "Board #1", MOCK_COLORS[1], new Board.Permissions(true, true, false, false)),
            new Board(new Board.ID(2), "Board #2", MOCK_COLORS[2], new Board.Permissions(true, false, false, false)),
            new Board(new Board.ID(3), "Board #3", MOCK_COLORS[3], new Board.Permissions(true, false, false, false)),
            new Board(new Board.ID(4), "Board #4", MOCK_COLORS[4], new Board.Permissions(true, false, false, false)),
            new Board(new Board.ID(5), "Board #5", MOCK_COLORS[5], new Board.Permissions(true, true, true, false)),
            new Board(new Board.ID(6), "Board #6", MOCK_COLORS[6], new Board.Permissions(true, true, true, false)),
            new Board(new Board.ID(7), "Board #7", MOCK_COLORS[7], new Board.Permissions(true, true, true, true)),
            new Board(new Board.ID(8), "Board #8", MOCK_COLORS[8], new Board.Permissions(true, true, true, true)),
            new Board(new Board.ID(9), "Board #9", MOCK_COLORS[9], new Board.Permissions(true, true, true, true)),
            new Board(new Board.ID(10), "Board #10", MOCK_COLORS[10], new Board.Permissions(true, true, true, true))
    };

    Label[] MOCK_LABELS = new Label[]{
            new Label(new Label.ID(1), new Board.ID(1), "Sample Label", Color.GREEN),
            new Label(new Label.ID(2), new Board.ID(1), "Work in Progress", Color.PINK),
            new Label(new Label.ID(3), new Board.ID(1), "Done", Color.CYAN),
            new Label(new Label.ID(4), new Board.ID(1), "Important", Color.MAGENTA),
            new Label(new Label.ID(5), new Board.ID(1), "Staffing", Color.DARK_GRAY),
            new Label(new Label.ID(6), new Board.ID(2), "Prio 1", Color.RED),
            new Label(new Label.ID(7), new Board.ID(2), "Prio 2", Color.ORANGE),
            new Label(new Label.ID(8), new Board.ID(2), "Prio 3", Color.YELLOW),
            new Label(new Label.ID(9), new Board.ID(2), "System: A", Color.GRAY),
            new Label(new Label.ID(10), new Board.ID(3), "System: B", Color.GRAY),
            new Label(new Label.ID(11), new Board.ID(3), "System: C", Color.GRAY),
            new Label(new Label.ID(12), new Board.ID(3), "System: D", Color.GRAY),
            new Label(new Label.ID(13), new Board.ID(3), "System: E", Color.GRAY),
            new Label(new Label.ID(14), new Board.ID(3), "System: F", Color.GRAY),
    };

    Column[] MOCK_COLUMNS = new Column[]{
            new Column(new Column.ID(1), new Board.ID(1), "ToDo", 1),
            new Column(new Column.ID(2), new Board.ID(1), "WiP", 2),
            new Column(new Column.ID(3), new Board.ID(2), "Done", 3),
            new Column(new Column.ID(4), new Board.ID(2), "Erste Spalte", 4),
            new Column(new Column.ID(5), new Board.ID(2), "Zweite Spalte", 5),
            new Column(new Column.ID(6), new Board.ID(3), "Dritte Spalt", 6),
            new Column(new Column.ID(7), new Board.ID(3), "One", 7),
            new Column(new Column.ID(8), new Board.ID(5), "Two", 8),
            new Column(new Column.ID(9), new Board.ID(6), "Three", 9),
    };

    Comment[] MOCK_COMMENTS = new Comment[]{
            new Comment(new Comment.ID(1), new Card.ID(1), new User.ID("jdoe"), LocalDateTime.now(), "This is a creative comment #1.", null),
            new Comment(new Comment.ID(1), new Card.ID(1), new User.ID("jdoe"), LocalDateTime.now(), "This is a creative comment #2.", null),
            new Comment(new Comment.ID(1), new Card.ID(1), new User.ID("jdoe"), LocalDateTime.now(), "This is a creative comment #3.", null),
            new Comment(new Comment.ID(2), new Card.ID(2), new User.ID("smith"), LocalDateTime.now(), "This is a creative comment #1.", null),
            new Comment(new Comment.ID(2), new Card.ID(2), new User.ID("smith"), LocalDateTime.now(), "This is a creative comment #2.", null),
            new Comment(new Comment.ID(2), new Card.ID(2), new User.ID("jdoe"), LocalDateTime.now(), "This is a creative comment #3.", null),
            new Comment(new Comment.ID(2), new Card.ID(3), new User.ID("smith"), LocalDateTime.now(), "This is a creative comment.", null),
            new Comment(new Comment.ID(2), new Card.ID(3), new User.ID("jdoe"), LocalDateTime.now(), "This is a creative comment.", null),
            new Comment(new Comment.ID(2), new Card.ID(4), new User.ID("smith"), LocalDateTime.now(), "This is a creative comment.", null),
            new Comment(new Comment.ID(2), new Card.ID(5), new User.ID("smith"), LocalDateTime.now(), "This is a creative comment.", null),
            new Comment(new Comment.ID(2), new Card.ID(6), new User.ID("jdoe"), LocalDateTime.now(), "This is a creative comment.", null),
    };

    List<Card> MOCK_CARDS = List.of(
            new Card(new Card.ID(0), new Column.ID(0), LocalDateTime.now(), 0, "Card-Title #0", "Card-Description 0 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), LocalDateTime.now(), null, null, null, false, false, 0, 3),
            new Card(new Card.ID(1), new Column.ID(0), LocalDateTime.now(), 1, "Card-Title #1", "", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), LocalDateTime.now(), null, null, null, false, false, 0, 0),
            new Card(new Card.ID(2), new Column.ID(1), LocalDateTime.now(), 2, "Card-Title #2", "- [ ] Check 1\n- [x] Check 2\n- [ ] Check 3", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), LocalDateTime.now(), null, null, null, false, false, 0, 4),
            new Card(new Card.ID(3), new Column.ID(1), LocalDateTime.now(), 3, "Card-Title #3", "Card-Description 3 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), LocalDateTime.now(), null, null, null, false, false, 0, 6),
            new Card(new Card.ID(4), new Column.ID(2), LocalDateTime.now(), 4, "Card-Title #4", "Card-Description 4 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), LocalDateTime.now(), null, null, null, false, false, 0, 0),
            new Card(new Card.ID(5), new Column.ID(2), LocalDateTime.now(), 5, "Card-Title #5", "Card-Description 5 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), LocalDateTime.now(), null, null, null, false, false, 0, 1),
            new Card(new Card.ID(6), new Column.ID(3), LocalDateTime.now(), 6, "Card-Title #6", "Card-Description 6 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), LocalDateTime.now(), null, null, null, false, false, 0, 0),
            new Card(new Card.ID(7), new Column.ID(3), LocalDateTime.now(), 7, "Card-Title #7", "Card-Description 7 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), LocalDateTime.now(), null, null, null, false, false, 0, 0),
            new Card(new Card.ID(8), new Column.ID(4), LocalDateTime.now(), 8, "Card-Title #8", "Card-Description 8 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), LocalDateTime.now(), null, null, null, false, false, 0, 6),
            new Card(new Card.ID(9), new Column.ID(9), LocalDateTime.now(), 9, "Card-Title #9", "Card-Description 9 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), LocalDateTime.now(), null, null, null, false, false, 0, 0)
    );

    Attachment[] MOCK_ATTACHMENTS = new Attachment[]{
            new Attachment(new Attachment.ID(1), new Card.ID(1), "Sample File", LocalDateTime.now(), new User.ID("jdoe"), Optional.empty(), new Attachment.FileSize(310_340), "image/png", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(2), new Card.ID(1), "Sample Image", LocalDateTime.now().minusDays(1).minusHours(8).minusMinutes(38), new User.ID("smith"), Optional.empty(), new Attachment.FileSize(140_000_000), "image/png", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(3), new Card.ID(1), "Another image", LocalDateTime.now().minusDays(10).minusHours(2).minusMinutes(17), new User.ID("smith"), Optional.empty(), new Attachment.FileSize(340_509_000), "image/jpg", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(4), new Card.ID(1), "Sample File", LocalDateTime.now(), new User.ID("jdoe"), Optional.empty(), new Attachment.FileSize(310_340), "image/png", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(5), new Card.ID(1), "Sample Image", LocalDateTime.now().minusDays(1).minusHours(8).minusMinutes(38), new User.ID("jdoe"), Optional.empty(), new Attachment.FileSize(140_000_000), "image/png", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(6), new Card.ID(2), "Another image", LocalDateTime.now().minusDays(10).minusHours(2).minusMinutes(17), new User.ID("smith"), Optional.empty(), new Attachment.FileSize(340_509_000), "image/jpg", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(7), new Card.ID(2), "Sample File", LocalDateTime.now(), new User.ID("jdoe"), Optional.empty(), new Attachment.FileSize(310_340), "image/png", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(8), new Card.ID(2), "Sample Image", LocalDateTime.now().minusDays(1).minusHours(8).minusMinutes(38), new User.ID("jdoe"), Optional.empty(), new Attachment.FileSize(140_000_000), "image/png", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(9), new Card.ID(3), "Another image", LocalDateTime.now().minusDays(10).minusHours(2).minusMinutes(17), new User.ID("smith"), Optional.empty(), new Attachment.FileSize(340_509_000), "image/jpg", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(10), new Card.ID(4), "Sample File", LocalDateTime.now(), new User.ID("jdoe"), Optional.empty(), new Attachment.FileSize(310_340), "image/png", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(11), new Card.ID(4), "Sample Image", LocalDateTime.now().minusDays(1).minusHours(8).minusMinutes(38), new User.ID("jdoe"), Optional.empty(), new Attachment.FileSize(140_000_000), "image/png", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(12), new Card.ID(6), "Another image", LocalDateTime.now().minusDays(10).minusHours(2).minusMinutes(17), new User.ID("smith"), Optional.empty(), new Attachment.FileSize(340_509_000), "image/jpg", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(13), new Card.ID(6), "Sample File", LocalDateTime.now(), new User.ID("jdoe"), Optional.empty(), new Attachment.FileSize(310_340), "image/png", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(14), new Card.ID(6), "Sample Image", LocalDateTime.now().minusDays(1).minusHours(8).minusMinutes(38), new User.ID("smith"), Optional.empty(), new Attachment.FileSize(140_000_000), "image/png", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(15), new Card.ID(7), "Another image", LocalDateTime.now().minusDays(10).minusHours(2).minusMinutes(17), new User.ID("smith"), Optional.empty(), new Attachment.FileSize(340_509_000), "image/jpg", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(16), new Card.ID(7), "Sample File", LocalDateTime.now(), new User.ID("jdoe"), Optional.empty(), new Attachment.FileSize(310_340), "image/png", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(17), new Card.ID(8), "Sample Image", LocalDateTime.now().minusDays(1).minusHours(8).minusMinutes(38), new User.ID("jdoe"), Optional.empty(), new Attachment.FileSize(140_000_000), "image/png", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(18), new Card.ID(9), "Another image", LocalDateTime.now().minusDays(10).minusHours(2).minusMinutes(17), new User.ID("jdoe"), Optional.empty(), new Attachment.FileSize(340_509_000), "image/jpg", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(19), new Card.ID(9), "Sample File", LocalDateTime.now(), new User.ID("jdoe"), Optional.empty(), new Attachment.FileSize(310_340), "image/png", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(20), new Card.ID(9), "Sample Image", LocalDateTime.now().minusDays(1).minusHours(8).minusMinutes(38), new User.ID("jdoe"), Optional.empty(), new Attachment.FileSize(140_000_000), "image/png", Optional.empty(), Optional.empty()),
            new Attachment(new Attachment.ID(21), new Card.ID(9), "Another image", LocalDateTime.now().minusDays(10).minusHours(2).minusMinutes(17), new User.ID("smith"), Optional.empty(), new Attachment.FileSize(340_509_000), "image/jpg", Optional.empty(), Optional.empty()),
    };
}
