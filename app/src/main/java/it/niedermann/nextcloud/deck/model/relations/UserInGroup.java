package it.niedermann.nextcloud.deck.model.relations;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import it.niedermann.nextcloud.deck.model.User;

@Entity(
        primaryKeys = {"groupId", "memberId"},
        indices = {@Index("groupId"), @Index("memberId"), @Index(name = "unique_idx_group_member", value = {"groupId","memberId"}, unique = true)},
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "localId",
                        childColumns = "groupId", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = User.class,
                        parentColumns = "localId",
                        childColumns = "memberId", onDelete = ForeignKey.CASCADE)
        })
public class UserInGroup {
    @NonNull
    private Long groupId;
    @NonNull
    private Long memberId;

    @NonNull
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(@NonNull Long groupId) {
        this.groupId = groupId;
    }

    @NonNull
    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(@NonNull Long memberId) {
        this.memberId = memberId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserInGroup that = (UserInGroup) o;

        if (!groupId.equals(that.groupId)) return false;
        return memberId.equals(that.memberId);
    }

    @Override
    public int hashCode() {
        int result = groupId.hashCode();
        result = 31 * result + memberId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "UserInGroup{" +
                "groupId=" + groupId +
                ", memberId=" + memberId +
                '}';
    }
}
