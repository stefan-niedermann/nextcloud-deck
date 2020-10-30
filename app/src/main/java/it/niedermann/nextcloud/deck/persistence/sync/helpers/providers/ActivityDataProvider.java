package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import androidx.annotation.NonNull;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
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
    public void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<Activity>> responder, Instant lastSync) {
        serverAdapter.getActivitiesForCard(card.getId(), responder);
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
    public void createOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<Activity> responder, Activity entity) {
        // nope.
    }

    @Override
    public void updateOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<Activity> callback, Activity entity) {
        // nope.
    }

    @Override
    public void deleteOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<Void> callback, Activity entity, DataBaseAdapter dataBaseAdapter) {
        // nope.
    }

    @Override
    public List<Activity> getAllChangedFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Instant lastSync) {
        return Collections.emptyList();
    }
}
