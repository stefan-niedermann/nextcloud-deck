package it.niedermann.nextcloud.deck.api;


import com.nextcloud.android.sso.api.ParsedResponse;

import java.util.List;

import io.reactivex.Observable;
import it.niedermann.nextcloud.deck.model.ocs.Activity;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.model.ocs.comment.OcsComment;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProject;
import it.niedermann.nextcloud.deck.model.ocs.projects.OcsProjectList;
import it.niedermann.nextcloud.deck.model.ocs.projects.to.OcsProjectLink;
import it.niedermann.nextcloud.deck.model.ocs.projects.to.OcsProjectNameForCreate;
import it.niedermann.nextcloud.deck.model.ocs.projects.to.OcsProjectNameUpdate;
import it.niedermann.nextcloud.deck.model.ocs.user.GroupMemberUIDs;
import it.niedermann.nextcloud.deck.model.ocs.user.OcsUser;
import it.niedermann.nextcloud.deck.model.ocs.user.OcsUserList;
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
    Observable<ParsedResponse<Capabilities>> getCapabilities(@Header("If-None-Match") String eTag);


    // Projects

    @GET("collaboration/resources/deck-card/{cardId}?format=json")
    Observable<OcsProjectList> getProjectsForCard(@Path("cardId") long cardId);

    @POST("collaboration/resources/collections/{ocsProjectId}?format=json")
    Observable<OcsProject> addCardToProject(@Path("ocsProjectId") long projectId, @Body OcsProjectLink comment);

    @DELETE("collaboration/resources/collections/{ocsProjectId}?resourceType=deck-card&resourceId={cardId}")
    Observable<OcsProject> removeCardFromProject(@Path("ocsProjectId") long projectId, @Path("cardId") long cardId);

    @PUT("collaboration/resources/collections/{ocsProjectId}?format=json")
    Observable<OcsProject> updateProjectName(@Path("ocsProjectId") long projectId, @Body OcsProjectNameUpdate ocsProjectName);

    @POST("collaboration/resources/deck-card/{cardId}?format=json")
    Observable<OcsProject> createProjectForCard(@Path("cardId") long cardId, @Body OcsProjectNameForCreate ocsProjectName);


    // Users

    @GET("apps/files_sharing/api/v1/sharees?format=json&perPage=20&itemType=0%2C1%2C7")
    Observable<OcsUserList> searchUser(@Query("search") String searchTerm);

    @GET("cloud/groups/{search}?format=json")
    Observable<GroupMemberUIDs> searchGroupMembers(@Path("search") String groupUid);

    @GET("cloud/users/{search}?format=json")
    Observable<OcsUser> getSingleUserData(@Path("search") String userUid);


    // Activities

    @GET("apps/activity/api/v2/activity/filter?format=json&object_type=deck_card&limit=50&since=-1&sort=asc")
    Observable<List<Activity>> getActivitiesForCard(@Query("object_id") long cardId);


    // Comments

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json;charset=utf-8"
    })
    @GET("apps/deck/api/v1.0/cards/{cardId}/comments")
    Observable<OcsComment> getCommentsForCard(@Path("cardId") long cardId);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json;charset=utf-8"
    })
    @POST("apps/deck/api/v1.0/cards/{cardId}/comments")
    Observable<OcsComment> createCommentForCard(@Path("cardId") long cardId, @Body DeckComment comment);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json;charset=utf-8"
    })
    @PUT("apps/deck/api/v1.0/cards/{cardId}/comments/{commentId}")
    Observable<OcsComment> updateCommentForCard(@Path("cardId") long cardId, @Path("commentId") long commentId, @Body DeckComment comment);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json;charset=utf-8"
    })
    @DELETE("apps/deck/api/v1.0/cards/{cardId}/comments/{commentId}")
    Observable<Void> deleteCommentForCard(@Path("cardId") long cardId, @Path("commentId") long commentId);
}
