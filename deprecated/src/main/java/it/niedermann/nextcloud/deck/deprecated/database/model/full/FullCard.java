package it.niedermann.nextcloud.deck.database.entity.full;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import it.niedermann.android.crosstabdnd.DragAndDropModel;
import it.niedermann.nextcloud.deck.database.entity.Attachment;
import it.niedermann.nextcloud.deck.database.entity.Card;
import it.niedermann.nextcloud.deck.database.entity.JoinCardWithLabel;
import it.niedermann.nextcloud.deck.database.entity.JoinCardWithUser;
import it.niedermann.nextcloud.deck.database.entity.Label;
import it.niedermann.nextcloud.deck.database.entity.User;
import it.niedermann.nextcloud.deck.database.entity.interfaces.IRemoteEntity;
import it.niedermann.nextcloud.deck.database.entity.ocs.comment.DeckComment;

public class FullCard implements IRemoteEntity, DragAndDropModel {

    @Ignore
    protected transient boolean isAttachmentsSorted = false;

    @Embedded
    public Card card;

    @Relation(entity = Label.class, parentColumn = "localId", entityColumn = "localId",
            associateBy = @Junction(value = JoinCardWithLabel.class, parentColumn = "cardId", entityColumn = "labelId"))

    public List<Label> labels = new ArrayList<>();

    @Relation(entity = User.class, parentColumn = "localId", entityColumn = "localId",
            associateBy = @Junction(value = JoinCardWithUser.class, parentColumn = "cardId", entityColumn = "userId"))
    public List<User> assignedUsers = new ArrayList<>();

    @Relation(parentColumn = "userId", entityColumn = "localId")
    public List<User> owner;

    @Relation(parentColumn = "localId", entityColumn = "cardId")
    public List<Attachment> attachments;

    @Relation(entity = DeckComment.class, parentColumn = "localId", entityColumn = "objectId", projection = "localId")
    public List<Long> commentIDs;

    public FullCard() {
        super();
    }

    public FullCard(FullCard fullCard) {
        this.card = new Card(fullCard.getCard());
        this.labels = copyList(fullCard.getLabels());
        this.assignedUsers = copyList(fullCard.getAssignedUsers());
        this.owner = copyList(fullCard.getOwner());
        this.attachments = copyList(fullCard.getAttachments());
        this.commentIDs = copyList(fullCard.getCommentIDs());
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    public List<User> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(List<User> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public void setCommentIDs(List<Long> commentIDs) {
        this.commentIDs = commentIDs;
    }

    public List<Long> getCommentIDs() {
        return commentIDs;
    }

    public int getCommentCount() {
        return commentIDs == null ? 0 : commentIDs.size();
    }

    public List<User> getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        List<User> user = new ArrayList<>();
        user.add(owner);
        this.owner = user;
    }

    public void setOwner(List<User> owner) {
        this.owner = owner;
    }

    public List<Attachment> getAttachments() {
        if (!isAttachmentsSorted && attachments != null) {
            Collections.sort(attachments);
            isAttachmentsSorted = true;
        }
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    @Ignore
    @Override
    public Card getEntity() {
        return card;
    }

    @NonNull
    @Override
    public String toString() {
        return "FullCard{" +
                "card=" + card +
                ", labels=" + labels +
                ", assignedUsers=" + assignedUsers +
                ", owner=" + owner +
                ", attachments=" + attachments +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FullCard fullCard = (FullCard) o;

        if (!Objects.equals(card, fullCard.card)) return false;
        if (!Objects.equals(labels, fullCard.labels))
            return false;
        if (!Objects.equals(assignedUsers, fullCard.assignedUsers))
            return false;
        if (!Objects.equals(owner, fullCard.owner)) return false;
        if (!Objects.equals(attachments, fullCard.attachments))
            return false;
        return Objects.equals(commentIDs, fullCard.commentIDs);
    }

    @Override
    public int hashCode() {
        int result = (isAttachmentsSorted ? 1 : 0);
        result = 31 * result + (card != null ? card.hashCode() : 0);
        result = 31 * result + (labels != null ? labels.hashCode() : 0);
        result = 31 * result + (assignedUsers != null ? assignedUsers.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (attachments != null ? attachments.hashCode() : 0);
        result = 31 * result + (commentIDs != null ? commentIDs.hashCode() : 0);
        return result;
    }

    @NonNull
    @Override
    public Long getComparableId() {
        return getLocalId();
    }
}
