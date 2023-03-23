package it.niedermann.nextcloud.deck.remote.api;


import java.util.List;

import it.niedermann.nextcloud.deck.model.ocs.Activity;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.model.ocs.comment.OcsComment;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProjectList;
import it.niedermann.nextcloud.deck.model.ocs.user.GroupMemberUIDs;
import it.niedermann.nextcloud.deck.model.ocs.user.OcsUser;
import it.niedermann.nextcloud.deck.model.ocs.user.OcsUserList;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * @link <a href="https://deck.readthedocs.io/en/latest/API-Nextcloud/">Nextcloud REST API</a>
 */
public interface NextcloudServerAPI {


    // Capabilities

    @GET("cloud/capabilities?format=json")
    Call<Capabilities> getCapabilities(@Header("If-None-Match") String eTag);


    // Projects

    @GET("collaboration/resources/deck-card/{cardId}?format=json")
    Call<OcsProjectList> getProjectsForCard(@Path("cardId") long cardId);


    // Users

    @GET("apps/files_sharing/api/v1/sharees?format=json&lookup=false&perPage=20&itemType=0%2C1%2C7")
    Call<OcsUserList> searchUser(@Query("search") String searchTerm);

    @GET("cloud/groups/{search}?format=json")
    Call<GroupMemberUIDs> searchGroupMembers(@Path("search") String groupUid);

    @GET("cloud/users/{search}?format=json")
    Call<OcsUser> getSingleUserData(@Path("search") String userUid);


    // Activities

    @GET("apps/activity/api/v2/activity/filter?format=json&object_type=deck_card&limit=50&since=-1&sort=asc")
    Call<List<Activity>> getActivitiesForCard(@Query("object_id") long cardId);


    // Comments

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json;charset=utf-8"
    })
    @GET("apps/deck/api/v1.0/cards/{cardId}/comments")
    Call<OcsComment> getCommentsForCard(@Path("cardId") long cardId);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json;charset=utf-8"
    })
    @POST("apps/deck/api/v1.0/cards/{cardId}/comments")
    Call<OcsComment> createCommentForCard(@Path("cardId") long cardId, @Body DeckComment comment);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json;charset=utf-8"
    })
    @PUT("apps/deck/api/v1.0/cards/{cardId}/comments/{commentId}")
    Call<OcsComment> updateCommentForCard(@Path("cardId") long cardId, @Path("commentId") long commentId, @Body DeckComment comment);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json;charset=utf-8"
    })
    @DELETE("apps/deck/api/v1.0/cards/{cardId}/comments/{commentId}")
    Call<Void> deleteCommentForCard(@Path("cardId") long cardId, @Path("commentId") long commentId);
}
