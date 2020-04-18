package it.niedermann.nextcloud.deck.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.model.ocs.Version;

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

    @NonNull
    private String url;

    @NonNull
    @ColumnInfo(defaultValue = "#0082c9")
    private String color = "#0082c9";

    @NonNull
    @ColumnInfo(defaultValue = "#ffffff")
    private String textColor = "#ffffff";

    @NonNull
    @ColumnInfo(defaultValue = "0.6.4")
    private String serverDeckVersion = "0.6.4";

    @NonNull
    @ColumnInfo(defaultValue = "0")
    private boolean maintenanceEnabled = false;

    @Ignore
    public Account(Long id, @NonNull String name, @NonNull String userName, @NonNull String url) {
        this(name, userName, url);
        this.id = id;
    }

    @Ignore
    public Account(String name, String userName, String url) {
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

    public void applyCapabilities(Capabilities capabilities) {
        maintenanceEnabled = capabilities.isMaintenanceEnabled();
        if (!isMaintenanceEnabled()) {
            color = capabilities.getColor();
            textColor = capabilities.getTextColor();
            if (capabilities.getDeckVersion()!=null) {
                serverDeckVersion = capabilities.getDeckVersion().getOriginalVersion();
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

    @NonNull
    public String getColor() {
        return color;
    }

    public void setColor(@NonNull String color) {
        this.color = color;
    }

    @NonNull
    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(@NonNull String textColor) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        if (maintenanceEnabled != account.maintenanceEnabled) return false;
        if (id != null ? !id.equals(account.id) : account.id != null) return false;
        if (!name.equals(account.name)) return false;
        if (!userName.equals(account.userName)) return false;
        if (!url.equals(account.url)) return false;
        if (!color.equals(account.color)) return false;
        if (!textColor.equals(account.textColor)) return false;
        return serverDeckVersion.equals(account.serverDeckVersion);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + name.hashCode();
        result = 31 * result + userName.hashCode();
        result = 31 * result + url.hashCode();
        result = 31 * result + color.hashCode();
        result = 31 * result + textColor.hashCode();
        result = 31 * result + serverDeckVersion.hashCode();
        result = 31 * result + (maintenanceEnabled ? 1 : 0);
        return result;
    }

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
                '}';
    }
}
