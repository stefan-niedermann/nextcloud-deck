package it.niedermann.nextcloud.remote.deck;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.remote.deck.dto.AccessControlDTO;
import it.niedermann.nextcloud.remote.deck.dto.AttachmentDTO;
import it.niedermann.nextcloud.remote.deck.dto.BoardDTO;
import it.niedermann.nextcloud.remote.deck.dto.CardDTO;
import it.niedermann.nextcloud.remote.deck.dto.CardUpdateDTO;
import it.niedermann.nextcloud.remote.deck.dto.CardUpdateOwnerStringDTO;
import it.niedermann.nextcloud.remote.deck.dto.ColumnDTO;
import it.niedermann.nextcloud.remote.deck.dto.LabelDTO;
import it.niedermann.nextcloud.remote.deck.dto.ReorderDTO;
import it.niedermann.nextcloud.remote.deck.dto.UserForAssignmentDTO;
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

/**
 * @link <a href="https://deck.readthedocs.io/en/latest/API/">Deck REST API</a>
 */
@SuppressWarnings("unused")
public interface DeckApi {

    String MODIFIED_SINCE_HEADER = "If-Modified-Since";
    String IF_NONE_MATCH = "If-None-Match";


    // Boards

    @POST("v1.0/boards")
    CompletableFuture<BoardDTO> createBoard(@Body BoardDTO board);

    @GET("v1.0/boards/{id}")
    CompletableFuture<BoardDTO> getBoard(@Path("id") long id, @Header(MODIFIED_SINCE_HEADER) String lastSync);

    @PUT("v1.0/boards/{id}")
    CompletableFuture<BoardDTO> updateBoard(@Path("id") long id, @Body BoardDTO board);

    @DELETE("v1.0/boards/{id}")
    CompletableFuture<Void> deleteBoard(@Path("id") long id);

    @DELETE("v1.0/boards/{id}/undo_delete")
    CompletableFuture<BoardDTO> restoreBoard(@Path("id") long id);

    @GET("v1.0/boards")
    CompletableFuture<List<BoardDTO>> getBoards(@Query("details") boolean verbose, @Header(MODIFIED_SINCE_HEADER) String lastSync, @Header(IF_NONE_MATCH) String eTag);

    @GET("v1.0/boards")
    CompletableFuture<List<BoardDTO>> getBoards(@Query("details") boolean verbose, @Header(MODIFIED_SINCE_HEADER) String lastSync);


    // Stacks

    @POST("v1.0/boards/{boardId}/stacks")
    CompletableFuture<ColumnDTO> createStack(@Path("boardId") long boardId, @Body ColumnDTO stack);

    @PUT("v1.0/boards/{boardId}/stacks/{stackId}")
    CompletableFuture<ColumnDTO> updateStack(@Path("boardId") long boardId, @Path("stackId") long id, @Body ColumnDTO stack);

    @DELETE("v1.0/boards/{boardId}/stacks/{stackId}")
    CompletableFuture<Void> deleteStack(@Path("boardId") long boardId, @Path("stackId") long id);

    @GET("v1.0/boards/{boardId}/stacks/{stackId}")
    CompletableFuture<ColumnDTO> getStack(@Path("boardId") long boardId, @Path("stackId") long id, @Header(MODIFIED_SINCE_HEADER) String lastSync);

    @GET("v1.0/boards/{boardId}/stacks")
    CompletableFuture<List<ColumnDTO>> getStacks(@Path("boardId") long boardId, @Header(MODIFIED_SINCE_HEADER) String lastSync);

    @GET("v1.0/boards/{boardId}/stacks/archived")
    CompletableFuture<List<ColumnDTO>> getArchivedStacks(@Path("boardId") long boardId, @Header(MODIFIED_SINCE_HEADER) String lastSync);


    // Cards

    @POST("v1.0/boards/{boardId}/stacks/{stackId}/cards")
    CompletableFuture<CardDTO> createCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Body CardDTO card);

    @PUT("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}")
    CompletableFuture<CardDTO> updateCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Body CardUpdateDTO card);

    @PUT("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}")
    CompletableFuture<CardDTO> updateCardOwnerString(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Body CardUpdateOwnerStringDTO card);

    @FormUrlEncoded
    @PUT("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/assignLabel")
    CompletableFuture<Void> assignLabelToCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Field("labelId") long labelId);

    @FormUrlEncoded
    @PUT("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/removeLabel")
    CompletableFuture<Void> unassignLabelFromCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Field("labelId") long labelId);

    @PUT("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/assignUser")
    CompletableFuture<Void> assignUserToCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Body UserForAssignmentDTO assignment);

    @PUT("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/unassignUser")
    CompletableFuture<Void> unassignUserFromCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Body UserForAssignmentDTO assignment);

    @PUT("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/reorder")
    CompletableFuture<List<CardDTO>> moveCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Body ReorderDTO reorder);

    @DELETE("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}")
    CompletableFuture<Void> deleteCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId);

    /**
     * @see <a href=\"https://github.com/nextcloud/deck/issues/2874\">This endpoint does only return {@link AttachmentDTO}s of type {@link EAttachmentType.DECK_FILE}</a>
     */
    @SuppressWarnings("JavadocReference")
    @GET("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}")
    CompletableFuture<CardDTO> getCard_1_0(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Header(MODIFIED_SINCE_HEADER) String lastSync);

    @GET("v1.1/boards/{boardId}/stacks/{stackId}/cards/{cardId}")
    CompletableFuture<CardDTO> getCard_1_1(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Header(MODIFIED_SINCE_HEADER) String lastSync);

    @POST("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/dependentCards/{dependentCardId}")
    CompletableFuture<Void> assignDependentToCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Path("dependentCardId") long dependentCardId);

    @DELETE("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/dependentCards/{dependentCardId}")
    CompletableFuture<Void> unassignDependentFromCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Path("dependentCardId") long dependentCardId);


    // Labels

    @GET("v1.0/boards/{boardId}/labels/{labelId}")
    CompletableFuture<LabelDTO> getLabel(@Path("boardId") long boardId, @Path("labelId") long labelId, @Header(MODIFIED_SINCE_HEADER) String lastSync);

    @PUT("v1.0/boards/{boardId}/labels/{labelId}")
    CompletableFuture<LabelDTO> updateLabel(@Path("boardId") long boardId, @Path("labelId") long labelId, @Body LabelDTO label);

    @POST("v1.0/boards/{boardId}/labels")
    CompletableFuture<LabelDTO> createLabel(@Path("boardId") long boardId, @Body LabelDTO label);

    @DELETE("v1.0/boards/{boardId}/labels/{labelId}")
    CompletableFuture<Void> deleteLabel(@Path("boardId") long boardId, @Path("labelId") long labelId);


    // Attachments

    @GET("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/attachments/{attachmentId}")
    CompletableFuture<ResponseBody> downloadAttachment(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Path("attachmentId") long attachmentId);

    @GET("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/attachments")
    CompletableFuture<List<AttachmentDTO>> getAttachments(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId);

    @Multipart
    @POST("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/attachments")
    CompletableFuture<AttachmentDTO> uploadAttachment(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Part MultipartBody.Part type, @Part MultipartBody.Part attachment);

    @Multipart
    @POST("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/attachments")
    CompletableFuture<AttachmentDTO> uploadAttachment(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Part MultipartBody.Part type, @Part MultipartBody.Part attachment, @Part MultipartBody.Part data);

    @Multipart
    @PUT("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/attachments")
    CompletableFuture<AttachmentDTO> updateAttachment(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Path("attachmentId") long attachmentId, @Part MultipartBody.Part type, @Part MultipartBody.Part attachment);

    @Multipart
    @PUT("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/attachments")
    CompletableFuture<AttachmentDTO> updateAttachment(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Path("attachmentId") long attachmentId, @Part MultipartBody.Part type, @Part MultipartBody.Part attachment, @Part MultipartBody.Part data);

    @DELETE("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/attachments/{attachmentId}")
    CompletableFuture<Void> deleteAttachment(@Query("type") String type, @Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Path("attachmentId") long attachmentId);

    @PUT("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/attachments/{attachmentId}/restore")
    CompletableFuture<AttachmentDTO> restoreAttachment(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Path("attachmentId") long attachmentId);


    // Access control lists

    @POST("v1.0/boards/{boardId}/acl")
    CompletableFuture<AccessControlDTO> createAccessControl(@Path("boardId") long boardId, @Body AccessControlDTO acl);

    @PUT("v1.0/boards/{boardId}/acl/{aclId}")
    CompletableFuture<AccessControlDTO> updateAccessControl(@Path("boardId") long boardId, @Path("aclId") long aclId, @Body AccessControlDTO acl);

    @DELETE("v1.0/boards/{boardId}/acl/{aclId}")
    CompletableFuture<Void> deleteAccessControl(@Path("boardId") long boardId, @Path("aclId") long aclId);

}
