package it.niedermann.nextcloud.deck.data.local.typeconverter;

import androidx.room3.ColumnTypeConverter;

import java.net.MalformedURLException;
import java.net.URL;

public class URLConverter {

    @ColumnTypeConverter
    public String fromURL(URL url) {
        if (url == null) {
            return null;
        }

        return url.toString();
    }

    @ColumnTypeConverter
    public URL fromString(String url) throws MalformedURLException {
        if (url == null) {
            return null;
        }

        return new URL(url);
    }
}
