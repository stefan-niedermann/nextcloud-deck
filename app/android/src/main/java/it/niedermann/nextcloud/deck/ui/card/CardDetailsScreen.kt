package it.niedermann.nextcloud.deck.ui.card

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.niedermann.nextcloud.deck.domain.model.Card
import it.niedermann.nextcloud.deck.ui.components.UserAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailsScreen(
    cardId: Long,
    onBack: () -> Unit,
    viewModel: CardDetailsViewModel = hiltViewModel()
) {
    val card by viewModel.card.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Details", "Attachments", "Comments", "Activity")
    val icons = listOf(Icons.Default.Info, Icons.Default.Attachment, Icons.Default.Comment, Icons.Default.History)

    LaunchedEffect(cardId) {
        viewModel.loadCard(cardId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(card?.title() ?: "Card Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) },
                        icon = { Icon(icons[index], contentDescription = title) }
                    )
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (viewModel.isLoading && card == null) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    when (selectedTab) {
                        0 -> CardDetailsTab(card)
                        1 -> AttachmentsTab(viewModel)
                        2 -> CommentsTab(viewModel)
                        3 -> ActivityTab(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun CardDetailsTab(card: Card?) {
    if (card == null) return
    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("Description", style = MaterialTheme.typography.titleMedium)
            Text(card.description().ifBlank { "No description" }, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun AttachmentsTab(viewModel: CardDetailsViewModel) {
    val attachments by viewModel.attachments.collectAsState()
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(attachments) { attachment ->
            ListItem(headlineContent = { Text(attachment.filename()) })
        }
    }
}

@Composable
fun CommentsTab(viewModel: CardDetailsViewModel) {
    val comments by viewModel.comments.collectAsState()
    val card by viewModel.card.collectAsState()
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(comments) { comment ->
            ListItem(
                headlineContent = { Text(comment.message()) },
                supportingContent = { Text(comment.author().value()) },
                leadingContent = {
                    UserAvatar(
                        accountId = card?.id()?.let { null }, // We don't have boardId here easily, but accountId is null for "current account" context if needed
                        userId = comment.author(),
                        size = 32.dp
                    )
                }
            )
        }
    }
}

@Composable
fun ActivityTab(viewModel: CardDetailsViewModel) {
    val activities by viewModel.activities.collectAsState()
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(activities) { activity ->
            ListItem(headlineContent = { Text(activity.subject()) })
        }
    }
}
