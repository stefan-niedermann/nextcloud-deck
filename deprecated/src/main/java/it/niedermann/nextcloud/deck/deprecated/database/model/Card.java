package it.niedermann.nextcloud.deck.database.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import com.google.gson.annotations.SerializedName;

import java.time.Instant;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.niedermann.nextcloud.deck.database.entity.enums.DBStatus;
import it.niedermann.nextcloud.deck.database.entity.interfaces.AbstractRemoteEntity;

@Entity(inheritSuperIndices = true,
        indices = {
                @Index(value = "accountId", name = "card_accID"),
                @Index("stackId")
        },
        foreignKeys = {
                @ForeignKey(
                        entity = Stack.class,
                        parentColumns = "localId",
                        childColumns = "stackId", onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Account.class,
                        parentColumns = "id",
                        childColumns = "accountId", onDelete = ForeignKey.CASCADE
                )
        }
)
public class Card extends AbstractRemoteEntity {

    private static Pattern PATTERN_MD_TASK = Pattern.compile("\\[([xX ])]");

    public static class TaskStatus {
        public int taskCount;
        public int doneCount;

        public TaskStatus(int taskCount, int doneCount) {
            this.taskCount = taskCount;
            this.doneCount = doneCount;
        }
    }

    @Ignore
    private TaskStatus taskStatus = null;

    private String title;
    private String description;
    @NonNull
    private Long stackId;
    private String type;
    private Instant createdAt;
    private Instant deletedAt;
    private Instant done;
    private int attachmentCount;

    private Long userId;
    private int order;
    private boolean archived;
    @SerializedName("duedate")
    private Instant dueDate;
    private boolean notified;
    private int overdue;
    private int commentsUnread;

    public Card() {
    }

    @Ignore
    public Card(String title, String description, long stackId) {
        this.title = title;
        this.description = description;
        this.stackId = stackId;
    }

    public Card(Card card) {
        super(card);
        this.title = card.getTitle();
        this.description = card.getDescription();
        this.stackId = card.getStackId();
        this.type = card.getType();
        this.createdAt = card.getCreatedAt();
        this.deletedAt = card.getDeletedAt();
        this.attachmentCount = card.getAttachmentCount();
        this.userId = card.getUserId();
        this.order = card.getOrder();
        this.archived = card.isArchived();
        this.dueDate = card.getDueDate();
        this.done = card.getDone();
        this.notified = card.isNotified();
        this.overdue = card.getOverdue();
        this.commentsUnread = card.getCommentsUnread();
    }

    @NonNull
    public TaskStatus getTaskStatus() {
        if (taskStatus == null) {
            int count = 0, done = 0;
            if (description != null) {
                final Matcher matcher = PATTERN_MD_TASK.matcher(description);
                while (matcher.find()) {
                    count++;
                    char c = matcher.group().charAt(1);
                    if (c == 'x' || c == 'X') {
                        done++;
                    }
                }
            }
            taskStatus = new TaskStatus(count, done);
        }
        return taskStatus;
    }

    public boolean isNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DBStatus getStatusEnum() {
        return DBStatus.findById(status);
    }

    public void setStatusEnum(DBStatus status) {
        this.status = status.getId();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.taskStatus = null;
    }

    public Long getStackId() {
        return stackId;
    }

    public void setStackId(Long stackId) {
        this.stackId = stackId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public int getAttachmentCount() {
        return attachmentCount;
    }

    public void setAttachmentCount(int attachmentCount) {
        this.attachmentCount = attachmentCount;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public Instant getDueDate() {
        return dueDate;
    }

    public void setDueDate(Instant dateTime) {
        this.dueDate = dateTime;
    }

    public int getOverdue() {
        return overdue;
    }

    public void setOverdue(int overdue) {
        this.overdue = overdue;
    }

    public int getCommentsUnread() {
        return commentsUnread;
    }

    public void setCommentsUnread(int commentsUnread) {
        this.commentsUnread = commentsUnread;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getOrder() {
        return this.order;
    }

    public Instant getDone() {
        return done;
    }

    public void setDone(Instant done) {
        this.done = done;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Card card = (Card) o;

        if (stackId != card.stackId) return false;
        if (attachmentCount != card.attachmentCount) return false;
        if (order != card.order) return false;
        if (archived != card.archived) return false;
        if (notified != card.notified) return false;
        if (overdue != card.overdue) return false;
        if (commentsUnread != card.commentsUnread) return false;
        if (!Objects.equals(title, card.title)) return false;
        if (!Objects.equals(description, card.description))
            return false;
        if (!Objects.equals(type, card.type)) return false;
        if (!Objects.equals(createdAt, card.createdAt))
            return false;
        if (!Objects.equals(deletedAt, card.deletedAt))
            return false;
        if (!Objects.equals(done, card.done))
            return false;
        if (!Objects.equals(userId, card.userId)) return false;
        return Objects.equals(dueDate, card.dueDate);
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (int) (stackId ^ (stackId >>> 32));
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (deletedAt != null ? deletedAt.hashCode() : 0);
        result = 31 * result + (done != null ? done.hashCode() : 0);
        result = 31 * result + attachmentCount;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + order;
        result = 31 * result + (archived ? 1 : 0);
        result = 31 * result + (dueDate != null ? dueDate.hashCode() : 0);
        result = 31 * result + (notified ? 1 : 0);
        result = 31 * result + overdue;
        result = 31 * result + commentsUnread;
        return result;
    }

    @Override
    public String toString() {
        return "Card{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", stackId=" + stackId +
                ", type='" + type + '\'' +
                ", createdAt=" + createdAt +
                ", deletedAt=" + deletedAt +
                ", done=" + done +
                ", attachmentCount=" + attachmentCount +
                ", userId=" + userId +
                ", order=" + order +
                ", archived=" + archived +
                ", dueDate=" + dueDate +
                ", notified=" + notified +
                ", overdue=" + overdue +
                ", commentsUnread=" + commentsUnread +
                ", localId=" + localId +
                ", accountId=" + accountId +
                ", id=" + id +
                ", status=" + status +
                ", lastModified=" + lastModified +
                ", lastModifiedLocal=" + lastModifiedLocal +
                '}';
    }
}
