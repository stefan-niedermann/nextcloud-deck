package it.niedermann.nextcloud.deck.ui.board

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.niedermann.nextcloud.deck.domain.model.Card
import it.niedermann.nextcloud.deck.domain.model.Column
import it.niedermann.nextcloud.deck.ui.util.toComposeColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardScreen(
    boardId: Long,
    onCardClick: (Long) -> Unit,
    viewModel: BoardViewModel = hiltViewModel()
) {
    val columns by viewModel.columns.collectAsState()
    val cardsByColumn by viewModel.cardsByColumn.collectAsState()
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
                    items(columns) { column ->
                        BoardColumn(
                            column = column,
                            cards = cardsByColumn[column.id.value()] ?: emptyList(),
                            onCardClick = onCardClick,
                            onAddCardClick = { showAddCardDialog = column.id.value() }
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
    onCardClick: (Long) -> Unit,
    onAddCardClick: () -> Unit
) {
    Card(
        modifier = Modifier.width(300.dp).fillMaxHeight(),
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
                items(cards) { card ->
                    CardItem(card = card, onClick = { onCardClick(card.id.value()) })
                }
            }
        }
    }
}

@Composable
fun CardItem(card: Card, onClick: () -> Unit) {
    val cardColor = card.color()?.toComposeColor() ?: MaterialTheme.colorScheme.surface
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Text(
            text = card.title(),
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
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
