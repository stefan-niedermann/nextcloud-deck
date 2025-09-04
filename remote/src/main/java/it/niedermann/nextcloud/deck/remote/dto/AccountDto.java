package it.niedermann.nextcloud.deck.remote.dto;

import com.google.gson.annotations.SerializedName;

import it.niedermann.nextcloud.deck.shared.model.Account;

public class AccountDto extends Account {

    transient protected Long id;

    @SerializedName("id")
    protected Long remoteId;
}
