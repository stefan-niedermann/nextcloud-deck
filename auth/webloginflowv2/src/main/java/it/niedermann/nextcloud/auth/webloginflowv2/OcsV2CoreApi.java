package it.niedermann.nextcloud.auth.webloginflowv2;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface OcsV2CoreApi {

    String HEADER_OCS_API_REQUEST = "OCS-APIRequest: true";
    String HEADER_USER_AGENT = "User-Agent: Deck WebLoginFlowV2";

    @Headers({HEADER_OCS_API_REQUEST, HEADER_USER_AGENT})
    @POST("index.php/login/v2?format=json")
    Call<InitWebLoginFlowV2Response> initWebLoginFlowV2();

    @Headers({HEADER_OCS_API_REQUEST, HEADER_USER_AGENT})
    @POST("index.php/login/v2/poll?format=json")
    Call<PollResponse> pollWebLoginFlowV2(@Body String token);


    record InitWebLoginFlowV2Response(Poll poll, String login) {
        record Poll(String token, String endpoint) {
        }
    }

    record PollResponse(String server, String loginName, String appPassword) {
    }
}
