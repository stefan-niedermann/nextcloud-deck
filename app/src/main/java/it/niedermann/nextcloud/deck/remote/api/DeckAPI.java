package it.niedermann.nextcloud.deck.remote.api;


import com.nextcloud.android.sso.api.EmptyResponse;

import java.util.List;

import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.enums.EAttachmentType;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.model.ocs.user.UserForAssignment;
import it.niedermann.nextcloud.deck.model.propagation.CardUpdate;
import it.niedermann.nextcloud.deck.model.propagation.Reorder;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @link <a href="https://deck.readthedocs.io/en/latest/API/">Deck REST API</a>
 */
public interface DeckAPI {

    String MODIFIED_SINCE_HEADER = "If-Modified-Since";
    String IF_NONE_MATCH = "If-None-Match";


    // Boards

    @POST("v1.0/boards")
    Call<FullBoard> createBoard(@Body Board board);

    @GET("v1.0/boards/{id}")
    Call<FullBoard> getBoard(@Path("id") long id, @Header(MODIFIED_SINCE_HEADER) String lastSync);

    @PUT("v1.0/boards/{id}")
    Call<FullBoard> updateBoard(@Path("id") long id, @Body Board board);

    @DELETE("v1.0/boards/{id}")
    Call<EmptyResponse> deleteBoard(@Path("id") long id);

    @DELETE("v1.0/boards/{id}/undo_delete")
    Call<FullBoard> restoreBoard(@Path("id") long id);

    @GET("v1.0/boards")
    Call<List<FullBoard>> getBoards(@Query("details") boolean verbose, @Header(MODIFIED_SINCE_HEADER) String lastSync, @Header(IF_NONE_MATCH) String eTag);

    @GET("v1.0/boards")
    Call<List<FullBoard>> getBoards(@Query("details") boolean verbose, @Header(MODIFIED_SINCE_HEADER) String lastSync);


    // Stacks

    @POST("v1.0/boards/{boardId}/stacks")
    Call<FullStack> createStack(@Path("boardId") long boardId, @Body Stack stack);

    @PUT("v1.0/boards/{boardId}/stacks/{stackId}")
    Call<FullStack> updateStack(@Path("boardId") long boardId, @Path("stackId") long id, @Body Stack stack);

    @DELETE("v1.0/boards/{boardId}/stacks/{stackId}")
    Call<EmptyResponse> deleteStack(@Path("boardId") long boardId, @Path("stackId") long id);

    @GET("v1.0/boards/{boardId}/stacks/{stackId}")
    Call<FullStack> getStack(@Path("boardId") long boardId, @Path("stackId") long id, @Header(MODIFIED_SINCE_HEADER) String lastSync);

    @GET("v1.0/boards/{boardId}/stacks")
    Call<List<FullStack>> getStacks(@Path("boardId") long boardId, @Header(MODIFIED_SINCE_HEADER) String lastSync);

    @GET("v1.0/boards/{boardId}/stacks/archived")
    Call<List<Stack>> getArchivedStacks(@Path("boardId") long boardId, @Header(MODIFIED_SINCE_HEADER) String lastSync);


    // Cards

    @POST("v1.0/boards/{boardId}/stacks/{stackId}/cards")
    Call<FullCard> createCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Body Card card);

    @PUT("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}")
    Call<FullCard> updateCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Body CardUpdate card);

    @FormUrlEncoded
    @PUT("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/assignLabel")
    Call<EmptyResponse> assignLabelToCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Field("labelId") long labelId);

    @FormUrlEncoded
    @PUT("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/removeLabel")
    Call<EmptyResponse> unassignLabelFromCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Field("labelId") long labelId);

    @FormUrlEncoded
    @PUT("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/assignUser")
    Call<EmptyResponse> assignUserToCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Body() UserForAssignment assignment);

    @FormUrlEncoded
    @PUT("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/unassignUser")
    Call<EmptyResponse> unassignUserFromCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Body() UserForAssignment assignment);

    @PUT("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/reorder")
    Call<List<FullCard>> moveCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Body Reorder reorder);

    @DELETE("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}")
    Call<EmptyResponse> deleteCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId);

    /**
     * @see <a href="https://github.com/nextcloud/deck/issues/2874">This endpoint does only return {@link Attachment}s of type {@link EAttachmentType.DECK_FILE}</a>
     */
    @SuppressWarnings("JavadocReference")
    @GET("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}")
    Call<FullCard> getCard_1_0(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Header(MODIFIED_SINCE_HEADER) String lastSync);

    @GET("v1.1/boards/{boardId}/stacks/{stackId}/cards/{cardId}")
    Call<FullCard> getCard_1_1(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Header(MODIFIED_SINCE_HEADER) String lastSync);


    // Labels

    @GET("v1.0/boards/{boardId}/labels/{labelId}")
    Call<Label> getLabel(@Path("boardId") long boardId, @Path("labelId") long labelId, @Header(MODIFIED_SINCE_HEADER) String lastSync);

    @PUT("v1.0/boards/{boardId}/labels/{labelId}")
    Call<Label> updateLabel(@Path("boardId") long boardId, @Path("labelId") long labelId, @Body Label label);

    @POST("v1.0/boards/{boardId}/labels")
    Call<Label> createLabel(@Path("boardId") long boardId, @Body Label label);

    @DELETE("v1.0/boards/{boardId}/labels/{labelId}")
    Call<EmptyResponse> deleteLabel(@Path("boardId") long boardId, @Path("labelId") long labelId);


    // Attachments

    @GET("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/attachments/{attachmentId}")
    Call<ResponseBody> downloadAttachment(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Path("attachmentId") long attachmentId);

    @GET("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/attachments")
    Call<List<Attachment>> getAttachments(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId);

    @Multipart
    @POST("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/attachments")
    Call<Attachment> uploadAttachment(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Part MultipartBody.Part type, @Part MultipartBody.Part attachment);

    @Multipart
    @PUT("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/attachments")
    Call<Attachment> updateAttachment(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Path("attachmentId") long attachmentId, @Part MultipartBody.Part type, @Part MultipartBody.Part attachment);

    @DELETE("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/attachments/{attachmentId}")
    Call<EmptyResponse> deleteAttachment(@Query("type") String type, @Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Path("attachmentId") long attachmentId);

    @PUT("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/attachments/{attachmentId}/restore")
    Call<Attachment> restoreAttachment(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Path("attachmentId") long attachmentId);


    // Access control lists

    @POST("v1.0/boards/{boardId}/acl")
    Call<AccessControl> createAccessControl(@Path("boardId") long boardId, @Body AccessControl acl);

    @PUT("v1.0/boards/{boardId}/acl/{aclId}")
    Call<AccessControl> updateAccessControl(@Path("boardId") long boardId, @Path("aclId") long aclId, @Body AccessControl acl);

    @DELETE("v1.0/boards/{boardId}/acl/{aclId}")
    Call<EmptyResponse> deleteAccessControl(@Path("boardId") long boardId, @Path("aclId") long aclId, @Body AccessControl acl);

}
