package it.niedermann.nextcloud.deck.ui.components

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.niedermann.nextcloud.deck.domain.model.User
import it.niedermann.nextcloud.deck.ui.accounts.AccountDialog
import it.niedermann.nextcloud.deck.ui.accounts.AccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    onAddAccount: () -> Unit,
    onCardClick: (Long) -> Unit,
    searchViewModel: SearchViewModel = hiltViewModel(LocalActivity.current as ComponentActivity),
    accountViewModel: AccountViewModel = hiltViewModel()
) {
    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    var showAccountDialog by remember { mutableStateOf(false) }
    val results by searchViewModel.results.collectAsStateWithLifecycle()
    val accounts by accountViewModel.accounts.collectAsStateWithLifecycle()
    val currentAccountId by accountViewModel.currentAccountId.collectAsStateWithLifecycle()
    val currentAccount = accounts.find { it.id() == currentAccountId }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = {
                        query = it
                        searchViewModel.search(it)
                    },
                    onSearch = {
                        searchViewModel.search(it)
                    },
                    expanded = active,
                    onExpandedChange = { active = it },
                    placeholder = { Text("Search cards...") },
                    leadingIcon = {
                        if (active) {
                            IconButton(onClick = { active = false }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        } else {
                            Icon(Icons.Default.Search, contentDescription = null)
                        }
                    },
                    trailingIcon = {
                        IconButton(onClick = { showAccountDialog = true }) {
                            if (currentAccount != null) {
                                UserAvatar(
                                    accountId = currentAccount.id(),
                                    userId = User.ID(currentAccount.username()),
                                    size = 30.dp
                                )
                            } else {
                                Icon(
                                    Icons.Default.AccountCircle,
                                    contentDescription = "Accounts",
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }
                )
            },
            expanded = active,
            onExpandedChange = { active = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (active) 0.dp else 16.dp, vertical = if (active) 0.dp else 8.dp)
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
