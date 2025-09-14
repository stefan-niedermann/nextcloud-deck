package it.niedermann.nextcloud.deck.remote.ocs;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.deck.remote.ocs.dto.CapabilitiesDto;
import it.niedermann.nextcloud.deck.remote.ocs.dto.OcsResponse;
import it.niedermann.nextcloud.deck.shared.model.User;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * @link <a href="https://www.open-collaboration-services.org/">OCS REST API</a>
 */
public interface OcsAPI {

    @GET("cloud/capabilities?format=json")
    Call<OcsResponse<CapabilitiesDto>> getCapabilities(@Header("If-None-Match") @Nullable String eTag);

    @GET("cloud/users/{userId}?format=json")
    Call<OcsResponse<User>> getUser(@Header("If-None-Match") @Nullable String eTag,
                                    @Path("userId") @NonNull String userId);
}
