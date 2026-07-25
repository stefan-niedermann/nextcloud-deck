package it.niedermann.nextcloud.deck.ui.boards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.niedermann.nextcloud.deck.domain.model.Board
import it.niedermann.nextcloud.deck.ui.components.AppTopBar
import it.niedermann.nextcloud.deck.ui.util.toComposeColor
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardListScreen(
    onBoardClick: (Long) -> Unit,
    onEditBoardClick: (Long) -> Unit,
    onAddAccount: () -> Unit,
    onCardClick: (Long) -> Unit,
    viewModel: BoardListViewModel = hiltViewModel()
) {
    val boards by viewModel.boards.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                onAddAccount = onAddAccount,
                onCardClick = onCardClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Outlined.Add, contentDescription = "Add Board")
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (viewModel.isLoading && boards.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (boards.isEmpty()) {
                Text(
                    text = "No boards found. Create one!",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(boards) { _, board ->
                        BoardItem(
                            board = board,
                            onClick = { onBoardClick(board.id.value()) },
                            onEditClick = { onEditBoardClick(board.id.value()) }
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

    if (showAddDialog) {
        AddBoardDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title ->
                viewModel.addBoard(title)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun BoardItem(board: Board, onClick: () -> Unit, onEditClick: () -> Unit) {
    val formatter = remember { DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM) }
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        ListItem(
            headlineContent = { Text(board.title) },
            supportingContent = {
                Text(
                    text = "Last edited: ${board.editedAt().format(formatter)}",
                    style = MaterialTheme.typography.bodySmall
                )
            },
            leadingContent = {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(board.color().toComposeColor(), shape = CircleShape)
                )
            },
            trailingContent = {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Edit Board")
                }
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

@Composable
fun AddBoardDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var title by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Board") },
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
