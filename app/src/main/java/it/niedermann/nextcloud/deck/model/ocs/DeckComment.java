package it.niedermann.nextcloud.deck.model.ocs;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.interfaces.AbstractRemoteEntity;
@Entity(inheritSuperIndices = true,
        indices = {
                @Index(value = "accountId", name = "comment_accID"),
                @Index("objectId")
        },
        foreignKeys = {
                @ForeignKey(
                        entity = Card.class,
                        parentColumns = "localId",
                        childColumns = "objectId", onDelete = ForeignKey.CASCADE
                )
        }
)
public class DeckComment extends AbstractRemoteEntity {
    private static final transient int MAX_MESSAGE_LENGTH = 1000;

    private Long objectId;
    private String actorType;
    private Date creationDateTime;
    private String actorId;
    private String actorDisplayName;
    private String message;
    private List<Mention> mentions;

    public  DeckComment() {
    }

    public DeckComment(long cardId, String actorId, String actorDisplayName, String message) {
        this.objectId = cardId;
        this.actorId = actorId;
        this.actorDisplayName = actorDisplayName;
        setMessage(message);
    }

    public String getActorType() {
        return actorType;
    }

    public Date getCreationDateTime() {
        return creationDateTime;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        if (message!= null && message.length() > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException("The server won't accept messages longer than "+MAX_MESSAGE_LENGTH+" characters!");
        }
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeckComment that = (DeckComment) o;

        if (actorId != null ? !actorId.equals(that.actorId) : that.actorId != null) return false;
        if (actorDisplayName != null ? !actorDisplayName.equals(that.actorDisplayName) : that.actorDisplayName != null)
            return false;
        return message != null ? message.equals(that.message) : that.message == null;
    }

    @Override
    public int hashCode() {
        int result = actorId != null ? actorId.hashCode() : 0;
        result = 31 * result + (actorDisplayName != null ? actorDisplayName.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }
}