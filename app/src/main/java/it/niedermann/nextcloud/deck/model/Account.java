package it.niedermann.nextcloud.deck.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;

@Entity
public class Account implements Serializable {
    @Transient
    private static final long serialVersionUID = 0;

    @Id(autoincrement = true)
    private Long id;

    @NotNull
    @Unique
    private String name;

    @Generated(hash = 951981252)
    public Account(Long id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }

    @Generated(hash = 882125521)
    public Account() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
