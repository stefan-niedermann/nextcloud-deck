package it.niedermann.nextcloud.deck.repository;

import static io.reactivex.rxjava3.core.Flowable.just;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import it.niedermann.nextcloud.deck.shared.model.Account;

public class AccountRepository extends AbstractRepository {

    private final BehaviorSubject<Boolean> hasAccountsMock = BehaviorSubject.createDefault(false);

    public AccountRepository(@NonNull Context context) {
        super(context);
    }

    public Flowable<ImportState> importAccount(@NonNull String accountName,
                                               @NonNull String url,
                                               @NonNull String userName,
                                               @NonNull String token) {

        // TODO Implement account import
        //  1. Check whether accountName already exists.
        //  2. Use repository-sync to fetch everything related to this account
        //  3. In case of an error rollback by deleting the local account and cascading this deletion.

        // Mock implementation
        return Observable.just(
                        new ImportState(accountName, url, userName, 0, 0, 0),
                        new ImportState(accountName, url, userName, 3, 0, 1),
                        new ImportState(accountName, url, userName, 3, 0, 2),
                        new ImportState(accountName, url, userName, 3, 1, 1),
                        new ImportState(accountName, url, userName, 3, 1, 2),
                        new ImportState(accountName, url, userName, 3, 2, 1),
                        new ImportState(accountName, url, userName, 3, 3, 0))
                .zipWith(Observable.interval(500, TimeUnit.MILLISECONDS), (state, timer) -> state)
                .doOnNext(state -> {
                    Log.v("TAG", "Next " + state.toString());

                    if (state.boardsDone() == 3) {
                        hasAccountsMock.onNext(true);
                    }
                })
                .toFlowable(BackpressureStrategy.BUFFER);
    }

    public Flowable<Boolean> hasAccounts() {
        // TODO Replace mock with actual database query
        return hasAccountsMock.share().toFlowable(BackpressureStrategy.LATEST);
//        return dataBaseAdapter.hasAnyAccounts();
    }

    public Flowable<Collection<Account>> getAccounts() {
        return just(Collections.emptySet());
    }

    public record ImportState(
            @NonNull String accountName,
            @NonNull String url,
            @NonNull String userName,
            int boardsTotal,
            int boardsDone,
            int boardsWip
    ) implements Serializable {
    }
}
