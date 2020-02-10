package it.niedermann.nextcloud.deck.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class GsonUTCDateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

    private final DateFormat dateFormat;
    private final Pattern UNIX_TIMESTAMP = Pattern.compile("^[0-9]+$");

    public GsonUTCDateAdapter() {
        //This is the format I need
      dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
      //This is the key line which converts the date to UTC which cannot be accessed with the default serializer
      dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override public synchronized JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(dateFormat.format(date));
    }

    @Override public synchronized Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        String dateValue = jsonElement.getAsString();
        try {
          return dateFormat.parse(dateValue);
      } catch (ParseException e) {
          // fallback to unix timestamp?
          if (UNIX_TIMESTAMP.matcher(dateValue).matches()){
              return new Date(Long.valueOf(dateValue));
          }
          throw new JsonParseException(e);
      }
    }
}