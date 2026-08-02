package it.niedermann.nextcloud.deck.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
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
    account: Account?,
    userId: User.ID,
    size: Dp,
    modifier: Modifier = Modifier
) {
    var bitmap by remember(account, userId) { mutableStateOf<ImageBitmap?>(null) }
    val density = LocalDensity.current
    val sizeInPx = with(density) { size.toPx() }.toInt()

    LaunchedEffect(account, userId, sizeInPx) {
        if (account == null) return@LaunchedEffect
        try {
            val useCase = AvatarProvider.get()
            val avatar = useCase.execute(account, userId, sizeInPx).await()
            withContext(Dispatchers.IO) {
                val b = BitmapFactory.decodeByteArray(avatar.content, 0, avatar.content.size)
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
            Icons.Outlined.AccountCircle,
            contentDescription = null,
            modifier = modifier.size(size),
            tint = androidx.compose.material3.MaterialTheme.colorScheme.primary
        )
    }
}
