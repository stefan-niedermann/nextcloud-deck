package it.niedermann.nextcloud.deck.shared.model;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;

public abstract class Account implements Serializable {

    protected Long id;
    protected String accountName;
    protected String token;
    protected String userName;
    protected String displayName;
    protected String url;
    protected Long currentBoardId;
    protected Integer color;
    protected Integer textColor;
    protected String serverDeckVersion;
    protected boolean maintenanceEnabled;
    protected String etag;
    protected String boardsEtag;

    public Account(Long id, @NonNull String accountName, @NonNull String userName, @NonNull String url) {
        this(accountName, userName, url);
        this.id = id;
    }

    public Account(@NonNull String accountName, @NonNull String userName, @NonNull String url) {
        this.accountName = accountName;
        this.userName = userName;
        this.url = url;
    }

    public Account(Long id) {
        this.id = id;
    }

    public Account() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NonNull
    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(@NonNull String accountName) {
        this.accountName = accountName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @NonNull
    public String getUserName() {
        return userName;
    }

    public void setUserName(@NonNull String userName) {
        this.userName = userName;
    }

    @NonNull
    public String getUrl() {
        return url;
    }

    public void setUrl(@NonNull String url) {
        this.url = url;
    }

    @ColorInt
    @NonNull
    public Integer getColor() {
        return color;
    }

    public void setColor(@NonNull Integer color) {
        this.color = color;
    }

    @NonNull
    public Integer getTextColor() {
        return textColor;
    }

    @Deprecated
    public void setTextColor(@NonNull Integer textColor) {
        this.textColor = textColor;
    }

    @NonNull
    public String getServerDeckVersion() {
        return serverDeckVersion;
    }

    public void setServerDeckVersion(@NonNull String serverDeckVersion) {
        this.serverDeckVersion = serverDeckVersion;
    }

    public boolean isMaintenanceEnabled() {
        return maintenanceEnabled;
    }

    public void setMaintenanceEnabled(boolean maintenanceEnabled) {
        this.maintenanceEnabled = maintenanceEnabled;
    }

    @Nullable
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(@Nullable String displayName) {
        this.displayName = displayName;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getBoardsEtag() {
        return boardsEtag;
    }

    public void setBoardsEtag(String boardsEtag) {
        this.boardsEtag = boardsEtag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return maintenanceEnabled == account.maintenanceEnabled &&
               Objects.equals(id, account.id) &&
               accountName.equals(account.accountName) &&
               userName.equals(account.userName) &&
               Objects.equals(displayName, account.displayName) &&
               url.equals(account.url) &&
               color.equals(account.color) &&
               textColor.equals(account.textColor) &&
               serverDeckVersion.equals(account.serverDeckVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accountName, userName, displayName, url, color, textColor, serverDeckVersion, maintenanceEnabled, etag, boardsEtag);
    }

    @NonNull
    @Override
    public String toString() {
        return "Account{" +
               "id=" + id +
               ", name='" + accountName + '\'' +
               ", userName='" + userName + '\'' +
               ", url='" + url + '\'' +
               ", color='" + color + '\'' +
               ", textColor='" + textColor + '\'' +
               ", serverDeckVersion='" + serverDeckVersion + '\'' +
               ", maintenanceEnabled=" + maintenanceEnabled +
               ", eTag='" + etag + '\'' +
               '}';
    }
}
