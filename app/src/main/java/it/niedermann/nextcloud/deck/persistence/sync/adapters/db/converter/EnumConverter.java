package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.converter;

import androidx.room.TypeConverter;

import it.niedermann.nextcloud.deck.model.enums.EDueType;
import it.niedermann.nextcloud.deck.model.widget.filter.EWidgetType;

public class EnumConverter {
    // #### EWidgetType
    @TypeConverter
    public static EWidgetType toWidgetTypeEnum(Integer value) {
        return value == null ? null : EWidgetType.findById(value);
    }

    @TypeConverter
    public static Integer fromWidgetTypeEnum(EWidgetType value) {
        return value == null ? null : value.getId();
    }

    // #### EDueType
    @TypeConverter
    public static EDueType toDueTypeEnum(Integer value) {
        return value == null ? null : EDueType.findById(value);
    }

    @TypeConverter
    public static int fromDueTypeEnum(EDueType value) {
        return value == null ? null : value.getId();
    }
}
