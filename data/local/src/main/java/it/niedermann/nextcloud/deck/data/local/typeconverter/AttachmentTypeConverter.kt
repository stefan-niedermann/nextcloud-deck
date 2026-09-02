package it.niedermann.nextcloud.deck.data.local.typeconverter

import androidx.room3.ColumnTypeConverter
import it.niedermann.nextcloud.deck.data.shared.AttachmentType

class AttachmentTypeConverter {
    @ColumnTypeConverter
    fun fromString(value: String?): AttachmentType? {
        return value?.let { AttachmentType.findByValue(it) }
    }

    @ColumnTypeConverter
    fun toString(type: AttachmentType?): String? {
        return type?.value
    }
}
