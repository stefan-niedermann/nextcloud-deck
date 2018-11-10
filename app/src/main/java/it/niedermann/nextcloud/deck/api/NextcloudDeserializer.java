package it.niedermann.nextcloud.deck.api;

import android.util.Log;

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
        // throws "Unsupported" exception
        Log.e("### deck (boards call)", e.toString());
        Board board = new Board();
        board.setTitle(getNullAsEmptyString(e.get("title")));
        board.setId(e.get("id").getAsLong());
        return board;
    }

    private Card parseCard(JsonObject e) {
        //TODO: impl
        Card card = new Card();
        card.setId(e.get("id").getAsLong());
        card.setTitle(getNullAsEmptyString(e.get("title")));
        card.setDescription(getNullAsEmptyString(e.get("description")));
        card.setStackId(e.get("stackId").getAsLong());
        card.setType(getNullAsEmptyString(e.get("type")));
//        stack.setDeletedAt(e.get("lastModified")) // TODO: parse date!
//        stack.setDeletedAt(e.get("createdAt")) // TODO: parse date!
//        stack.setDeletedAt(e.get("duedate")) // TODO: parse date!
//        stack.setDeletedAt(e.get("deletedAt")) // TODO: parse date!
        //todo labels
        //e.get "labels"
        //e.get "assignedUsers"
        //e.get "attachments"
        card.setStackId(e.get("attachmentCount").getAsInt());
        card.setOrder(e.get("order").getAsInt());
        card.setOverdue(e.get("overdue").getAsInt());
        card.setCommentsUnread(e.get("commentsUnread").getAsInt());
        card.setOwner(getNullAsEmptyString(e.get("owner")));
        card.setArchived(e.get("archived").getAsBoolean());

        return card;
    }
    private Stack parseStack(JsonObject e) {
        Log.e("### deck (stacks call)", e.toString());
        Stack stack = new Stack();
        stack.setTitle(getNullAsEmptyString(e.get("title")));
        stack.setBoardId(e.get("boardId").getAsLong());
        stack.setId(e.get("id").getAsLong());
        stack.setOrder(e.get("order").getAsInt());
        if (e.has("cards")){
            JsonArray cardsJson = e.getAsJsonArray("cards");
            List<Card> cards = new ArrayList<>();
            for (JsonElement cardJson: cardsJson) {
                cards.add(parseCard(cardJson.getAsJsonObject()));
            }
            stack.setCards(cards);
        }
//        stack.setDeletedAt(e.get("deletedAt")) // TODO: parse date!
        return stack;
    }
    private Label parseLabel(JsonObject e) {
        //TODO: impl
        // throws "Unsupported" exception
        //Log.e("### deck (labels call)", e.getAsString());
        Label label = new Label();
        Log.e("### deck", e.getAsString());
        label.setTitle("implement meeeee! (in NextcloudDeserializer.java)");
        return label;
    }

    private String getNullAsEmptyString(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
    }
}
