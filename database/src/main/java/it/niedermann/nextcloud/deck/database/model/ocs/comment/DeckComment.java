package it.niedermann.nextcloud.deck.database.model.ocs.comment;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.niedermann.nextcloud.deck.database.model.Account;
import it.niedermann.nextcloud.deck.database.model.Card;
import it.niedermann.nextcloud.deck.database.model.interfaces.AbstractRemoteEntity;

@Entity(inheritSuperIndices = true,
        indices = {
                @Index(value = "accountId", name = "comment_accID"),
                @Index("objectId"),
                @Index(value = "parentId", name = "idx_comment_parentID")
        },
        foreignKeys = {
                @ForeignKey(
                        entity = Card.class,
                        parentColumns = "localId",
                        childColumns = "objectId", onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = DeckComment.class,
                        parentColumns = "localId",
                        childColumns = "parentId", onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Account.class,
                        parentColumns = "id",
                        childColumns = "accountId", onDelete = ForeignKey.CASCADE
                )
        }
)
public class DeckComment extends AbstractRemoteEntity {
    private static final transient int MAX_MESSAGE_LENGTH = 1000;

    private Long objectId;
    private String actorType;
    private Instant creationDateTime;
    private String actorId;
    private String actorDisplayName;
    private String message;
    private Long parentId;
    @Ignore
    private List<Mention> mentions = new ArrayList<>();

    public DeckComment() {
    }

    @Ignore
    public DeckComment(String message, String actorDisplayName, Instant creationDateTime) {
        setMessage(message);
        setActorDisplayName(actorDisplayName);
        setCreationDateTime(creationDateTime);
    }

    public DeckComment(long cardId, String actorId, String actorDisplayName, String message) {
        this.objectId = cardId;
        this.actorId = actorId;
        this.actorDisplayName = actorDisplayName;
        setMessage(message);
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getActorType() {
        return actorType;
    }

    public void setActorType(String actorType) {
        this.actorType = actorType;
    }

    public Instant getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(Instant creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public String getActorDisplayName() {
        return actorDisplayName;
    }

    public void setActorDisplayName(String actorDisplayName) {
        this.actorDisplayName = actorDisplayName;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getMessage() {
        return message;
    }

    public List<Mention> getMentions() {
        return mentions;
    }

    public void setMentions(List<Mention> mentions) {
        this.mentions = mentions;
    }

    public void setMessage(String message) {
        if (message != null && message.length() > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException("The server won't accept messages longer than " + MAX_MESSAGE_LENGTH + " characters!");
        }
        this.message = message;
    }

    public void addMention(Mention mention) {
        mentions.add(mention);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeckComment that = (DeckComment) o;

        if (!Objects.equals(actorId, that.actorId)) return false;
        if (!Objects.equals(actorDisplayName, that.actorDisplayName))
            return false;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        int result = actorId != null ? actorId.hashCode() : 0;
        result = 31 * result + (actorDisplayName != null ? actorDisplayName.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }
}