package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import androidx.annotation.NonNull;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import it.niedermann.nextcloud.deck.api.ResponseCallback;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.ocs.Activity;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;

public class ActivityDataProvider extends AbstractSyncDataProvider<Activity> {

    @NonNull
    private final Card card;

    public ActivityDataProvider(AbstractSyncDataProvider<?> parent, @NonNull Card card) {
        super(parent);
        this.card = card;
    }

    @Override
    public Disposable getAllFromServer(ServerAdapter serverAdapter, long accountId, ResponseCallback<List<Activity>> responder, Instant lastSync) {
        return serverAdapter.getActivitiesForCard(card.getId(), responder);
    }

    @Override
    public Activity getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Activity entity) {
        return dataBaseAdapter.getActivityByRemoteIdDirectly(accountId, entity.getId());
    }

    @Override
    public long createInDB(DataBaseAdapter dataBaseAdapter, long accountId, Activity activity) {
//        activity.getType() //FIXME: filter out comments!
        activity.setAccountId(accountId);
        activity.setCardId(card.getLocalId());
        return dataBaseAdapter.createActivity(accountId, activity);
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, Activity activity, boolean setStatus) {
        activity.setAccountId(accountId);
        activity.setCardId(card.getLocalId());
        dataBaseAdapter.updateActivity(activity, setStatus);
    }

    @Override
    public void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, Activity activity) {
        dataBaseAdapter.deleteActivity(activity);
    }

    @Override
    public Disposable createOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, ResponseCallback<Activity> responder, Activity entity) {
        return new CompositeDisposable();
    }

    @Override
    public Disposable updateOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, ResponseCallback<Activity> callback, Activity entity) {
        return new CompositeDisposable();
    }

    @Override
    public Disposable deleteOnServer(ServerAdapter serverAdapter, long accountId, ResponseCallback<Void> callback, Activity entity, DataBaseAdapter dataBaseAdapter) {
        return new CompositeDisposable();
    }

    @Override
    public List<Activity> getAllChangedFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Instant lastSync) {
        return Collections.emptyList();
    }
}
