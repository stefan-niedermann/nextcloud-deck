package it.niedermann.nextcloud.remote.ocs;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import it.niedermann.nextcloud.remote.ocs.dto.OcsUserDTO;

public class OcsUserDtoAdapter extends TypeAdapter<OcsUserDTO> {

    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        @Override
        @SuppressWarnings("unchecked")
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (type.getRawType() == OcsUserDTO.class) {
                return (TypeAdapter<T>) new OcsUserDtoAdapter((TypeAdapter<OcsUserDTO>) gson.getDelegateAdapter(this, type));
            }
            return null;
        }
    };

    private final TypeAdapter<OcsUserDTO> delegate;

    private OcsUserDtoAdapter(TypeAdapter<OcsUserDTO> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void write(JsonWriter out, OcsUserDTO value) throws IOException {
        delegate.write(out, value);
    }

    @Override
    public OcsUserDTO read(JsonReader in) throws IOException {
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
}
