package it.niedermann.nextcloud.deck.api;


import io.reactivex.Observable;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import retrofit2.http.GET;

public interface NextcloudServerAPI {

    @GET("cloud/capabilities?format=json")
    Observable<Capabilities> getCapabilities();

    //@GET("apps/activity/api/v2/activity/filter?format=json&object_type=deck_card&object_id={cardId}&limit=50&since=-1&sort=asc")
}
