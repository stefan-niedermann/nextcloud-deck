package it.niedermann.nextcloud.deck.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class GsonUTCInstantAdapter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {

    private static final Pattern UNIX_TIMESTAMP = Pattern.compile("^[0-9]+$");

    @Override
    public synchronized JsonElement serialize(Instant date, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(DateTimeFormatter.ISO_INSTANT.format(date));
    }

    @Override
    public synchronized Instant deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        String dateValue = jsonElement.getAsString();
        try {
            final Instant parsedDate = Instant.parse(dateValue);
            if (parsedDate == null) {
                throw new ParseException("Parsed date is null", 0);
            }
            return parsedDate;
        } catch (ParseException e) {
            // fallback to unix timestamp?
            if (UNIX_TIMESTAMP.matcher(dateValue).matches()) {
                return Instant.ofEpochMilli(Long.parseLong(dateValue));
            }
            throw new JsonParseException(e);
        }
    }
}