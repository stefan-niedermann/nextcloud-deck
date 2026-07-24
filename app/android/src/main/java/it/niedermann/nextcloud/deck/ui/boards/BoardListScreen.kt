package it.niedermann.nextcloud.deck.ui.boards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.niedermann.nextcloud.deck.domain.model.Board
import it.niedermann.nextcloud.deck.ui.util.toComposeColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardListScreen(
    onBoardClick: (Long) -> Unit,
    viewModel: BoardListViewModel = hiltViewModel()
) {
    val boards by viewModel.boards.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Board")
            }
        }
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
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(boards) { board ->
                        BoardItem(board = board, onClick = { onBoardClick(board.id.value()) })
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
fun BoardItem(board: Board, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(board.title) },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .padding(4.dp)
                    .fillMaxSize()
                    .aspectRatio(1f)
                    .background(board.color().toComposeColor(), shape = MaterialTheme.shapes.small)
            )
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
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
