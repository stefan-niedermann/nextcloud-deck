package it.niedermann.nextcloud.deck.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;

/**
 * Created by david on 27.06.17.
 */

public class GsonConfig {

    public static Gson GetGson() {
        Type boardList = new TypeToken<List<FullBoard>>() {}.getType();
        Type board = new TypeToken<FullBoard>() {}.getType();
        Type cardList = new TypeToken<FullCard>() {}.getType();
        Type card = new TypeToken<FullCard>() {}.getType();
        Type labelList = new TypeToken<Label>() {}.getType();
        Type label = new TypeToken<Label>() {}.getType();
        Type stackList = new TypeToken<List<FullStack>>() {}.getType();
        Type stack = new TypeToken<FullStack>() {}.getType();

        return new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(boardList,     new NextcloudArrayDeserializer<>("boards", FullBoard.class))
                .registerTypeAdapter(board,         new NextcloudArrayDeserializer<>("board", FullBoard.class))
                .registerTypeAdapter(cardList,      new NextcloudArrayDeserializer<>("cards", FullCard.class))
                .registerTypeAdapter(card,          new NextcloudDeserializer<>("card", FullCard.class))
                .registerTypeAdapter(labelList,     new NextcloudArrayDeserializer<>("labels", Label.class))
                .registerTypeAdapter(label,         new NextcloudDeserializer<>("label", Label.class))
                .registerTypeAdapter(stackList,     new NextcloudArrayDeserializer<>("stacks", FullStack.class))
                .registerTypeAdapter(stack,         new NextcloudDeserializer<>("stack", FullStack.class))
                .create();
    }

}
