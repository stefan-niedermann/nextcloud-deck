package it.niedermann.nextcloud.deck.ui.pickstack

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.niedermann.nextcloud.deck.domain.model.Account
import it.niedermann.nextcloud.deck.domain.model.Board
import it.niedermann.nextcloud.deck.domain.model.Card
import it.niedermann.nextcloud.deck.domain.model.Column as DeckColumn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickStackDialog(
    cardId: Card.ID,
    mode: PickStackViewModel.Mode,
    onDismiss: () -> Unit,
    viewModel: PickStackViewModel = hiltViewModel()
) {
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val boards by viewModel.boards.collectAsStateWithLifecycle()
    val columns by viewModel.columns.collectAsStateWithLifecycle()
    val selectedAccount by viewModel.selectedAccount.collectAsStateWithLifecycle()
    val selectedBoard by viewModel.selectedBoard.collectAsStateWithLifecycle()
    val selectedColumn by viewModel.selectedColumn.collectAsStateWithLifecycle()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (mode == PickStackViewModel.Mode.MOVE) "Move Card" else "Copy Card") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (accounts.size > 1) {
                    AccountDropdown(
                        accounts = accounts,
                        selectedAccount = selectedAccount,
                        onAccountSelected = { viewModel.selectAccount(it) }
                    )
                }

                BoardDropdown(
                    boards = boards,
                    selectedBoard = selectedBoard,
                    onBoardSelected = { viewModel.selectBoard(it) }
                )

                Text("Select Column:", style = MaterialTheme.typography.labelMedium)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                ) {
                    items(columns) { column ->
                        ColumnItem(
                            column = column,
                            isSelected = selectedColumn == column,
                            onClick = { viewModel.selectColumn(column) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { viewModel.performAction(cardId, mode, onDismiss) },
                enabled = selectedColumn != null
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDropdown(
    accounts: List<Account>,
    selectedAccount: Account?,
    onAccountSelected: (Account) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedAccount?.accountName() ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Account") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            accounts.forEach { account ->
                DropdownMenuItem(
                    text = { Text(account.accountName()) },
                    onClick = {
                        onAccountSelected(account)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardDropdown(
    boards: List<Board>,
    selectedBoard: Board?,
    onBoardSelected: (Board) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedBoard?.title() ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Board") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            boards.forEach { board ->
                DropdownMenuItem(
                    text = { Text(board.title()) },
                    onClick = {
                        onBoardSelected(board)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ColumnItem(
    column: DeckColumn,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = column.title,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
