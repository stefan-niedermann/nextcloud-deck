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

/**
 * Created by david on 24.05.17.
 */

public class NextcloudArrayDeserializer<T> implements JsonDeserializer<List<T>> {

    protected final String mKey;
    protected final Class<T> mType;

    public NextcloudArrayDeserializer(String key, Class<T> type) {
        this.mKey = key;
        this.mType = type;
    }

    @Override
    public List<T> deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        List<T> items = new ArrayList<>();
        if (json.isJsonArray()) {
            JsonArray jArr = json.getAsJsonArray();

            for (int i = 0; i < jArr.size(); i++) {
                JsonObject obj = jArr.get(i).getAsJsonObject();
                items.add(JsonToEntityParser.parseJsonObject(obj, mType));
            }
        } else if (json.isJsonObject()) {
            try {
                items.add(JsonToEntityParser.parseJsonObject(json.getAsJsonObject(), mType));
            } catch (Exception e) {
                throw new IllegalArgumentException("NextcloudArrayDeserializer got a Json Object, fallback parsing failed for input: " + json, e);
            }
        } else {
            throw new IllegalArgumentException("NextcloudArrayDeserializer got a malformed Json Object: " + json);
        }
        return items;

    }


}
