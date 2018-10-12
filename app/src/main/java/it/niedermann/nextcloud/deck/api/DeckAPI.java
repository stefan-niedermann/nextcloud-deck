package it.niedermann.nextcloud.deck.api;


import java.util.List;

import io.reactivex.Observable;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface DeckAPI {

        @GET("boards")
        Observable<List<Board>> boards();

        @POST("boards")
        Observable createBoard(Board board);

        @GET("boards")
        Observable<Board> getBoard(long id);

        @GET("boards")
        Observable<List<Card>> getTasks();
}
