package it.niedermann.nextcloud.deck.ui.board

import android.content.ClipData
import android.view.View
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.niedermann.nextcloud.deck.domain.model.Account
import it.niedermann.nextcloud.deck.domain.model.Card
import it.niedermann.nextcloud.deck.domain.model.Column
import it.niedermann.nextcloud.deck.domain.model.Label
import it.niedermann.nextcloud.deck.ui.components.UserAvatar
import it.niedermann.nextcloud.deck.ui.util.toComposeColor
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardScreen(
    boardId: Long,
    onCardClick: (Long) -> Unit,
    viewModel: BoardViewModel = hiltViewModel()
) {
    val columns by viewModel.columns.collectAsState()
    val cardsByColumn by viewModel.cardsByColumn.collectAsState()
    val labels by viewModel.labels.collectAsState()
    val currentAccountId by viewModel.currentAccountId.collectAsState()
    var showAddCardDialog by remember { mutableStateOf<Long?>(null) }
    var showAddColumnDialog by remember { mutableStateOf(false) }

    LaunchedEffect(boardId) {
        viewModel.loadBoard(boardId)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddColumnDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Column")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (viewModel.isLoading && columns.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (columns.isEmpty()) {
                Text(
                    text = "No columns. Create one!",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(columns) { _, column ->
                        BoardColumn(
                            column = column,
                            cards = cardsByColumn[column.id.value()] ?: emptyList(),
                            labels = labels,
                            currentAccountId = currentAccountId,
                            draggingCardId = viewModel.draggingCardId,
                            dropTargetColumnId = viewModel.dropTargetColumnId,
                            dropTargetIndex = viewModel.dropTargetIndex,
                            onCardClick = onCardClick,
                            onAddCardClick = { showAddCardDialog = column.id.value() },
                            onDragStart = { viewModel.draggingCardId = it },
                            onDragOver = { colId, index ->
                                viewModel.dropTargetColumnId = colId
                                viewModel.dropTargetIndex = index
                            },
                            onDrop = { cardId, colId, index ->
                                viewModel.moveCard(cardId, colId, index)
                                viewModel.draggingCardId = null
                                viewModel.dropTargetColumnId = null
                                viewModel.dropTargetIndex = -1
                            }
                        )
                    }
                }
            }

            if (viewModel.error != null) {
                Box(modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)) {
                    Text(viewModel.error!!, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    if (showAddColumnDialog) {
        AddColumnDialog(
            onDismiss = { showAddColumnDialog = false },
            onConfirm = { title ->
                viewModel.addColumn(boardId, title)
                showAddColumnDialog = false
            }
        )
    }

    if (showAddCardDialog != null) {
        AddCardDialog(
            onDismiss = { showAddCardDialog = null },
            onConfirm = { title ->
                viewModel.addCard(showAddCardDialog!!, title)
                showAddCardDialog = null
            }
        )
    }
}

@Composable
fun BoardColumn(
    column: Column,
    cards: List<Card>,
    labels: Map<Long, Label>,
    currentAccountId: Account.ID?,
    draggingCardId: Card.ID?,
    dropTargetColumnId: Column.ID?,
    dropTargetIndex: Int,
    onCardClick: (Long) -> Unit,
    onAddCardClick: () -> Unit,
    onDragStart: (Card.ID) -> Unit,
    onDragOver: (Column.ID, Int) -> Unit,
    onDrop: (Card.ID, Column.ID, Int) -> Unit
) {
    val isTarget = dropTargetColumnId == column.id

    Card(
        modifier = Modifier
            .width(300.dp)
            .fillMaxHeight()
            .dragAndDropTarget(
                shouldStartDragAndDrop = { event ->
                    event.mimeTypes().contains("text/plain")
                },
                target = remember(column.id, cards.size) {
                    object : DragAndDropTarget {
                        override fun onEntered(event: DragAndDropEvent) {
                            onDragOver(column.id, cards.size)
                        }

                        override fun onMoved(event: DragAndDropEvent) {
                            onDragOver(column.id, cards.size)
                        }

                        override fun onExited(event: DragAndDropEvent) {
                        }

                        override fun onDrop(event: DragAndDropEvent): Boolean {
                            val cardIdStr =
                                event.toAndroidDragEvent().clipData.getItemAt(0).text.toString()
                            val cardId = Card.ID(cardIdStr.toLong())
                            onDrop(cardId, column.id, cards.size)
                            return true
                        }
                    }
                }
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = column.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(8.dp)
                )
                IconButton(onClick = onAddCardClick) {
                    Icon(Icons.Default.Add, contentDescription = "Add Card")
                }
            }
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(cards) { index, card ->
                    if (isTarget && dropTargetIndex == index) {
                        PlaceholderCard()
                    }
                    CardItem(
                        card = card,
                        labels = labels,
                        currentAccountId = currentAccountId,
                        isDragging = draggingCardId == card.id,
                        onDragStart = { onDragStart(card.id) },
                        onClick = { onCardClick(card.id.value()) }
                    )
                }
                if (isTarget && dropTargetIndex >= cards.size) {
                    item { PlaceholderCard() }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CardItem(
    card: Card,
    labels: Map<Long, Label>,
    currentAccountId: Account.ID?,
    isDragging: Boolean,
    onDragStart: () -> Unit,
    onClick: () -> Unit
) {
    val cardColor = card.color()?.toComposeColor() ?: MaterialTheme.colorScheme.surface
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .dragAndDropSource { _ ->
                onDragStart()
                DragAndDropTransferData(
                    ClipData.newPlainText("card_id", card.id().value().toString()),
                    flags = View.DRAG_FLAG_GLOBAL
                )
            }
            .graphicsLayer {
                alpha = if (isDragging) 0.5f else 1.0f
            }
    ) {
        Card(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = cardColor)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = card.title(),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    if (card.dueDate() != null) {
                        val locale = LocalConfiguration.current.locales[0]
                        val formatter = remember(locale) { DateTimeFormatter.ofPattern("MMM dd", locale) }
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.extraSmall,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = card.dueDate().format(formatter),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }

                if (card.labels().isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        card.labels().forEach { labelId ->
                            labels[labelId.value()]?.let { LabelChip(it) }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Comments
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.ModeComment,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "0", // TODO mock Comments count
                                modifier = Modifier.padding(start = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        // Tasks
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.CheckBox,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "0/0", // TODO mock Tasks count
                                modifier = Modifier.padding(start = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        // Attachments
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.Attachment,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "0", // TODO mock Attachments count
                                modifier = Modifier.padding(start = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (card.assignees().isNotEmpty()) {
                        Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                            card.assignees().take(3).forEach { userId ->
                                UserAvatar(
                                    accountId = currentAccountId,
                                    userId = userId,
                                    size = 24.dp,
                                    modifier = Modifier.border(1.dp, Color.White, CircleShape)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LabelChip(label: Label) {
    Surface(
        color = label.color().toComposeColor().copy(alpha = 0.2f),
        contentColor = label.color().toComposeColor(),
        shape = MaterialTheme.shapes.extraSmall,
    ) {
        Text(
            text = label.title(),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun PlaceholderCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        border = BorderStroke(
            2.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {}
}

@Composable
fun AddColumnDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var title by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Column") },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(title) }, enabled = title.isNotBlank()) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddCardDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var title by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Card") },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(title) }, enabled = title.isNotBlank()) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
