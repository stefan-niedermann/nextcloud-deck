package it.niedermann.nextcloud.deck.data.local.typeconverter;

import androidx.room3.TypeConverter;

import java.net.MalformedURLException;
import java.net.URL;

public class URLConverter {

    @TypeConverter
    public String fromURL(URL url) {
        if (url == null) {
            return null;
        }

        return url.toString();
    }

    @TypeConverter
    public URL fromString(String url) throws MalformedURLException {
        if (url == null) {
            return null;
        }

        return new URL(url);
    }
}
