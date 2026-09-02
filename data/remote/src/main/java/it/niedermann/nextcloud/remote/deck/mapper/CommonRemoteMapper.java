package it.niedermann.nextcloud.remote.deck.mapper;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import it.niedermann.nextcloud.deck.domain.model.AccessControl;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Activity;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.Comment;
import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.deck.domain.model.User;

public class CommonRemoteMapper {

    public Board.ID toBoardId(Long value) {
        return value != null ? new Board.ID(value) : null;
    }

    public Long fromBoardId(Board.ID id) {
        return id != null ? id.value() : null;
    }

    public Board.RemoteID toBoardRemoteId(Long value) {
        return value != null ? new Board.RemoteID(value) : null;
    }

    public Long fromBoardRemoteId(Board.RemoteID id) {
        return id != null ? id.value() : null;
    }

    public Column.ID toColumnId(Long value) {
        return value != null ? new Column.ID(value) : null;
    }

    public Long fromColumnId(Column.ID id) {
        return id != null ? id.value() : null;
    }

    public Column.RemoteID toColumnRemoteId(Long value) {
        return value != null ? new Column.RemoteID(value) : null;
    }

    public Long fromColumnRemoteId(Column.RemoteID id) {
        return id != null ? id.value() : null;
    }

    public Card.ID toCardId(Long value) {
        return value != null ? new Card.ID(value) : null;
    }

    public Long fromCardId(Card.ID id) {
        return id != null ? id.value() : null;
    }

    public Card.RemoteID toCardRemoteId(Long value) {
        return value != null ? new Card.RemoteID(value) : null;
    }

    public Long fromCardRemoteId(Card.RemoteID id) {
        return id != null ? id.value() : null;
    }

    public Label.ID toLabelId(Long value) {
        return value != null ? new Label.ID(value) : null;
    }

    public Long fromLabelId(Label.ID id) {
        return id != null ? id.value() : null;
    }

    public Label.RemoteID toLabelRemoteId(Long value) {
        return value != null ? new Label.RemoteID(value) : null;
    }

    public Long fromLabelRemoteId(Label.RemoteID id) {
        return id != null ? id.value() : null;
    }

    public User.ID toUserId(String value) {
        return value != null ? new User.ID(value) : null;
    }

    public String fromUserId(User.ID id) {
        return id != null ? id.value() : null;
    }

    public Account.ID toAccountId(Long value) {
        return value != null ? new Account.ID(value) : null;
    }

    public Long fromAccountId(Account.ID id) {
        return id != null ? id.value() : null;
    }

    public it.niedermann.nextcloud.deck.data.shared.Attachment.ID toAttachmentId(Long value) {
        return value != null ? new it.niedermann.nextcloud.deck.data.shared.Attachment.ID(value) : null;
    }

    public Long fromAttachmentId(it.niedermann.nextcloud.deck.data.shared.Attachment.ID id) {
        return id != null ? id.value() : null;
    }

    public it.niedermann.nextcloud.deck.data.shared.Attachment.RemoteID toAttachmentRemoteId(Long value) {
        return value != null ? new it.niedermann.nextcloud.deck.data.shared.Attachment.RemoteID(value) : null;
    }

    public Long fromAttachmentRemoteId(it.niedermann.nextcloud.deck.data.shared.Attachment.RemoteID id) {
        return id != null ? id.value() : null;
    }

    public it.niedermann.nextcloud.deck.data.shared.Attachment.FileSize toFileSize(Long value) {
        return value != null ? new it.niedermann.nextcloud.deck.data.shared.Attachment.FileSize(value) : null;
    }

    public Long fromFileSize(it.niedermann.nextcloud.deck.data.shared.Attachment.FileSize size) {
        return size != null ? size.bytes() : null;
    }

    public Comment.ID toCommentId(Long value) {
        return value != null ? new Comment.ID(value) : null;
    }

    public Long fromCommentId(Comment.ID id) {
        return id != null ? id.value() : null;
    }

    public Comment.RemoteID toCommentRemoteId(Long value) {
        return value != null ? new Comment.RemoteID(value) : null;
    }

    public Long fromCommentRemoteId(Comment.RemoteID id) {
        return id != null ? id.value() : null;
    }

    public AccessControl.RemoteID toAccessControlRemoteId(Long value) {
        return value != null ? new AccessControl.RemoteID(value) : null;
    }

    public Long fromAccessControlRemoteId(AccessControl.RemoteID id) {
        return id != null ? id.value() : null;
    }

    public Activity.ID toActivityId(Long value) {
        return value != null ? new Activity.ID(value) : null;
    }

    public Long fromActivityId(Activity.ID id) {
        return id != null ? id.value() : null;
    }

    public Activity.RemoteID toActivityRemoteId(Long value) {
        return value != null ? new Activity.RemoteID(value) : null;
    }

    public Long fromActivityRemoteId(Activity.RemoteID id) {
        return id != null ? id.value() : null;
    }

    public OffsetDateTime toOffsetDateTime(Long timestamp) {
        return timestamp != null ? OffsetDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneOffset.UTC) : null;
    }

    public Long fromOffsetDateTime(OffsetDateTime dateTime) {
        return dateTime != null ? dateTime.toEpochSecond() : null;
    }

    public it.niedermann.nextcloud.deck.domain.model.Color toColor(String value) {
        return value != null ? it.niedermann.nextcloud.deck.domain.model.Color.decode(value) : null;
    }

    public String fromColor(it.niedermann.nextcloud.deck.domain.model.Color color) {
        if (color == null) return null;
        return String.format("%06X", (0xFFFFFF & color.argb()));
    }

    public String mapDisplayName(String displayName) {
        return displayName != null ? displayName : "";
    }
}
