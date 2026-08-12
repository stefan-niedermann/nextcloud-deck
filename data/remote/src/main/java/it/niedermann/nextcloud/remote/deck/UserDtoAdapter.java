package it.niedermann.nextcloud.remote.deck;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import it.niedermann.nextcloud.remote.deck.dto.UserDTO;

public class UserDtoAdapter extends TypeAdapter<UserDTO> {

    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        @Override
        @SuppressWarnings("unchecked")
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (type.getRawType() == UserDTO.class) {
                return (TypeAdapter<T>) new UserDtoAdapter((TypeAdapter<UserDTO>) gson.getDelegateAdapter(this, type));
            }
            return null;
        }
    };

    private final TypeAdapter<UserDTO> delegate;

    private UserDtoAdapter(TypeAdapter<UserDTO> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void write(JsonWriter out, UserDTO value) throws IOException {
        delegate.write(out, value);
    }

    @Override
    public UserDTO read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.STRING) {
            final var user = new UserDTO();
            user.setUid(in.nextString());
            return user;
        }
        return delegate.read(in);
    }
}
