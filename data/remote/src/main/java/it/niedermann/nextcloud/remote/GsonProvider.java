package it.niedermann.nextcloud.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.OffsetDateTime;

import it.niedermann.nextcloud.remote.deck.OffsetDateTimeAdapter;
import it.niedermann.nextcloud.remote.deck.UserDtoAdapter;


public class GsonProvider {

    private final Gson gson;

    public GsonProvider() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
                .registerTypeAdapterFactory(OffsetDateTimeAdapter.FACTORY)
                .registerTypeAdapterFactory(UserDtoAdapter.FACTORY)
                .create();
    }

    public Gson getGson() {
        return gson;
    }
}
