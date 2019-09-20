package it.niedermann.nextcloud.deck.api;


import java.util.List;

import io.reactivex.Observable;
import it.niedermann.nextcloud.deck.model.ocs.Activity;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NextcloudServerAPI {

    @GET("cloud/capabilities?format=json")
    Observable<Capabilities> getCapabilities();

    @GET("apps/activity/api/v2/activity/filter?format=json&object_type=deck_card&limit=50&since=-1&sort=asc")
    Observable<List<Activity>> getActivitiesForCard(@Query("object_id") long cardId);
}
