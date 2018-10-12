package it.niedermann.nextcloud.deck.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import it.niedermann.nextcloud.deck.model.board.Board;
import it.niedermann.nextcloud.deck.model.board.Card;

/**
 * Created by david on 27.06.17.
 */

public class GsonConfig {

    public static Gson GetGson() {
        Type boardList = new TypeToken<Board>() {}.getType();
        Type taskList = new TypeToken<Card>() {}.getType();

        return new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(boardList,     new NextcloudDeserializer<>("boards", Board.class))
                .registerTypeAdapter(taskList,     new NextcloudDeserializer<>("tasks", Card.class))
                .create();
    }

}
