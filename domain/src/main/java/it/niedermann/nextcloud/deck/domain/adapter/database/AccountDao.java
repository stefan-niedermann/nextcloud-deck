package it.niedermann.nextcloud.deck.domain.adapter.database;

import io.reactivex.rxjava3.core.Flowable;

public interface AccountDao {

    Flowable<Boolean> hasAccounts();
}
