package it.niedermann.nextcloud.deck.persistence.sync.adapters.db;

import androidx.room.TypeConverter;

import java.time.Instant;

public class DateTypeConverter {

    @TypeConverter
    public static Instant toInstant(Long value) {
        return value == null ? null : Instant.ofEpochMilli(value);
    }

    @TypeConverter
    public static Long fromInstant(Instant value) {
        return value == null ? null : value.toEpochMilli();
    }
}