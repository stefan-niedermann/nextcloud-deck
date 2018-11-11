package it.niedermann.nextcloud.deck.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Stack;

/**
 * Created by david on 27.06.17.
 */

public class GsonConfig {

    public static Gson GetGson() {
        Type boardList = new TypeToken<List<Board>>() {}.getType();
        Type board = new TypeToken<Board>() {}.getType();
        Type cardList = new TypeToken<Card>() {}.getType();
        Type labelList = new TypeToken<Label>() {}.getType();
        Type stackList = new TypeToken<List<Stack>>() {}.getType();
        Type stack = new TypeToken<Stack>() {}.getType();

        return new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(boardList,     new NextcloudArrayDeserializer<>("boards", Board.class))
                .registerTypeAdapter(board,     new NextcloudArrayDeserializer<>("board", Board.class))
                .registerTypeAdapter(cardList,     new NextcloudArrayDeserializer<>("cards", Card.class))
                .registerTypeAdapter(labelList,     new NextcloudArrayDeserializer<>("labels", Label.class))
                .registerTypeAdapter(stack,     new NextcloudDeserializer<>("stack", Stack.class))
                .registerTypeAdapter(stackList,     new NextcloudArrayDeserializer<>("stacks", Stack.class))
                .create();
    }

}
