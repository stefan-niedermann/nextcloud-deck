package it.niedermann.nextcloud.deck.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Stack;

/**
 * Created by david on 24.05.17.
 */

public class NextcloudDeserializer<T> implements JsonDeserializer<List<T>> {

    private final String mKey;
    private final Class<T> mType;


    public NextcloudDeserializer(String key, Class<T> type) {
        this.mKey = key;
        this.mType = type;
    }

    public static final String TAG = NextcloudDeserializer.class.getCanonicalName();

    @Override
    public List<T> deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        JsonArray jArr = json.getAsJsonArray();

        List<T> items = new ArrayList<>();
        for(int i = 0; i < jArr.size(); i++) {
            JsonObject obj = jArr.get(i).getAsJsonObject();
            if(mType == Board.class) {
                items.add((T) parseBoard(obj));
            } else if (mType == Card.class) {
                items.add((T) parseCard(obj));
            } else if (mType == Stack.class) {
                items.add((T) parseStack(obj));
            } else if (mType == Label.class) {
                items.add((T) parseLabel(obj));
            }
        }

        return items;
    }


    private Board parseBoard(JsonObject e) {
        return new Board(0, e.get("id").getAsLong(), getNullAsEmptyString(e.get("title")));
    }

    private Card parseCard(JsonObject e) {
        return new Card(e.get("id").getAsLong(), getNullAsEmptyString(e.get("title")));
    }
    private Stack parseStack(JsonObject e) {
        //TODO: impl
        Stack stack = new Stack();
        stack.setTitle("not implemented yet...  (in NextcloudDeserializer.java)");
        return stack;
    }
    private Label parseLabel(JsonObject e) {
        //TODO: impl
        Label label = new Label();
        label.setTitle("implement meeeee! (in NextcloudDeserializer.java)");
        return label;
    }

    private String getNullAsEmptyString(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
    }
}
