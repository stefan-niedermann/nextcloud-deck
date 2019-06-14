package it.niedermann.nextcloud.deck.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

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

    @Ignore
    public Account(Long id, @NonNull String name, @NonNull String userName, @NonNull String url) {
        this(name, userName, url);
        this.id = id;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        if (id != null ? !id.equals(account.id) : account.id != null) return false;
        if (!name.equals(account.name)) return false;
        if (!userName.equals(account.userName)) return false;
        return url.equals(account.url);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + name.hashCode();
        result = 31 * result + userName.hashCode();
        result = 31 * result + url.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", userName='" + userName + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
