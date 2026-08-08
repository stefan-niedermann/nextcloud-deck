package it.niedermann.nextcloud.remote.ocs;

import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.remote.ocs.dto.OcsAutocompleteResponseDTO;
import it.niedermann.nextcloud.remote.ocs.dto.OcsCapabilitiesResponseDTO;
import it.niedermann.nextcloud.remote.ocs.dto.OcsSearchProvidersResponseDTO;
import it.niedermann.nextcloud.remote.ocs.dto.OcsSearchResultResponseDTO;
import it.niedermann.nextcloud.remote.ocs.dto.OcsUserResponseDTO;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @link <a href="https://www.open-collaboration-services.org/">OCS REST API</a>
 */
public interface OcsApi {

    String HEADER_OCS_API_REQUEST = "OCS-APIRequest: true";

    @Headers({HEADER_OCS_API_REQUEST})
    @GET("ocs/v1.php/cloud/capabilities?format=json")
    CompletableFuture<OcsCapabilitiesResponseDTO> getCapabilities(@Header("If-None-Match") @Nullable String eTag);

    @Headers({HEADER_OCS_API_REQUEST})
    @GET("ocs/v1.php/cloud/users/{userId}?format=json")
    CompletableFuture<OcsUserResponseDTO> getUser(@Header("If-None-Match") @Nullable String eTag,
                                                   @Path("userId") String userId);

    @Headers({HEADER_OCS_API_REQUEST})
    @GET("index.php/avatar/{userId}/{size}")
    CompletableFuture<Response<ResponseBody>> getAvatar(@Path("userId") String userId, @Path("size") int size);

    @Headers({HEADER_OCS_API_REQUEST})
    @GET("ocs/v1.php/core/autocomplete/get?format=json")
    CompletableFuture<OcsAutocompleteResponseDTO> searchUser(@Header("If-None-Match") @Nullable String eTag,
                                                              @Query("search") String term,
                                                              @Query("shareTypes[]") List<Integer> shareTypes,
                                                              @Query("itemType") @Nullable List<Integer> itemType,
                                                              @Query("itemId") @Nullable Long itemId,
                                                              @Query("limit") int limit);

    @Headers({HEADER_OCS_API_REQUEST})
    @GET("ocs/v1.php/search/providers?format=json")
    CompletableFuture<OcsSearchProvidersResponseDTO> getSearchProviders();

    @Headers({HEADER_OCS_API_REQUEST})
    @GET("ocs/v1.php/search/providers/{provider}/search?format=json")
    CompletableFuture<OcsSearchResultResponseDTO> search(@Header("If-None-Match") @Nullable String eTag,
                                                         @Path("provider") String provider,
                                                         @Query("term") String term);

    @Headers({HEADER_OCS_API_REQUEST})
    @GET("ocs/v1.php/references/resolve")
    CompletableFuture<Object> resolve(@Header("If-None-Match") @Nullable String eTag,
                                      @Query("reference") URI reference);
}
