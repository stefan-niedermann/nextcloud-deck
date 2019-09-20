package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.ocs.Activity;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;

public class ActivityDataProvider extends AbstractSyncDataProvider<Activity> {

    protected Card card;

    public ActivityDataProvider(AbstractSyncDataProvider<?> parent, Card card) {
        super(parent);
        this.card = card;
    }

    @Override
    public void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<Activity>> responder, Date lastSync) {
        serverAdapter.getActivitiesForCard(card.getId(), responder);
    }

    @Override
    public Activity getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Activity entity) {
        //TODO
        return null;
    }

    @Override
    public long createInDB(DataBaseAdapter dataBaseAdapter, long accountId, Activity b) {
        //TODO
        return 0;
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, Activity activity, boolean setStatus) {
        //TODO
    }

    @Override
    public void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, Activity activity) {
        //TODO
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
    public List<Activity> getAllChangedFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Date lastSync) {
        return new ArrayList<>();
    }
}
