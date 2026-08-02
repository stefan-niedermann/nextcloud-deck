package it.niedermann.nextcloud.deck.data.local.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "Permission")
data class PermissionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long
)
