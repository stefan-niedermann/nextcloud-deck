package it.niedermann.nextcloud.deck.ui.accounts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.niedermann.nextcloud.deck.domain.model.Account
import it.niedermann.nextcloud.deck.domain.model.User
import it.niedermann.nextcloud.deck.ui.components.UserAvatar

@Composable
fun AccountDialog(
    onDismiss: () -> Unit,
    onAddAccount: () -> Unit,
    viewModel: AccountViewModel = hiltViewModel()
) {
    val accounts by viewModel.accounts.collectAsState()
    val currentAccountId by viewModel.currentAccountId.collectAsState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Accounts") },
        text = {
            Column {
                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                    // Sort so current is at top
                    val sortedAccounts = accounts.sortedByDescending { it.id() == currentAccountId }
                    items(sortedAccounts) { account ->
                        AccountItem(
                            account = account,
                            isCurrent = account.id() == currentAccountId,
                            onClick = {
                                viewModel.switchAccount(account.id())
                                onDismiss()
                            },
                            onDelete = {
                                viewModel.deleteAccount(account.id())
                            }
                        )
                    }
                }
                TextButton(
                    onClick = onAddAccount,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Account")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun AccountItem(
    account: Account,
    isCurrent: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    ListItem(
        headlineContent = { Text(account.accountName()) },
        supportingContent = { Text(account.url().toString()) },
        leadingContent = {
            UserAvatar(
                accountId = account.id(),
                userId = User.ID(account.username()),
                size = 40.dp
            )
        },
        trailingContent = {
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        },
        colors = if (isCurrent) {
            ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        } else {
            ListItemDefaults.colors()
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}
