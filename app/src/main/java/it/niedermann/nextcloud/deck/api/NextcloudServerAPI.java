package it.niedermann.nextcloud.deck.api;


import io.reactivex.Observable;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NextcloudServerAPI {

    public static final String FORMAT_JSON = "format";

    @GET("cloud/capabilities")
    Observable<FullBoard> createBoard(@Body Board board, @Query("format") String format);

}
