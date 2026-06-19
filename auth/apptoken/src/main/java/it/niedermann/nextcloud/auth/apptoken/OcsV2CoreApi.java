package it.niedermann.nextcloud.auth.apptoken;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;

public interface OcsV2CoreApi {

    String HEADER_OCS_API_REQUEST = "OCS-APIRequest: true";
    String HEADER_USER_AGENT = "User-Agent: Deck CLI";

    @Headers({HEADER_OCS_API_REQUEST, HEADER_USER_AGENT})
    @GET("getapppassword?format=json")
    Call<GetAppPasswordResponse> getAppPassword(@Header("Authorization") String credentials);

    @Headers(HEADER_OCS_API_REQUEST)
    @DELETE("apppassword?format=json")
    Call<?> deleteAppPassword();

    record GetAppPasswordResponse(Ocs ocs) {
        record Ocs(Data data) {
            record Data(String apppassword) {
            }
        }
    }
}
