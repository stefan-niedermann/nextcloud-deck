package it.niedermann.nextcloud.deck.api;


import java.util.List;

import io.reactivex.Observable;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Stack;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface DeckAPI {

        // ### BOARDS
        @POST("boards")
        Observable createBoard(@Body Board board);

        @GET("boards/{id}")
        Observable<Board> getBoard(@Path("id") long id);

        @GET("boards")
        Observable<List<Board>> boards();


        // ### Stacks
        @POST("board/{boardId}/stacks")
        Observable createStack(@Path("boardId") long boardId, @Body Stack stack);

        @PUT("board/{boardId}/stacks/{stackId}")
        Observable<Stack> updateStack(@Path("boardId") long boardId, @Path("stackId") long id, @Body Stack stack);

        @DELETE("board/{boardId}/stacks/{stackId}")
        Observable<Stack> deleteStack(@Path("boardId") long boardId, @Path("stackId") long id);

        @GET("board/{boardId}/stacks/{stackId}")
        Observable<Stack> getStack(@Path("boardId") long boardId, @Path("stackId") long id);

        @GET("board/{boardId}/stacks")
        Observable<List<Stack>> getStacks(@Path("boardId") long boardId);

        @GET("board/{boardId}/stacks/archived")
        Observable<List<Stack>> getArchivedStacks(@Path("boardId") long boardId);


        // ### Cards
        @POST("board/{boardId}/stacks/{stackId}/cards")
        Observable createCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Body Card card);

        @PUT("board/{boardId}/stacks/{stackId}/cards/{cardId}")
        Observable<Card> updateCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId, @Body Card card);

        @DELETE("board/{boardId}/stacks/{stackId}/cards/{cardId}")
        Observable<Card> deleteCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId);

        @GET("board/{boardId}/stacks/{stackId}/cards/{cardId}")
        Observable<Card> getCard(@Path("boardId") long boardId, @Path("stackId") long stackId, @Path("cardId") long cardId);


        // ### LABELS
        @GET("boards/{boardId}labels/{labelId}")
        Observable<Label> getLabel(@Path("boardId") long boardId, @Path("labelId") long labelId);

        @PUT("boards/boards/{boardId}/labels/{labelId}")
        Observable<Label> updateLabel(@Path("boardId") long boardId, @Path("labelId") long labelId, @Body Label label);

        @POST("boards/boards/{boardId}/labels")
        Observable<Label> createLabel(@Path("boardId") long boardId, @Body Label label);

        @DELETE("boards/boards/{boardId}/labels/{labelId}")
        Observable<Label> deleteLabel(@Path("boardId") long boardId, @Path("labelId") long labelId);


}
