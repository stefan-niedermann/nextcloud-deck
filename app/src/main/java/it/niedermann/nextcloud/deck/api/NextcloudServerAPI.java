package it.niedermann.nextcloud.deck.api;


import io.reactivex.Observable;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import retrofit2.http.GET;

public interface NextcloudServerAPI {

    @GET("cloud/capabilities?format=json")
    Observable<Capabilities> getCapabilities();
}
