package it.niedermann.nextcloud.deck.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
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
import it.niedermann.nextcloud.deck.domain.model.User
import it.niedermann.nextcloud.deck.ui.accounts.AccountDialog
import it.niedermann.nextcloud.deck.ui.accounts.AccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    onAddAccount: () -> Unit,
    onCardClick: (Long) -> Unit,
    searchViewModel: SearchViewModel = hiltViewModel(),
    accountViewModel: AccountViewModel = hiltViewModel()
) {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    var showAccountDialog by remember { mutableStateOf(false) }
    val results by searchViewModel.results.collectAsState()
    val accounts by accountViewModel.accounts.collectAsState()
    val currentAccountId by accountViewModel.currentAccountId.collectAsState()
    val currentAccount = accounts.find { it.id() == currentAccountId }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchBar(
            query = query,
            onQueryChange = { 
                query = it
                searchViewModel.search(it)
            },
            onSearch = { 
                searchViewModel.search(it)
            },
            active = active,
            onActiveChange = { active = it },
            placeholder = { Text("Search cards...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.weight(1f)
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(results) { card ->
                    ListItem(
                        headlineContent = { Text(card.title()) },
                        supportingContent = { Text(card.description().take(50)) },
                        modifier = Modifier.clickable {
                            onCardClick(card.id().value())
                            active = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(onClick = { showAccountDialog = true }) {
            if (currentAccount != null) {
                UserAvatar(
                    accountId = currentAccount.id(),
                    userId = User.ID(currentAccount.username()),
                    size = 40.dp
                )
            } else {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = "Accounts",
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }

    if (showAccountDialog) {
        AccountDialog(
            onDismiss = { showAccountDialog = false },
            onAddAccount = {
                showAccountDialog = false
                onAddAccount()
            }
        )
    }
}
