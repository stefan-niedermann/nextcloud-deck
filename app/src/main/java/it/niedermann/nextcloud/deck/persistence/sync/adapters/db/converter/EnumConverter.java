package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.converter;

import androidx.room.TypeConverter;

import it.niedermann.nextcloud.deck.model.enums.EDueType;
import it.niedermann.nextcloud.deck.model.widget.filter.EWidgetType;

public class EnumConverter {
    // #### EWidgetType
    @TypeConverter
    public static EWidgetType toWidgetTypeEnum(int value) {
        return EWidgetType.findById(value);
    }

    @TypeConverter
    public static int fromWidgetTypeEnum(EWidgetType value) {
        return value == null ? null : value.getId();
    }

    // #### EDueType
    @TypeConverter
    public static EDueType toDueTypeEnum(int value) {
        return EDueType.findById(value);
    }

    @TypeConverter
    public static int fromDueTypeEnum(EDueType value) {
        return value == null ? null : value.getId();
    }
}
