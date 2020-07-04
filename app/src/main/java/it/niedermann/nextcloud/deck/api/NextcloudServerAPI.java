package it.niedermann.nextcloud.deck.api;


import java.util.List;

import io.reactivex.Observable;
import it.niedermann.nextcloud.deck.model.ocs.Activity;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.model.ocs.comment.OcsComment;
import it.niedermann.nextcloud.deck.model.ocs.user.OcsUser;
import it.niedermann.nextcloud.deck.model.ocs.user.OcsUserList;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NextcloudServerAPI {

    @GET("cloud/capabilities?format=json")
    Observable<Capabilities> getCapabilities();

    @GET("cloud/users?format=json")
    Observable<OcsUserList> getAllUsers();

    @GET("cloud/users/{uid}?format=json")
    Observable<OcsUser> getUserDetails(@Path("uid") String uid);

    @GET("apps/activity/api/v2/activity/filter?format=json&object_type=deck_card&limit=50&since=-1&sort=asc")
    Observable<List<Activity>> getActivitiesForCard(@Query("object_id") long cardId);

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
