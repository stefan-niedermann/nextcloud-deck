package it.niedermann.nextcloud.deck.api;


import java.util.List;

import io.reactivex.Observable;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
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
