package it.niedermann.nextcloud.remote.ocs;


import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.List;

import io.reactivex.rxjava3.core.Single;
import it.niedermann.nextcloud.remote.ocs.dto.CapabilitiesResponse;
import it.niedermann.nextcloud.remote.ocs.dto.OcsAutocompleteResult;
import it.niedermann.nextcloud.remote.ocs.dto.OcsResponse;
import it.niedermann.nextcloud.remote.ocs.dto.OcsSearchProvider;
import it.niedermann.nextcloud.remote.ocs.dto.OcsSearchResult;
import it.niedermann.nextcloud.remote.ocs.dto.OcsUser;
import retrofit2.Call;
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
    Call<OcsResponse<CapabilitiesResponse>> getCapabilities(@Header("If-None-Match") @Nullable String eTag);

    @Headers({HEADER_OCS_API_REQUEST})
    @GET("ocs/v1.php/cloud/capabilities?format=json")
    Single<OcsResponse<CapabilitiesResponse>> getCapabilitiesRx(@Header("If-None-Match") @Nullable String eTag);

    @Headers({HEADER_OCS_API_REQUEST})
    @GET("ocs/v1.php/cloud/users/{userId}?format=json")
    Call<OcsResponse<OcsUser>> getUser(@Header("If-None-Match") @Nullable String eTag,
                                       @Path("userId") String userId);


    @Headers({HEADER_OCS_API_REQUEST})
    @GET("ocs/v1.php/core/autocomplete/get?format=json")
    Call<OcsResponse<List<OcsAutocompleteResult>>> searchUser(@Header("If-None-Match") @Nullable String eTag,
                                                              @Query("search") String term,
                                                              /// `0` = user, `1` = group, should match OcsAutocompleteSource#shareType
                                                              /// TODO User converter class and replace int with enum
                                                              @Query("shareTypes[]") List<Integer> shareTypes,
                                                              @Query("itemType") @Nullable List<Integer> itemType,
                                                              @Query("itemId") @Nullable Long itemId,
                                                              @Query("limit") int limit);

    @Headers({HEADER_OCS_API_REQUEST})
    @GET("ocs/v1.php/search/providers?format=json")
    Call<OcsResponse<List<OcsSearchProvider>>> getSearchProviders();

    @Headers({HEADER_OCS_API_REQUEST})
    @GET("ocs/v1.php/search/providers/{provider}/search?format=json")
    Call<OcsResponse<OcsSearchResult>> search(@Header("If-None-Match") @Nullable String eTag,
                                              @Path("provider") String provider,
                                              @Query("term") String term);

    /// @return JSON sample
    /// ```json
    /// "references": {
    ///   "https:\/\/cloud.example.com\/index.php\/apps\/files\/?dir=.&scrollto=Nextcloud%20Manual.pdf": {
    ///       "richObjectType": "open-graph",
    ///               "richObject": {
    ///           "id": "https:\/\/cloud.example.com\/index.php\/apps\/files\/?dir=.&scrollto=Nextcloud%20Manual.pdf",
    ///                   "name": "https:\/\/cloud.example.com\/index.php\/apps\/files\/?dir=.&scrollto=Nextcloud%20Manual.pdf",
    ///                   "description": null,
    ///                   "thumb": null,
    ///                   "link": "https:\/\/cloud.example.com\/index.php\/apps\/files\/?dir=.&scrollto=Nextcloud%20Manual.pdf"
    ///       },
    ///       "openGraphObject": {
    ///           "id": "https:\/\/cloud.example.com\/index.php\/apps\/files\/?dir=.&scrollto=Nextcloud%20Manual.pdf",
    ///                   "name": "https:\/\/cloud.example.com\/index.php\/apps\/files\/?dir=.&scrollto=Nextcloud%20Manual.pdf",
    ///                   "description": null,
    ///                   "thumb": null,
    ///                   "link": "https:\/\/cloud.example.com\/index.php\/apps\/files\/?dir=.&scrollto=Nextcloud%20Manual.pdf"
    ///       },
    ///       "accessible": true
    ///   }
    /// }
    /// ```

    @Headers({HEADER_OCS_API_REQUEST})
    @GET("ocs/v1.php/references/resolve")
    Call<OcsResponse<Object>> resolve(@Header("If-None-Match") @Nullable String eTag,
                                      @Query("reference") URI reference);
}
