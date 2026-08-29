package it.niedermann.nextcloud.deck.data.local.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import it.niedermann.nextcloud.deck.domain.model.Color
import it.niedermann.nextcloud.deck.domain.model.Version
import java.net.URL

@Entity(
    tableName = "Account",
)
@JvmRecord
data class AccountEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val url: URL,
    val username: String,
    val token: String,
    val accountName: String,
    @androidx.room3.Embedded
    val capabilities: CapabilitiesEntity
)
