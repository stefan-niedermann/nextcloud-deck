package it.niedermann.nextcloud.deck.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import it.niedermann.nextcloud.deck.domain.model.Account
import it.niedermann.nextcloud.deck.domain.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext

@Composable
fun UserAvatar(
    accountId: Account.ID?,
    userId: User.ID,
    size: Dp,
    modifier: Modifier = Modifier
) {
    var bitmap by remember(accountId, userId) { mutableStateOf<ImageBitmap?>(null) }
    val density = LocalDensity.current
    val sizeInPx = with(density) { size.toPx() }.toInt()

    LaunchedEffect(accountId, userId, sizeInPx) {
        try {
            val useCase = AvatarProvider.get()
            val inputStream = if (accountId != null) {
                useCase.execute(accountId, userId, sizeInPx).await()
            } else {
                useCase.execute(userId, sizeInPx).await()
            }
            withContext(Dispatchers.IO) {
                val b = BitmapFactory.decodeStream(inputStream)
                bitmap = b?.asImageBitmap()
            }
        } catch (e: Exception) {
            // Log error or show fallback
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!,
            contentDescription = null,
            modifier = modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Icon(
            Icons.Default.AccountCircle,
            contentDescription = null,
            modifier = modifier.size(size),
            tint = androidx.compose.material3.MaterialTheme.colorScheme.primary
        )
    }
}
