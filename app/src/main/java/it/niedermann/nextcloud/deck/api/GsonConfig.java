package it.niedermann.nextcloud.deck.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Stack;

/**
 * Created by david on 27.06.17.
 */

public class GsonConfig {

    public static Gson GetGson() {
        Type boardList = new TypeToken<Board>() {}.getType();
        Type cardList = new TypeToken<Card>() {}.getType();
        Type labelList = new TypeToken<Label>() {}.getType();
        Type stackList = new TypeToken<Stack>() {}.getType();

        return new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(boardList,     new NextcloudArrayDeserializer<>("boards", Board.class))
                .registerTypeAdapter(cardList,     new NextcloudArrayDeserializer<>("cards", Card.class))
                .registerTypeAdapter(labelList,     new NextcloudArrayDeserializer<>("labels", Label.class))
                .registerTypeAdapter(stackList,     new NextcloudArrayDeserializer<>("stacks", Stack.class))
                .registerTypeAdapter(stackList,     new NextcloudDeserializer<>("stacks", Stack.class))
                .create();
    }

}
