package it.niedermann.nextcloud.deck.data.repository;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
            new User(new User.ID("smith"), "MR. Smith")
    };

    Board[] MOCK_BOARDS = new Board[]{
            new Board(new Board.ID(1), "Board #1", MOCK_COLORS[1], new Board.Permissions(true, true, true, true)),
            new Board(new Board.ID(2), "Board #2", MOCK_COLORS[2], new Board.Permissions(true, false, true, false)),
            new Board(new Board.ID(3), "Board #3", MOCK_COLORS[3], new Board.Permissions(true, false, false, true)),
            new Board(new Board.ID(4), "Board #4", MOCK_COLORS[4], new Board.Permissions(true, false, false, false)),
            new Board(new Board.ID(5), "Board #5", MOCK_COLORS[5], new Board.Permissions(true, true, true, false)),
            new Board(new Board.ID(6), "Board #6", MOCK_COLORS[6], new Board.Permissions(true, true, true, false)),
            new Board(new Board.ID(7), "Board #7", MOCK_COLORS[7], new Board.Permissions(true, true, true, true)),
            new Board(new Board.ID(8), "Board #8", MOCK_COLORS[8], new Board.Permissions(true, true, true, true)),
            new Board(new Board.ID(9), "Board #9", MOCK_COLORS[9], new Board.Permissions(true, true, true, true)),
            new Board(new Board.ID(10), "Board #10", MOCK_COLORS[10], new Board.Permissions(true, true, true, true))
    };

    Label[] MOCK_LABELS = new Label[]{
            new Label(new Label.ID(1), new Board.ID(1), "Sample Label", new Color(0, 255, 0)),
            new Label(new Label.ID(2), new Board.ID(1), "Work in Progress", new Color(255, 175, 175)),
            new Label(new Label.ID(3), new Board.ID(1), "Done", new Color(0, 255, 255)),
            new Label(new Label.ID(4), new Board.ID(1), "Important", new Color(255, 0, 255)),
            new Label(new Label.ID(5), new Board.ID(1), "Staffing", new Color(64, 64, 64)),
            new Label(new Label.ID(6), new Board.ID(2), "Prio 1", new Color(255, 0, 0)),
            new Label(new Label.ID(7), new Board.ID(2), "Prio 2", new Color(255, 200, 0)),
            new Label(new Label.ID(8), new Board.ID(2), "Prio 3", new Color(255, 255, 0)),
            new Label(new Label.ID(9), new Board.ID(2), "System: A", new Color(128, 128, 128)),
            new Label(new Label.ID(10), new Board.ID(3), "System: B", new Color(128, 128, 128)),
            new Label(new Label.ID(11), new Board.ID(3), "System: C", new Color(128, 128, 128)),
            new Label(new Label.ID(12), new Board.ID(3), "System: D", new Color(128, 128, 128)),
            new Label(new Label.ID(13), new Board.ID(3), "System: E", new Color(128, 128, 128)),
            new Label(new Label.ID(14), new Board.ID(3), "System: F", new Color(128, 128, 128)),
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
            new Comment(new Comment.ID(1), new Card.ID(1), new User.ID("jdoe"), OffsetDateTime.now(), "This is a creative comment #1."),
            new Comment(new Comment.ID(2), new Card.ID(1), new User.ID("jdoe"), OffsetDateTime.now(), "This is a creative comment #2."),
            new Comment(new Comment.ID(3), new Card.ID(1), new User.ID("jdoe"), OffsetDateTime.now(), "This is a creative comment #3."),
            new Comment(new Comment.ID(4), new Card.ID(2), new User.ID("smith"), OffsetDateTime.now(), "This is a creative comment #1."),
            new Comment(new Comment.ID(5), new Card.ID(2), new User.ID("smith"), OffsetDateTime.now(), "This is a creative comment #2."),
            new Comment(new Comment.ID(6), new Card.ID(2), new User.ID("jdoe"), OffsetDateTime.now(), "This is a creative comment #3."),
            new Comment(new Comment.ID(7), new Card.ID(3), new User.ID("smith"), OffsetDateTime.now(), "This is a creative comment."),
            new Comment(new Comment.ID(8), new Card.ID(3), new User.ID("jdoe"), OffsetDateTime.now(), "This is a creative comment."),
            new Comment(new Comment.ID(9), new Card.ID(4), new User.ID("smith"), OffsetDateTime.now(), "This is a creative comment."),
            new Comment(new Comment.ID(10), new Card.ID(5), new User.ID("smith"), OffsetDateTime.now(), "This is a creative comment."),
            new Comment(new Comment.ID(11), new Card.ID(6), new User.ID("jdoe"), OffsetDateTime.now(), "This is a creative comment."),
    };

    List<Card> MOCK_CARDS = List.of(
            new Card(new Card.ID(0), new Card.RemoteID(100), new Column.ID(1), OffsetDateTime.now(), 0, "Card-Title #0", "Card-Description 0 Lorem Ipsum Dolor sit Amet", Set.of(new Label.ID(1), new Label.ID(2)), Set.of(new User.ID("jdoe")), Collections.emptyList(), false, false, 0, 3),
            new Card(new Card.ID(1), new Card.RemoteID(101), new Column.ID(1), OffsetDateTime.now(), 1, "Card-Title #1", "", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), false, false, 0, 0),
            new Card(new Card.ID(2), null, new Column.ID(1), OffsetDateTime.now(), 2, "Card-Title #2", "- [ ] Check 1\n- [x] Check 2\n- [ ] Check 3", Set.of(new Label.ID(3)), Set.of(new User.ID("jdoe")), Collections.emptyList(), false, false, 0, 4),
            new Card(new Card.ID(3), new Card.RemoteID(103), new Column.ID(1), OffsetDateTime.now(), 3, "Card-Title #3", "Card-Description 3 Lorem Ipsum Dolor sit Amet", Set.of(new Label.ID(4), new Label.ID(5)), Set.of(new User.ID("smith")), Collections.emptyList(), false, false, 0, 6),
            new Card(new Card.ID(2), null, new Column.ID(2), OffsetDateTime.now(), 4, "Card-Title #4", "Card-Description 4 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), false, false, 0, 0),
            new Card(new Card.ID(5), new Card.RemoteID(105), new Column.ID(2), OffsetDateTime.now(), 5, "Card-Title #5", "Card-Description 5 Lorem Ipsum Dolor sit Amet", Set.of(new Label.ID(1)), Collections.emptySet(), Collections.emptyList(), false, false, 0, 1),
            new Card(new Card.ID(6), null, new Column.ID(3), OffsetDateTime.now(), 6, "Card-Title #6", "Card-Description 6 Lorem Ipsum Dolor sit Amet", Set.of(new Label.ID(6), new Label.ID(7)), Collections.emptySet(), Collections.emptyList(), false, false, 0, 0),
            new Card(new Card.ID(7), new Card.RemoteID(107), new Column.ID(3), OffsetDateTime.now(), 7, "Card-Title #7", "Card-Description 7 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), false, false, 0, 0),
            new Card(new Card.ID(8), null, new Column.ID(4), OffsetDateTime.now(), 8, "A very very long Card-Title for the card with the number #8", "Card-Description 8 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), false, false, 0, 6),
            new Card(new Card.ID(9), new Card.RemoteID(109), new Column.ID(9), OffsetDateTime.now(), 9, "Card-Title #9", "Card-Description 9 Lorem Ipsum Dolor sit Amet", Collections.emptySet(), Collections.emptySet(), Collections.emptyList(), false, false, 0, 0)
    );

    Attachment[] MOCK_ATTACHMENTS = new Attachment[]{
            new Attachment(new Attachment.ID(1), new Card.ID(1), AttachmentType.FILE, "Sample File", OffsetDateTime.now(), new User.ID("jdoe"), new Attachment.FileSize(310_340), "image/png"),
            new Attachment(new Attachment.ID(2), new Card.ID(1), AttachmentType.FILE, "Sample Image", OffsetDateTime.now(), new User.ID("smith"), new Attachment.FileSize(140_000_000), "image/png")
    };
}
