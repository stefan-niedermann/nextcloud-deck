package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.converter;

import androidx.annotation.Nullable;
import androidx.room.TypeConverter;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.model.enums.EAttachmentType;
import it.niedermann.nextcloud.deck.model.enums.EDueType;
import it.niedermann.nextcloud.deck.model.enums.ESortCriteria;
import it.niedermann.nextcloud.deck.model.widget.filter.EWidgetType;

public class EnumConverter {
    // #### EWidgetType
    @TypeConverter
    public static EWidgetType toWidgetTypeEnum(Integer value) {
        try {
            return value == null ? null : EWidgetType.findById(value);
        } catch (IllegalArgumentException e) {
            DeckLog.error(EWidgetType.class.getSimpleName(), value, "not found. Falling back to generic", EWidgetType.FILTER_WIDGET);
            return EWidgetType.FILTER_WIDGET;
        }
    }

    @TypeConverter
    public static Integer fromWidgetTypeEnum(EWidgetType value) {
        return value == null ? null : value.getId();
    }

    // #### EDueType
    @TypeConverter
    @Nullable
    public static EDueType toDueTypeEnum(@Nullable Integer value) {
        return value == null ? null : EDueType.findById(value);
    }

    @TypeConverter
    @Nullable
    public static Integer fromDueTypeEnum(@Nullable EDueType value) {
        return value == null ? null : value.getId();
    }

    // #### ESortCriteria
    @TypeConverter
    @Nullable
    public static ESortCriteria toSortCriteriaEnum(@Nullable Integer value) {
        return value == null ? null : ESortCriteria.findById(value);
    }

    @TypeConverter
    @Nullable
    public static Integer fromSortCriteriaEnum(@Nullable ESortCriteria value) {
        return value == null ? null : value.getId();
    }

    // #### EAttachmentType
    @TypeConverter
    @Nullable
    public static EAttachmentType toEAttachmentType(@Nullable String value) {
        return value == null ? null : EAttachmentType.findByValue(value);
    }

    @TypeConverter
    @Nullable
    public static String fromEAttachmentType(@Nullable EAttachmentType value) {
        return value == null ? null : value.getValue();
    }
}
