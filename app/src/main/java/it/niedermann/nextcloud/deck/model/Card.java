package it.niedermann.nextcloud.deck.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import java.util.Date;

import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.interfaces.RemoteEntity;

@Entity(inheritSuperIndices = true, indices = {@Index(value = "accountId", name = "card_acc")})
public class Card extends RemoteEntity {


    private String title;
    private String description;
    @NonNull
//    @Index
    private long stackId;
    //    @ToOne(joinProperty = "stackId")
//    protected Stack stack;
    private String type;
    private Date createdAt;
    private Date deletedAt;
    //    @ToMany
//    @JoinEntity(entity = JoinCardWithLabel.class, sourceProperty = "cardId", targetProperty = "labelId")
//    private List<Label> labels = new ArrayList<>();
//    @ToMany
//    @JoinEntity(entity = JoinCardWithUser.class, sourceProperty = "cardId", targetProperty = "userId")
    private String attachments;
    private int attachmentCount;

    private Long userId;
    //    @ToOne(joinProperty = "userId")
//    private User owner;
//    @Index
    @NonNull
    private int order;
    //    @Index
    private boolean archived;
    private Date dueDate;
    private boolean notified;
    private int overdue;
    private int commentsUnread;



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
    }

    public long getStackId() {
        return stackId;
    }

    public void setStackId(long stackId) {
        this.stackId = stackId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getAttachments() {
        return attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
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

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
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

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getOrder() {
        return this.order;
    }
}
