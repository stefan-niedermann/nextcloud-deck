package it.niedermann.nextcloud.remote.ocs;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Flexible adapter factory to handle Nextcloud OCS API inconsistencies:
 * 1. Fields expected as Objects but returned as empty Arrays [] when no data is found.
 * 2. Fields expected as Lists but returned as single Objects {} when only one item is found.
 */
public class OcsDataFlexibleAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        final Class<? super T> rawType = type.getRawType();
        final String name = rawType.getName();

        // 1. Handle Objects (DTOs or Ocs wrappers) that might be returned as empty arrays []
        if (name.startsWith("it.niedermann.nextcloud.remote") &&
                !List.class.isAssignableFrom(rawType)) {

            final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
            return new TypeAdapter<T>() {
                @Override
                public void write(JsonWriter out, T value) throws IOException {
                    delegate.write(out, value);
                }

                @Override
                public T read(JsonReader in) throws IOException {
                    if (in.peek() == JsonToken.BEGIN_ARRAY) {
                        System.err.println("OcsDataFlexibleAdapterFactory: skipping unexpected array for object type " + name + " at " + in.getPath());
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

        // 2. Handle Lists that might be returned as single Objects
        if (List.class.isAssignableFrom(rawType)) {
            final Type typeOfT = type.getType();
            if (typeOfT instanceof ParameterizedType) {
                final Type elementType = ((ParameterizedType) typeOfT).getActualTypeArguments()[0];
                if (elementType instanceof Class) {
                    Class<?> elementClass = (Class<?>) elementType;
                    String elementName = elementClass.getName();
                    if (elementName.startsWith("it.niedermann.nextcloud.remote")) {

                        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
                        final TypeAdapter<Object> elementAdapter = (TypeAdapter<Object>) gson.getAdapter(TypeToken.get(elementType));

                        return new TypeAdapter<T>() {
                            @Override
                            public void write(JsonWriter out, T value) throws IOException {
                                delegate.write(out, value);
                            }

                            @Override
                            public T read(JsonReader in) throws IOException {
                                if (in.peek() == JsonToken.BEGIN_OBJECT) {
                                    // System.err.println("OcsDataFlexibleAdapterFactory: List expected for element " + elementName + " but found Object at " + in.getPath());
                                    List<Object> list = new ArrayList<>();
                                    list.add(elementAdapter.read(in));
                                    return (T) list;
                                }
                                return delegate.read(in);
                            }
                        };
                    }
                }
            }
        }

        return null;
    }
}
