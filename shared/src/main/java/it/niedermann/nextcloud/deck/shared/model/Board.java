package it.niedermann.nextcloud.deck.shared.model;

import java.time.Instant;


public class Board {

    protected long id;
    protected long remoteId;
    protected long accountId;
    protected String title;
    protected long ownerId;
    protected Integer color;
    protected boolean archived;
    protected int shared;
    protected Instant deletedAt;
    protected boolean permissionRead = false;
    protected boolean permissionEdit = false;
    protected boolean permissionManage = false;
    protected boolean permissionShare = false;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(long remoteId) {
        this.remoteId = remoteId;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public int getShared() {
        return shared;
    }

    public void setShared(int shared) {
        this.shared = shared;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

    public boolean isPermissionRead() {
        return permissionRead;
    }

    public void setPermissionRead(boolean permissionRead) {
        this.permissionRead = permissionRead;
    }

    public boolean isPermissionEdit() {
        return permissionEdit;
    }

    public void setPermissionEdit(boolean permissionEdit) {
        this.permissionEdit = permissionEdit;
    }

    public boolean isPermissionManage() {
        return permissionManage;
    }

    public void setPermissionManage(boolean permissionManage) {
        this.permissionManage = permissionManage;
    }

    public boolean isPermissionShare() {
        return permissionShare;
    }

    public void setPermissionShare(boolean permissionShare) {
        this.permissionShare = permissionShare;
    }
}
