package it.niedermann.nextcloud.remote.ocs;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class OcsObjectOrEmptyListAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!type.getRawType().getName().startsWith("it.niedermann.nextcloud.remote.ocs.dto")) {
            return null;
        }
        if (!type.getRawType().getSimpleName().endsWith("DTO")) {
            return null;
        }

        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                delegate.write(out, value);
            }

            @Override
            public T read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.BEGIN_ARRAY) {
                    in.beginArray();
                    while (in.hasNext()) {
                        in.skipValue();
                    }
                    in.endArray();
                    return null;
                }
                return delegate.read(in);
            }
        };
    }
}
