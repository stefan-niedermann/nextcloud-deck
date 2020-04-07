package it.niedermann.nextcloud.deck.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import it.niedermann.nextcloud.deck.DeckLog;

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

    @Override
    public T deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        DeckLog.verbose(json.toString());
        return JsonToEntityParser.parseJsonObject(json.getAsJsonObject(), mType);

    }
}
