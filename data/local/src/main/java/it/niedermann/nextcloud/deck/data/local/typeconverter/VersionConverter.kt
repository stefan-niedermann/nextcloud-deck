package it.niedermann.nextcloud.deck.data.local.typeconverter

import androidx.room3.ColumnTypeConverter
import it.niedermann.nextcloud.deck.domain.model.Version

class VersionConverter {
    @ColumnTypeConverter
    fun fromString(value: String?): Version? {
        return value?.let { Version.parse(it) }
    }

    @ColumnTypeConverter
    fun toString(version: Version?): String? {
        return version?.toString()
    }
}
