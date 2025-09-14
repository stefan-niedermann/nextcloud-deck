package it.niedermann.nextcloud.deck.database.entity.ocs.comment;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(inheritSuperIndices = true,
        indices = {
                @Index("commentId")
        },
        foreignKeys = {
                @ForeignKey(
                        entity = DeckComment.class,
                        parentColumns = "localId",
                        childColumns = "commentId", onDelete = ForeignKey.CASCADE
                )
        }
)
public class Mention {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    private Long commentId;
    private String mentionId;
    private String mentionType;
    private String mentionDisplayName;

    public Mention() {
    }

    @Ignore
    public Mention(Long commentId, String mentionId, String mentionType, String mentionDisplayName) {
        this.commentId = commentId;
        this.mentionId = mentionId;
        this.mentionType = mentionType;
        this.mentionDisplayName = mentionDisplayName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getMentionId() {
        return mentionId;
    }

    public void setMentionId(String mentionId) {
        this.mentionId = mentionId;
    }

    public String getMentionType() {
        return mentionType;
    }

    public void setMentionType(String mentionType) {
        this.mentionType = mentionType;
    }

    public String getMentionDisplayName() {
        return mentionDisplayName;
    }

    public void setMentionDisplayName(String mentionDisplayName) {
        this.mentionDisplayName = mentionDisplayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mention mention = (Mention) o;

        if (!Objects.equals(id, mention.id)) return false;
        if (!Objects.equals(commentId, mention.commentId))
            return false;
        if (!Objects.equals(mentionId, mention.mentionId))
            return false;
        if (!Objects.equals(mentionType, mention.mentionType))
            return false;
        return Objects.equals(mentionDisplayName, mention.mentionDisplayName);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (commentId != null ? commentId.hashCode() : 0);
        result = 31 * result + (mentionId != null ? mentionId.hashCode() : 0);
        result = 31 * result + (mentionType != null ? mentionType.hashCode() : 0);
        result = 31 * result + (mentionDisplayName != null ? mentionDisplayName.hashCode() : 0);
        return result;
    }
}
