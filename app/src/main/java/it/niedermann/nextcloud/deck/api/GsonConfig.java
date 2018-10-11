package it.niedermann.nextcloud.deck.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import it.niedermann.nextcloud.deck.model.Board;

/**
 * Created by david on 27.06.17.
 */

public class GsonConfig {

    public static Gson GetGson() {
        Type boardList = new TypeToken<List<Board>>() {}.getType();

        return new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(boardList,     new NextcloudDeserializer<>("boards", Board.class))
                .create();
    }

}