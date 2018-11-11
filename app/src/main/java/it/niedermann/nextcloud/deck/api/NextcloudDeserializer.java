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

public class NextcloudDeserializer<T> implements JsonDeserializer<T> {

    protected final String mKey;
    protected final Class<T> mType;


    public NextcloudDeserializer(String key, Class<T> type) {
        this.mKey = key;
        this.mType = type;
    }

    public static final String TAG = NextcloudDeserializer.class.getCanonicalName();

    @Override
    public T deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        return JsonToEntityParser.parseJsonObject(json.getAsJsonObject(), mType);

    }
}
