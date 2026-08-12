package it.niedermann.nextcloud.deck.data.local.mapper;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Attachment;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.Comment;
import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.model.AccessControl;
import it.niedermann.nextcloud.deck.domain.model.Activity;

public class CommonLocalMapper {

    public Board.ID toBoardId(long value) {
        return new Board.ID(value);
    }

    public long fromBoardId(Board.ID id) {
        return id != null ? id.value() : 0L;
    }

    public Board.RemoteID toBoardRemoteId(Long value) {
        return value != null ? new Board.RemoteID(value) : null;
    }

    public Long fromBoardRemoteId(Board.RemoteID id) {
        return id != null ? id.value() : null;
    }

    public Column.ID toColumnId(long value) {
        return new Column.ID(value);
    }

    public long fromColumnId(Column.ID id) {
        return id != null ? id.value() : 0L;
    }

    public Column.RemoteID toColumnRemoteId(Long value) {
        return value != null ? new Column.RemoteID(value) : null;
    }

    public Long fromColumnRemoteId(Column.RemoteID id) {
        return id != null ? id.value() : null;
    }

    public Card.ID toCardId(long value) {
        return new Card.ID(value);
    }

    public long fromCardId(Card.ID id) {
        return id != null ? id.value() : 0L;
    }

    public Card.RemoteID toCardRemoteId(Long value) {
        return value != null ? new Card.RemoteID(value) : null;
    }

    public Long fromCardRemoteId(Card.RemoteID id) {
        return id != null ? id.value() : null;
    }

    public Label.ID toLabelId(long value) {
        return new Label.ID(value);
    }

    public long fromLabelId(Label.ID id) {
        return id != null ? id.value() : 0L;
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

    public User.RemoteID toUserRemoteId(String value) {
        return value != null ? new User.RemoteID(value) : null;
    }

    public String fromUserRemoteId(User.RemoteID id) {
        return id != null ? id.value() : null;
    }

    public Account.ID toAccountId(long value) {
        return new Account.ID(value);
    }

    public long fromAccountId(Account.ID id) {
        return id != null ? id.value() : 0L;
    }

    public Attachment.ID toAttachmentId(long value) {
        return new Attachment.ID(value);
    }

    public long fromAttachmentId(Attachment.ID id) {
        return id != null ? id.value() : 0L;
    }

    public Attachment.RemoteID toAttachmentRemoteId(Long value) {
        return value != null ? new Attachment.RemoteID(value) : null;
    }

    public Long fromAttachmentRemoteId(Attachment.RemoteID id) {
        return id != null ? id.value() : null;
    }

    public Attachment.FileSize toFileSize(long value) {
        return new Attachment.FileSize(value);
    }

    public long fromFileSize(Attachment.FileSize size) {
        return size != null ? size.bytes() : 0L;
    }

    public Comment.ID toCommentId(long value) {
        return new Comment.ID(value);
    }

    public long fromCommentId(Comment.ID id) {
        return id != null ? id.value() : 0L;
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

    public Activity.ID toActivityId(long value) {
        return new Activity.ID(value);
    }

    public long fromActivityId(Activity.ID id) {
        return id != null ? id.value() : 0L;
    }

    public Activity.RemoteID toActivityRemoteId(Long value) {
        return value != null ? new Activity.RemoteID(value) : null;
    }

    public Long fromActivityRemoteId(Activity.RemoteID id) {
        return id != null ? id.value() : null;
    }
}
