package it.niedermann.nextcloud.deck.persistence.sync.adapters.db;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateTypeConverter {

    @TypeConverter
    public static Date toDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long toLong(Date value) {
        return value == null ? null : value.getTime();
    }
}