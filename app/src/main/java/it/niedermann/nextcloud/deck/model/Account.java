package it.niedermann.nextcloud.deck.model;

import android.net.Uri;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.nextcloud.android.sso.model.SingleSignOnAccount;

import java.io.Serializable;
import java.util.Objects;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.model.ocs.Version;
import it.niedermann.nextcloud.deck.ui.accountswitcher.AccountSwitcherDialog;

@Entity(indices = {@Index(value = "name", unique = true)})
public class Account implements Serializable {
    @Ignore
    private static final long serialVersionUID = 0;

    @PrimaryKey(autoGenerate = true)
    protected Long id;

    @NonNull
    private String name;

    @NonNull
    private String userName;

    @Ignore
    @Nullable
    private String userDisplayName;

    @NonNull
    private String url;

    /**
     * See {@link Capabilities#DEFAULT_COLOR}
     */
    @NonNull
    @ColumnInfo(defaultValue = "0")
    private Integer color = Capabilities.DEFAULT_COLOR;

    @NonNull
    @ColumnInfo(defaultValue = "0")
    private Integer textColor = Capabilities.DEFAULT_TEXT_COLOR;

    @NonNull
    @ColumnInfo(defaultValue = "0.6.4")
    private String serverDeckVersion = "0.6.4";

    @NonNull
    @ColumnInfo(defaultValue = "0")
    private boolean maintenanceEnabled = false;

    private String etag;
    private String boardsEtag;

    @Ignore
    public Account(Long id, @NonNull String name, @NonNull String userName, @NonNull String url) {
        this(name, userName, url);
        this.id = id;
    }

    @Ignore
    public Account(@NonNull String name, @NonNull String userName, @NonNull String url) {
        this.name = name;
        this.userName = userName;
        this.url = url;
    }

    @Ignore
    public Account(Long id) {
        this.id = id;
    }

    public Account() {
    }

    public void applyCapabilities(Capabilities capabilities, String eTag) {
        if (capabilities == null) {
            maintenanceEnabled = true;
            return;
        }
        maintenanceEnabled = capabilities.isMaintenanceEnabled();
        if (!isMaintenanceEnabled()) {
            try {
                // Nextcloud might return color format #000 which cannot be parsed by Color.parseColor()
                // https://github.com/stefan-niedermann/nextcloud-deck/issues/466
                color = capabilities.getColor();
                textColor = capabilities.getTextColor();
            } catch (Exception e) {
                DeckLog.logError(e);
                color = Capabilities.DEFAULT_COLOR;
                textColor = Capabilities.DEFAULT_TEXT_COLOR;
            }
            if (capabilities.getDeckVersion() != null) {
                serverDeckVersion = capabilities.getDeckVersion().getOriginalVersion();
            }
            if (eTag != null) {
                this.etag = eTag;
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
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

    public static long getSerialVersionUID() {
        return serialVersionUID;
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

    public Version getServerDeckVersionAsObject() {
        return Version.of(serverDeckVersion);
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
    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(@Nullable String userDisplayName) {
        this.userDisplayName = userDisplayName;
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

    /**
     * A cache buster parameter is added for duplicate account names on different hosts which shall be fetched from the same {@link SingleSignOnAccount} (e. g. {@link AccountSwitcherDialog})
     *
     * @return an {@link String} to fetch the avatar for this account.
     */
    public String getAvatarUrl(@Px int size) {
        return getUrl() + "/index.php/avatar/" + Uri.encode(getUserName()) + "/" + size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return maintenanceEnabled == account.maintenanceEnabled &&
                Objects.equals(id, account.id) &&
                name.equals(account.name) &&
                userName.equals(account.userName) &&
                Objects.equals(userDisplayName, account.userDisplayName) &&
                url.equals(account.url) &&
                color.equals(account.color) &&
                textColor.equals(account.textColor) &&
                serverDeckVersion.equals(account.serverDeckVersion) &&
                Objects.equals(etag, account.etag) &&
                Objects.equals(boardsEtag, account.boardsEtag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, userName, userDisplayName, url, color, textColor, serverDeckVersion, maintenanceEnabled, etag, boardsEtag);
    }

    @NonNull
    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", name='" + name + '\'' +
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
