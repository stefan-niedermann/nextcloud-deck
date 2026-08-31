package it.niedermann.nextcloud.remote.ocs;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import it.niedermann.nextcloud.remote.ocs.dto.OcsUserDTO;

public class OcsUserDtoAdapter implements JsonDeserializer<OcsUserDTO> {
    @Override
    public OcsUserDTO deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonArray()) {
            return null;
        }
        return context.deserialize(json, OcsUserDTO.class);
    }
}
