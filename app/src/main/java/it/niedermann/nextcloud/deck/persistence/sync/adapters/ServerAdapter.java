package it.niedermann.nextcloud.deck.persistence.sync.adapters;

import android.app.Activity;
import android.content.Context;

import java.util.List;

import it.niedermann.nextcloud.deck.api.ApiProvider;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.api.RequestHelper;
import it.niedermann.nextcloud.deck.model.Board;

public class ServerAdapter implements IPersistenceAdapter {

    private Context applicationContext;
    private ApiProvider provider;
    private Activity sourceActivity;

    public ServerAdapter(Context applicationContext, Activity sourceActivity) {
        this.applicationContext = applicationContext;
        this.sourceActivity = sourceActivity;
        provider = new ApiProvider(applicationContext);
    }


    @Override
    public void getBoards(IResponseCallback<List<Board>> responseCallback) {
        RequestHelper.request(sourceActivity, provider, () -> provider.getAPI().boards(), responseCallback);
    }
}
