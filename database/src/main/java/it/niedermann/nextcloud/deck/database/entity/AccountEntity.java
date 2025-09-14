package it.niedermann.nextcloud.deck.database.entity;

import androidx.room.Entity;

import it.niedermann.nextcloud.deck.shared.model.Account;


@Entity(
        tableName = "Account",
        inheritSuperIndices = true,
        primaryKeys = {
                "id"
        },
        indices = {
        },
        foreignKeys = {
        }

)
public class AccountEntity extends Account {

    protected int id;
}
