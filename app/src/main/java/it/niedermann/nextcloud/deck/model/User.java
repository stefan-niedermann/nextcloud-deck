package it.niedermann.nextcloud.deck.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.DaoException;

import java.util.Date;

import it.niedermann.nextcloud.deck.model.interfaces.RemoteEntity;

@Entity
public class User implements RemoteEntity {


    @Id
    private Long localId;

    @NotNull
    private Long id;

    @NotNull
    long accountId;

    @ToOne(joinProperty = "accountId")
    protected Account account;

    private String primaryKey;
    private String uid;
    private String displayname;

    private Date lastModified;
    private Date lastModifiedLocal;

    @Generated(hash = 1501133588)
    private transient Long account__resolvedKey;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1507654846)
    private transient UserDao myDao;

    public User() {
        super();
    }

    public User(String primaryKey, String uid, String displayname) {
        this.primaryKey = primaryKey;
        this.uid = uid;
        this.displayname = displayname;
    }

    @Generated(hash = 1358425037)
    public User(Long localId, @NotNull Long id, long accountId, String primaryKey, String uid,
            String displayname, Date lastModified, Date lastModifiedLocal) {
        this.localId = localId;
        this.id = id;
        this.accountId = accountId;
        this.primaryKey = primaryKey;
        this.uid = uid;
        this.displayname = displayname;
        this.lastModified = lastModified;
        this.lastModifiedLocal = lastModifiedLocal;
    }

    @Override
    public Long getLocalId() {
        return localId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public long getAccountId() {
        return this.accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 2143533054)
    public Account getAccount() {
        long __key = this.accountId;
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

    @Override
    public void setLocalId(Long id) {

    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1716871290)
    public void setAccount(@NotNull Account account) {
        if (account == null) {
            throw new DaoException(
                    "To-one property 'accountId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.account = account;
            accountId = account.getId();
            account__resolvedKey = accountId;
        }
    }

    @Override
    public Date getLastModified() {
        return lastModified;
    }

    @Override
    public Date getLastModifiedLocal() {
        return lastModifiedLocal;
    }

    @Override
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public void setLastModifiedLocal(Date lastModified) {
        this.lastModifiedLocal = lastModified;
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

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2059241980)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserDao() : null;
    }

}
