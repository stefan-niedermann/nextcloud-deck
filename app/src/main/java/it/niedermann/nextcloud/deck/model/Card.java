package it.niedermann.nextcloud.deck.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.interfaces.RemoteEntity;
import it.niedermann.nextcloud.deck.model.join.card.JoinCardWithLabel;
import it.niedermann.nextcloud.deck.model.join.card.JoinCardWithUser;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@Entity
public class Card implements RemoteEntity {
    @Id(autoincrement = true)
    protected Long localId;

    @NotNull
    @Index
    @ToOne(joinProperty = "id")
    protected Account account;

    protected Long id;

    @NotNull
    @Index
    protected int status = DBStatus.UP_TO_DATE.getId();

    private String title;
    private String description;
    private long stackId;
    private String type;
    private Date lastModified;
    private Date createdAt;
    private Date deletedAt;
    @ToMany
    @JoinEntity(entity = JoinCardWithLabel.class, sourceProperty = "cardId", targetProperty = "labelId")
    private List<Label> labels = new ArrayList<>();
    @ToMany
    @JoinEntity(entity = JoinCardWithUser.class, sourceProperty = "cardId", targetProperty = "userId")
    private List<User> assignedUsers = new ArrayList<>();
    private String attachments;
    private int attachmentCount;
    private String owner;
    private int order;
    private boolean archived;
    private String dueDate;
    private int overdue;
    private int commentsUnread;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 599084715)
    private transient CardDao myDao;

    @Generated(hash = 2050594904)
    public Card(Long localId, Long id, int status, String title, String description, long stackId,
            String type, Date lastModified, Date createdAt, Date deletedAt, String attachments,
            int attachmentCount, String owner, int order, boolean archived, String dueDate, int overdue,
            int commentsUnread) {
        this.localId = localId;
        this.id = id;
        this.status = status;
        this.title = title;
        this.description = description;
        this.stackId = stackId;
        this.type = type;
        this.lastModified = lastModified;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.attachments = attachments;
        this.attachmentCount = attachmentCount;
        this.owner = owner;
        this.order = order;
        this.archived = archived;
        this.dueDate = dueDate;
        this.overdue = overdue;
        this.commentsUnread = commentsUnread;
    }

    @Generated(hash = 52700939)
    public Card() {
    }

    @Generated(hash = 1501133588)
    private transient Long account__resolvedKey;

    public Long getLocalId() {
        return localId;
    }

    public void setLocalId(Long localId) {
        this.localId = localId;
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

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
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

    public void addLabel(Label label){
        this.labels.add(label);
    }

    public void addAssignedUser(User user) {
        this.assignedUsers.add(user);
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getOrder() {
        return order;
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

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
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

    public boolean getArchived() {
        return this.archived;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 2035152785)
    public Account getAccount() {
        Long __key = this.id;
        if (account__resolvedKey == null || !account__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AccountDao targetDao = daoSession.getAccountDao();
            Account accountNew = targetDao.load(__key);
            synchronized (this) {
                account = accountNew;
                account__resolvedKey = __key;
            }
        }
        return account;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 19318331)
    public void setAccount(Account account) {
        synchronized (this) {
            this.account = account;
            id = account == null ? null : account.getId();
            account__resolvedKey = id;
        }
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 2060968075)
    public List<Label> getLabels() {
        if (labels == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            LabelDao targetDao = daoSession.getLabelDao();
            List<Label> labelsNew = targetDao._queryCard_Labels(localId);
            synchronized (this) {
                if (labels == null) {
                    labels = labelsNew;
                }
            }
        }
        return labels;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 902294403)
    public synchronized void resetLabels() {
        labels = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 813202807)
    public List<User> getAssignedUsers() {
        if (assignedUsers == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            List<User> assignedUsersNew = targetDao._queryCard_AssignedUsers(localId);
            synchronized (this) {
                if (assignedUsers == null) {
                    assignedUsers = assignedUsersNew;
                }
            }
        }
        return assignedUsers;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 743509305)
    public synchronized void resetAssignedUsers() {
        assignedUsers = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    public int getStatus() {
        return this.status;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1693529984)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCardDao() : null;
    }
}
