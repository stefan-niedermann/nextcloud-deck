package it.niedermann.nextcloud.deck.api;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Stack;

public class JsonToEntityParser {
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");

    static {
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    protected static <T> T parseJsonObject(JsonObject obj, Class<T> mType) {
        if(mType == Board.class) {
            return (T) parseBoard(obj);
        } else if (mType == Card.class) {
            return (T) parseCard(obj);
        } else if (mType == Stack.class) {
            return (T) parseStack(obj);
        } else if (mType == Label.class) {
            return (T) parseLabel(obj);
        }
        throw new IllegalArgumentException("unregistered type: "+mType.getCanonicalName());
    }


    protected static Board parseBoard(JsonObject e) {
        // throws "Unsupported" exception
        Log.e("### deck (boards call)", e.toString());
        Board board = new Board();
        board.setTitle(getNullAsEmptyString(e.get("title")));
        board.setId(e.get("id").getAsLong());
        return board;
    }

    protected static Card parseCard(JsonObject e) {
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
        card.setDueDate(getTimestamp(e.get("duedate")));
        card.setCommentsUnread(e.get("commentsUnread").getAsInt());
        card.setOwner(getNullAsEmptyString(e.get("owner")));
        card.setArchived(e.get("archived").getAsBoolean());

        return card;
    }
    protected static Stack parseStack(JsonObject e) {
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
    protected static Label parseLabel(JsonObject e) {
        //TODO: impl
        // throws "Unsupported" exception
        //Log.e("### deck (labels call)", e.getAsString());
        Label label = new Label();
        Log.e("### deck", e.getAsString());
        label.setTitle("implement meeeee! (in NextcloudArrayDeserializer.java)");
        return label;
    }

    protected static String getNullAsEmptyString(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
    }

    protected static Date getTimestamp(JsonElement jsonElement) {
        if (jsonElement.isJsonNull()) {
            return null;
        } else {
            try {
                return formatter.parse(jsonElement.getAsString());
            } catch (ParseException e) {
                return null;
            }
        }
    }
}
