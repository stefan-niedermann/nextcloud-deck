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

import it.niedermann.nextcloud.deck.DeckConsts;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;

public class JsonToEntityParser {
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");

    static {
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    protected static <T> T parseJsonObject(JsonObject obj, Class<T> mType) {
        if (mType == FullBoard.class) {
            return (T) parseBoard(obj);
        } else if (mType == FullCard.class) {
            return (T) parseCard(obj);
        } else if (mType == FullStack.class) {
            return (T) parseStack(obj);
        } else if (mType == Label.class) {
            return (T) parseLabel(obj);
        }
        throw new IllegalArgumentException("unregistered type: " + mType.getCanonicalName());
    }


    protected static FullBoard parseBoard(JsonObject e) {
        FullBoard fullBoard = new FullBoard();

        DeckLog.log(e.toString());
        Board board = new Board();
        board.setTitle(getNullAsEmptyString(e.get("title")));
        board.setId(e.get("id").getAsLong());
        fullBoard.setBoard(board);

        if (e.has("labels") && !e.get("labels").isJsonNull()) {
            JsonArray labelsJson = e.getAsJsonArray("labels");
            List<Label> labels = new ArrayList<>();
            for (JsonElement labelJson : labelsJson) {
                labels.add(parseLabel(labelJson.getAsJsonObject()));
            }
            fullBoard.setLabels(labels);
        }
        //todo e.get "participants" / acl

        JsonElement owner = e.get("owner");
        if (owner != null) {
            if (owner.isJsonPrimitive()) {//TODO: remove if, let only else!
                Log.d(DeckConsts.DEBUG_TAG, "owner is Primitive, skipping");
            } else
                fullBoard.setOwner(parseUser(owner.getAsJsonObject()));
        }
        return fullBoard;
    }

    protected static FullCard parseCard(JsonObject e) {
        DeckLog.log(e.toString());
        //TODO: impl
        FullCard fullCard = new FullCard();
        Card card = new Card();
        fullCard.setCard(card);
        card.setId(e.get("id").getAsLong());
        card.setTitle(getNullAsEmptyString(e.get("title")));
        card.setDescription(getNullAsEmptyString(e.get("description")));
        card.setStackId(e.get("stackId").getAsLong());
        card.setType(getNullAsEmptyString(e.get("type")));
//        stack.setDeletedAt(e.get("lastModified")) // TODO: parse date!
//        stack.setDeletedAt(e.get("createdAt")) // TODO: parse date!
//        stack.setDeletedAt(e.get("deletedAt")) // TODO: parse date!
        if (e.has("labels") && !e.get("labels").isJsonNull()) {
            JsonArray labelsJson = e.getAsJsonArray("labels");
            List<Label> labels = new ArrayList<>();
            for (JsonElement labelJson : labelsJson) {
                labels.add(parseLabel(labelJson.getAsJsonObject()));
            }
            fullCard.setLabels(labels);
        }
        //todo e.get "participants" / acl
        //todo e.get "attachments"
        card.setStackId(e.get("attachmentCount").getAsInt());
        card.setOrder(e.get("order").getAsInt());
        card.setOverdue(e.get("overdue").getAsInt());
        card.setDueDate(getTimestamp(e.get("duedate")));
        card.setCommentsUnread(e.get("commentsUnread").getAsInt());
        JsonElement owner = e.get("owner");
        if (owner != null) {
            if (owner.isJsonPrimitive()) {//TODO: remove if, let only else!
                Log.d(DeckConsts.DEBUG_TAG, "owner is Primitive, skipping");
            } else
                fullCard.setOwner(parseUser(owner.getAsJsonObject()));
        }
        card.setArchived(e.get("archived").getAsBoolean());

        return fullCard;
    }

    protected static User parseUser(JsonObject e) {
        DeckLog.log(e.toString());
        User user = new User();
        user.setDisplayname(getNullAsEmptyString(e.get("displayname")));
        user.setPrimaryKey(getNullAsEmptyString(e.get("primaryKey")));
        user.setUid(getNullAsEmptyString(e.get("uid")));
        return user;
    }

    protected static FullStack parseStack(JsonObject e) {
        DeckLog.log(e.toString());
        FullStack fullStack = new FullStack();
        Stack stack = new Stack();
        stack.setTitle(getNullAsEmptyString(e.get("title")));
        stack.setBoardId(e.get("boardId").getAsLong());
        stack.setId(e.get("id").getAsLong());
        stack.setOrder(e.get("order").getAsInt());
        if (e.has("cards")) {
            JsonArray cardsJson = e.getAsJsonArray("cards");
            List<FullCard> cards = new ArrayList<>();
            List<Long> cardIds = new ArrayList<>();
            for (JsonElement cardJson : cardsJson) {
//                cards.add(parseCard(cardJson.getAsJsonObject()));
                cardIds.add(cardJson.getAsJsonObject().get("id").getAsLong());
            }
            fullStack.setCards(cardIds);
        }
        fullStack.setStack(stack);
//        stack.setDeletedAt(e.get("deletedAt")) // TODO: parse date!
        return fullStack;
    }

    protected static Label parseLabel(JsonObject e) {
        DeckLog.log(e.toString());
        Label label = new Label();
        label.setId(e.get("id").getAsLong());
        label.setTitle(getNullAsEmptyString(e.get("title")));
        label.setColor(getNullAsEmptyString(e.get("color")));
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
