package it.niedermann.nextcloud.deck.ui.boards.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import it.niedermann.nextcloud.deck.domain.repository.MockData
import it.niedermann.nextcloud.deck.ui.util.toComposeColor
import it.niedermann.nextcloud.deck.ui.util.toDomainColor

private val predefinedColors = MockData.MOCK_COLORS.map { it.toComposeColor() }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBoardScreen(
    boardId: Long,
    onBack: () -> Unit,
    viewModel: EditBoardViewModel = hiltViewModel()
) {
    val board by viewModel.board.collectAsStateWithLifecycle()
    var title by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(predefinedColors[0]) }

    LaunchedEffect(boardId) {
        viewModel.loadBoard(boardId)
    }

    LaunchedEffect(board) {
        board?.let {
            title = it.title()
            selectedColor = it.color().toComposeColor()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Board") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            board?.let {
                                val updatedBoard = Board(
                                    it.id(),
                                    title,
                                    selectedColor.toDomainColor(),
                                    it.ownerId(),
                                    it.archived,
                                    it.permissions(),
                                    it.accountId(),
                                    it.remoteId(),
                                    it.status(),
                                    it.lastModified(),
                                    it.etag()
                                )
                                viewModel.updateBoard(updatedBoard)
                                onBack()
                            }
                        },
                        enabled = title.isNotBlank()
                    ) {
                        Icon(Icons.Outlined.Check, contentDescription = "Save")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (viewModel.isLoading && board == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (board != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Board Color",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(48.dp),
                        contentPadding = PaddingValues(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(predefinedColors) { color ->
                            ColorItem(
                                color = color,
                                isSelected = color == selectedColor,
                                onClick = { selectedColor = color }
                            )
                        }
                    }
                }
            }

            if (viewModel.error != null) {
                Text(
                    text = viewModel.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun ColorItem(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(color, shape = CircleShape)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                Icons.Outlined.Check,
                contentDescription = null,
                tint = if (color.luminance() > 0.5f) Color.Black else Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

private fun Color.luminance(): Float {
    return 0.2126f * red + 0.7152f * green + 0.0722f * blue
}
