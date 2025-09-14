package it.niedermann.nextcloud.deck.remote.api.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import it.niedermann.android.util.ColorUtil;

public class JsonColorSerializer extends TypeAdapter<Integer> {
    @Override
    public void write(JsonWriter out, Integer value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(ColorUtil.intColorToHexString(value));
        }
    }

    @Override
    public Integer read(JsonReader in) throws IOException {
        // currently not needed
        return null;
    }
}
