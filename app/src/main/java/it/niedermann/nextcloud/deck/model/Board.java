package it.niedermann.nextcloud.deck.model;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
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
import it.niedermann.nextcloud.deck.model.join.board.JoinBoardWithLabel;
import it.niedermann.nextcloud.deck.model.join.board.JoinBoardWithPermission;
import it.niedermann.nextcloud.deck.model.join.board.JoinBoardWithUser;

@Entity
public class Board implements RemoteEntity {
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
    @ToOne(joinProperty = "localId")
    private User owner;
    private String color;
    private boolean archived;
    @ToMany
    @JoinEntity(entity = JoinBoardWithLabel.class, sourceProperty = "boardId", targetProperty = "labelId")
    private List<Label> labels = new ArrayList<>();
    private String acl;
    @ToMany
    @JoinEntity(entity = JoinBoardWithPermission.class, sourceProperty = "boardId", targetProperty = "permissionId")
    private List<Permission> permissions = new ArrayList<>();
    @ToMany
    @JoinEntity(entity = JoinBoardWithUser.class, sourceProperty = "boardId", targetProperty = "userId")
    private List<User> users = new ArrayList<>();
    private int shared;
    private Date deletedAt;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 754839907)
    private transient BoardDao myDao;

    @Generated(hash = 389383261)
    public Board(Long localId, Long id, int status, String title, String color, boolean archived, String acl, int shared,
            Date deletedAt) {
        this.localId = localId;
        this.id = id;
        this.status = status;
        this.title = title;
        this.color = color;
        this.archived = archived;
        this.acl = acl;
        this.shared = shared;
        this.deletedAt = deletedAt;
    }

    @Generated(hash = 1406520307)
    public Board() {
    }

    @Generated(hash = 1501133588)
    private transient Long account__resolvedKey;

    @Generated(hash = 1847295403)
    private transient Long owner__resolvedKey;

    public void setLocalId(Long localId) {
        this.localId = localId;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public String getAcl() {
        return acl;
    }

    public void setAcl(String acl) {
        this.acl = acl;
    }

    public int getShared() {
        return shared;
    }

    public void setShared(int shared) {
        this.shared = shared;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DBStatus getStatusEnum() {
        return DBStatus.findById(status);
    }

    public void setStatusEnum(DBStatus status) {
        this.status = status.getId();
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

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 2117670560)
    public User getOwner() {
        Long __key = this.localId;
        if (owner__resolvedKey == null || !owner__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            User ownerNew = targetDao.load(__key);
            synchronized (this) {
                owner = ownerNew;
                owner__resolvedKey = __key;
            }
        }
        return owner;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1357246117)
    public void setOwner(User owner) {
        synchronized (this) {
            this.owner = owner;
            localId = owner == null ? null : owner.getId();
            owner__resolvedKey = localId;
        }
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 39169395)
    public List<Label> getLabels() {
        if (labels == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            LabelDao targetDao = daoSession.getLabelDao();
            List<Label> labelsNew = targetDao._queryBoard_Labels(localId);
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
    @Generated(hash = 64193987)
    public List<Permission> getPermissions() {
        if (permissions == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PermissionDao targetDao = daoSession.getPermissionDao();
            List<Permission> permissionsNew = targetDao._queryBoard_Permissions(localId);
            synchronized (this) {
                if (permissions == null) {
                    permissions = permissionsNew;
                }
            }
        }
        return permissions;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 853623651)
    public synchronized void resetPermissions() {
        permissions = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1080970764)
    public List<User> getUsers() {
        if (users == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            UserDao targetDao = daoSession.getUserDao();
            List<User> usersNew = targetDao._queryBoard_Users(localId);
            synchronized (this) {
                if (users == null) {
                    users = usersNew;
                }
            }
        }
        return users;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1027274768)
    public synchronized void resetUsers() {
        users = null;
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
    @Generated(hash = 525723581)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getBoardDao() : null;
    }
}
