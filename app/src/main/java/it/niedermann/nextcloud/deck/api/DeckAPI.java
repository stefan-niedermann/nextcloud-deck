package it.niedermann.nextcloud.deck.api;


import com.nextcloud.android.sso.api.ParsedResponse;

import java.util.List;

import io.reactivex.Observable;
import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.model.propagation.CardUpdate;
import it.niedermann.nextcloud.deck.model.propagation.Reorder;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
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

public interface DeckAPI {

    String MODIFIED_SINCE_HEADER = "If-Modified-Since";
    String IF_NONE_MATCH = "If-None-Match";

    // ### BOARDS
    @POST("boards")
    Observable<FullBoard> createBoard(@Body Board board);

    @GET("boards/{id}")
    Observable<FullBoard> getBoard(@Path("id") long id, @Header(MODIFIED_SINCE_HEADER) String lastSync);

    @PUT("boards/{id}")
    Observable<FullBoard> updateBoard(@Path("id") long id, @Body Board board);

    @DELETE("boards/{id}")
    Observable<Void> deleteBoard(@Path("id") long id);

    @DELETE("boards/{id}/undo_delete")
    Observable<FullBoard> restoreBoard(@Path("id") long id);

    @GET("boards")
    Observable<ParsedResponse<List<FullBoard>>> getBoards(@Query ("details") boolean verbose, @Header(MODIFIED_SINCE_HEADER) String lastSync, @Header(IF_NONE_MATCH) String eTag);


    // ### Stacks
    @POST("boards/{boardId}/stacks")
    Observable<FullStack>  createStack(@Path("boardId") long boardId, @Body Stack stack);

    @PUT("boards/{boardId}/stacks/{stackId}")
    Observable<FullStack> updateStack(@Path("boardId") long boardId, @Path("stackId") long id, @Body Stack stack);

    @DELETE("boards/{boardId}/stacks/{stackId}")
    Observable<Void> deleteStack(@Path("boardId") long boardId, @Path("stackId") long id);

    @GET("boards/{boardId}/stacks/{stackId}")
    Observable<FullStack> getStack(@Path("boardId") long boardId, @Path("stackId") long id, @Header(MODIFIED_SINCE_HEADER) String lastSync);

    @GET("boards/{boardId}/stacks")
    Observable<List<FullStack>> getStacks(@Path("boardId") long boardId, @Header(MODIFIED_SINCE_HEADER) String lastSync);

    @GET("boards/{boardId}/stacks/archived")
    Observable<List<Stack>> getArchivedStacks(@Path("boardId") long boardId, @Header(MODIFIED_SINCE_HEADER) String lastSync);


    // ### Cards
    @POST("boards/{boardId}/stacks/{stackId}/cards")
    Observable<FullCard> createCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Body Card card);

    @PUT("boards/{boardId}/stacks/{stackId}/cards/{cardId}")
    Observable<FullCard> updateCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Body CardUpdate card);

    @FormUrlEncoded
    @PUT("boards/{boardId}/stacks/{stackId}/cards/{cardId}/assignLabel")
    Observable<Void> assignLabelToCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Field("labelId") long labelId);

    @FormUrlEncoded
    @PUT("boards/{boardId}/stacks/{stackId}/cards/{cardId}/removeLabel")
    Observable<Void> unassignLabelFromCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Field("labelId") long labelId);

    @FormUrlEncoded
    @PUT("boards/{boardId}/stacks/{stackId}/cards/{cardId}/assignUser")
    Observable<Void> assignUserToCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Field("userId") String userUID);

    @FormUrlEncoded
    @PUT("boards/{boardId}/stacks/{stackId}/cards/{cardId}/unassignUser")
    Observable<Void> unassignUserFromCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Field("userId")  String userUID);

    @PUT("boards/{boardId}/stacks/{stackId}/cards/{cardId}/reorder")
    Observable<List<FullCard>> moveCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Body Reorder reorder);

    @DELETE("boards/{boardId}/stacks/{stackId}/cards/{cardId}")
    Observable<Void> deleteCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId);

    @GET("boards/{boardId}/stacks/{stackId}/cards/{cardId}")
    Observable<FullCard> getCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Header(MODIFIED_SINCE_HEADER) String lastSync);


    // ### LABELS
    @GET("boards/{boardId}/labels/{labelId}")
    Observable<Label> getLabel(@Path("boardId") long boardId, @Path("labelId") long labelId, @Header(MODIFIED_SINCE_HEADER) String lastSync);

    @PUT("boards/{boardId}/labels/{labelId}")
    Observable<Label> updateLabel(@Path("boardId") long boardId, @Path("labelId") long labelId, @Body Label label);

    @POST("boards/{boardId}/labels")
    Observable<Label> createLabel(@Path("boardId") long boardId, @Body Label label);

    @DELETE("boards/{boardId}/labels/{labelId}")
    Observable<Void> deleteLabel(@Path("boardId") long boardId, @Path("labelId") long labelId);


    // ### ATTACHMENTS
    @GET("boards/{boardId}/stacks/{stackId}/cards/{cardId}/attachments/{attachmentId}")
    Observable<ResponseBody> downloadAttachment(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Path("attachmentId") long attachmentId);

    @GET("boards/{boardId}/stacks/{stackId}/cards/{cardId}/attachments")
    Observable<List<Attachment>> getAttachments(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId);

    @Multipart
    @POST("boards/{boardId}/stacks/{stackId}/cards/{cardId}/attachments?type=deck_file")
    Observable<Attachment> uploadAttachment(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId,  @Part MultipartBody.Part type, @Part MultipartBody.Part attachment);

    @Multipart
    @PUT("boards/{boardId}/stacks/{stackId}/cards/{cardId}/attachments?type=deck_file")
    Observable<Attachment>  updateAttachment(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Path("attachmentId") long attachmentId, @Part MultipartBody.Part type, @Part MultipartBody.Part attachment);

    @DELETE("boards/{boardId}/stacks/{stackId}/cards/{cardId}/attachments/{attachmentId}")
    Observable<Void> deleteAttachment(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Path("attachmentId") long attachmentId);

    @PUT("boards/{boardId}/stacks/{stackId}/cards/{cardId}/attachments/{attachmentId}/restore")
    Observable<Attachment> restoreAttachment(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Path("attachmentId") long attachmentId);

    // ### ACL
    @POST("boards/{boardId}/acl")
    Observable<AccessControl> createAccessControl(@Path("boardId") long boardId, @Body AccessControl acl);

    @PUT("boards/{boardId}/acl/{aclId}")
    Observable<AccessControl> updateAccessControl(@Path("boardId") long boardId, @Path("aclId") long aclId, @Body AccessControl acl);

    @DELETE("boards/{boardId}/acl/{aclId}")
    Observable<Void> deleteAccessControl(@Path("boardId") long boardId, @Path("aclId") long aclId, @Body AccessControl acl);

    // ### COMMENTS

}
