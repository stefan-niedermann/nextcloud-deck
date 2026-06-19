package it.niedermann.nextcloud.remote.deck;


/**
 * @link <a href="https://deck.readthedocs.io/en/latest/API/">Deck REST API</a>
 */
public interface DeckApi {

    String MODIFIED_SINCE_HEADER = "If-Modified-Since";
    String IF_NONE_MATCH = "If-None-Match";


    // Boards
//
//    @POST("v1.0/boards")
//    CompletableFuture<FullBoard> createBoard(@Body Board board);
//
//    @GET("v1.0/boards/{id}")
//    CompletableFuture<FullBoard> getBoard(@Path("id") long id, @Header(MODIFIED_SINCE_HEADER) String lastSync);
//
//    @PUT("v1.0/boards/{id}")
//    CompletableFuture<FullBoard> updateBoard(@Path("id") long id, @Body Board board);
//
//    @DELETE("v1.0/boards/{id}")
//    CompletableFuture<EmptyResponse> deleteBoard(@Path("id") long id);
//
//    @DELETE("v1.0/boards/{id}/undo_delete")
//    CompletableFuture<FullBoard> restoreBoard(@Path("id") long id);
//
//    @GET("v1.0/boards")
//    CompletableFuture<List<FullBoard>> getBoards(@Query("details") boolean verbose, @Header(MODIFIED_SINCE_HEADER) String lastSync, @Header(IF_NONE_MATCH) String eTag);
//
//    @GET("v1.0/boards")
//    CompletableFuture<List<FullBoard>> getBoards(@Query("details") boolean verbose, @Header(MODIFIED_SINCE_HEADER) String lastSync);
//
//
//    // Stacks
//
//    @POST("v1.0/boards/{boardId}/stacks")
//    CompletableFuture<FullStack> createStack(@Path("boardId") long boardId, @Body Stack stack);
//
//    @PUT("v1.0/boards/{boardId}/stacks/{stackId}")
//    CompletableFuture<FullStack> updateStack(@Path("boardId") long boardId, @Path("stackId") long id, @Body Stack stack);
//
//    @DELETE("v1.0/boards/{boardId}/stacks/{stackId}")
//    CompletableFuture<EmptyResponse> deleteStack(@Path("boardId") long boardId, @Path("stackId") long id);
//
//    @GET("v1.0/boards/{boardId}/stacks/{stackId}")
//    CompletableFuture<FullStack> getStack(@Path("boardId") long boardId, @Path("stackId") long id, @Header(MODIFIED_SINCE_HEADER) String lastSync);
//
//    @GET("v1.0/boards/{boardId}/stacks")
//    CompletableFuture<List<FullStack>> getStacks(@Path("boardId") long boardId, @Header(MODIFIED_SINCE_HEADER) String lastSync);
//
//    @GET("v1.0/boards/{boardId}/stacks/archived")
//    CompletableFuture<List<Stack>> getArchivedStacks(@Path("boardId") long boardId, @Header(MODIFIED_SINCE_HEADER) String lastSync);
//
//
//    // Cards
//
//    @POST("v1.0/boards/{boardId}/stacks/{stackId}/cards")
//    CompletableFuture<FullCard> createCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Body Card card);
//
//    @PUT("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}")
//    CompletableFuture<FullCard> updateCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Body CardUpdate card);
//
//    @PUT("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}")
//    CompletableFuture<FullCard> updateCardOwnerString(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Body CardUpdateOwnerString card);
//
//    @FormUrlEncoded
//    @PUT("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/assignLabel")
//    CompletableFuture<EmptyResponse> assignLabelToCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Field("labelId") long labelId);
//
//    @FormUrlEncoded
//    @PUT("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/removeLabel")
//    CompletableFuture<EmptyResponse> unassignLabelFromCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Field("labelId") long labelId);
//
//    @FormUrlEncoded
//    @PUT("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/assignUser")
//    CompletableFuture<EmptyResponse> assignUserToCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Body() UserForAssignment assignment);
//
//    @FormUrlEncoded
//    @PUT("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/unassignUser")
//    CompletableFuture<EmptyResponse> unassignUserFromCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Body() UserForAssignment assignment);
//
//    @PUT("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/reorder")
//    CompletableFuture<List<FullCard>> moveCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Body Reorder reorder);
//
//    @DELETE("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}")
//    CompletableFuture<EmptyResponse> deleteCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId);
//
//    /**
//     * @see <a href="https://github.com/nextcloud/deck/issues/2874">This endpoint does only return {@link Attachment}s of type {@link EAttachmentType.DECK_FILE}</a>
//     */
//    @SuppressWarnings("JavadocReference")
//    @GET("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}")
//    CompletableFuture<FullCard> getCard_1_0(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Header(MODIFIED_SINCE_HEADER) String lastSync);
//
//    @GET("v1.1/boards/{boardId}/stacks/{stackId}/cards/{cardId}")
//    CompletableFuture<FullCard> getCard_1_1(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Header(MODIFIED_SINCE_HEADER) String lastSync);
//
//
//    // Labels
//
//    @GET("v1.0/boards/{boardId}/labels/{labelId}")
//    CompletableFuture<Label> getLabel(@Path("boardId") long boardId, @Path("labelId") long labelId, @Header(MODIFIED_SINCE_HEADER) String lastSync);
//
//    @PUT("v1.0/boards/{boardId}/labels/{labelId}")
//    CompletableFuture<Label> updateLabel(@Path("boardId") long boardId, @Path("labelId") long labelId, @Body Label label);
//
//    @POST("v1.0/boards/{boardId}/labels")
//    CompletableFuture<Label> createLabel(@Path("boardId") long boardId, @Body Label label);
//
//    @DELETE("v1.0/boards/{boardId}/labels/{labelId}")
//    CompletableFuture<EmptyResponse> deleteLabel(@Path("boardId") long boardId, @Path("labelId") long labelId);
//
//
//    // Attachments
//
//    @GET("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/attachments/{attachmentId}")
//    CompletableFuture<ResponseBody> downloadAttachment(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Path("attachmentId") long attachmentId);
//
//    @GET("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/attachments")
//    CompletableFuture<List<Attachment>> getAttachments(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId);
//
//    @Multipart
//    @POST("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/attachments")
//    CompletableFuture<Attachment> uploadAttachment(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Part MultipartBody.Part type, @Part MultipartBody.Part attachment);
//
//    @Multipart
//    @POST("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/attachments")
//    CompletableFuture<Attachment> uploadAttachment(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Part MultipartBody.Part type, @Part MultipartBody.Part attachment, @Part MultipartBody.Part data);
//
//    @Multipart
//    @PUT("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/attachments")
//    CompletableFuture<Attachment> updateAttachment(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Path("attachmentId") long attachmentId, @Part MultipartBody.Part type, @Part MultipartBody.Part attachment);
//
//    @Multipart
//    @PUT("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/attachments")
//    CompletableFuture<Attachment> updateAttachment(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Path("attachmentId") long attachmentId, @Part MultipartBody.Part type, @Part MultipartBody.Part attachment, @Part MultipartBody.Part data);
//
//    @DELETE("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/attachments/{attachmentId}")
//    CompletableFuture<EmptyResponse> deleteAttachment(@Query("type") String type, @Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Path("attachmentId") long attachmentId);
//
//    @PUT("v1.0/boards/{boardId}/stacks/{stackId}/cards/{cardId}/attachments/{attachmentId}/restore")
//    CompletableFuture<Attachment> restoreAttachment(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Path("attachmentId") long attachmentId);
//
//
//    // Access control lists
//
//    @POST("v1.0/boards/{boardId}/acl")
//    CompletableFuture<AccessControl> createAccessControl(@Path("boardId") long boardId, @Body AccessControl acl);
//
//    @PUT("v1.0/boards/{boardId}/acl/{aclId}")
//    CompletableFuture<AccessControl> updateAccessControl(@Path("boardId") long boardId, @Path("aclId") long aclId, @Body AccessControl acl);
//
//    @DELETE("v1.0/boards/{boardId}/acl/{aclId}")
//    CompletableFuture<EmptyResponse> deleteAccessControl(@Path("boardId") long boardId, @Path("aclId") long aclId, @Body AccessControl acl);

}
