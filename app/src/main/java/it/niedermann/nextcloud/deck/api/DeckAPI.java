package it.niedermann.nextcloud.deck.api;


import java.util.List;

import io.reactivex.Observable;
import it.niedermann.nextcloud.deck.model.Board;
import retrofit2.http.GET;

public interface DeckAPI {

        @GET("boards")
        Observable<List<Board>> boards();
}
