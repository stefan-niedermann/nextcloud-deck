package it.niedermann.nextcloud.deck.persistence.sync.adapters.db;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.util.Date;

public class DateTypeConverter {

    @Deprecated
    @TypeConverter
    public static Date toDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @Deprecated
    @TypeConverter
    public static Long fromDate(Date value) {
        return value == null ? null : value.getTime();
    }

    @TypeConverter
    public static Instant toInstant(Long value) {
        return value == null ? null : Instant.ofEpochMilli(value);
    }

    @TypeConverter
    public static Long fromInstant(Instant value) {
        return value == null ? null : value.toEpochMilli();
    }
}