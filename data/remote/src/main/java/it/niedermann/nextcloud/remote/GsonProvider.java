package it.niedermann.nextcloud.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class GsonProvider {

    private final Gson gson;

    public GsonProvider() {
        this.gson = new GsonBuilder()
                .create();
    }

    public Gson getGson() {
        return gson;
    }
}
