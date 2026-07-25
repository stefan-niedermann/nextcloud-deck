package it.niedermann.nextcloud.deck.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Attachment
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.niedermann.nextcloud.deck.domain.model.Card
import it.niedermann.nextcloud.deck.domain.model.Label
import it.niedermann.nextcloud.deck.domain.model.User
import it.niedermann.nextcloud.deck.ui.components.UserAvatar
import it.niedermann.nextcloud.deck.ui.util.toComposeColor
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailsScreen(
    cardId: Long,
    onBack: () -> Unit,
    viewModel: CardDetailsViewModel = hiltViewModel()
) {
    val card by viewModel.card.collectAsStateWithLifecycle()
    val tabs = listOf("Details", "Attachments", "Comments", "Activity")
    val icons = listOf(Icons.Outlined.Info, Icons.Outlined.Attachment, Icons.Outlined.ModeComment, Icons.Outlined.Bolt)
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(cardId) {
        viewModel.loadCard(cardId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(card?.title() ?: "Card Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            SecondaryTabRow(selectedTabIndex = pagerState.currentPage) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { 
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = { Text(title) },
                        icon = { Icon(icons[index], contentDescription = title) }
                    )
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (viewModel.isLoading && card == null) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.Top
                    ) { page ->
                        when (page) {
                            0 -> CardDetailsTab(card, viewModel)
                            1 -> AttachmentsTab(viewModel)
                            2 -> CommentsTab(viewModel)
                            3 -> ActivityTab(viewModel)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CardDetailsTab(card: Card?, viewModel: CardDetailsViewModel) {
    if (card == null) return
    var isEditingDescription by remember { mutableStateOf(false) }
    var descriptionText by remember(card.description()) { mutableStateOf(card.description()) }
    val boardLabels by viewModel.boardLabels.collectAsStateWithLifecycle()
    val userSearchResults by viewModel.userSearchResults.collectAsStateWithLifecycle()

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Labels
        item {
            Text("Labels", style = MaterialTheme.typography.titleMedium)
            LabelSelector(
                selectedLabels = card.labels(),
                availableLabels = boardLabels,
                onToggleLabel = viewModel::toggleLabel
            )
        }

        // Assignees
        item {
            Text("Assignees", style = MaterialTheme.typography.titleMedium)
            AssigneeSelector(
                selectedAssignees = card.assignees(),
                searchResults = userSearchResults,
                onSearchQueryChange = viewModel::onUserSearchQueryChange,
                onToggleAssignee = viewModel::toggleAssignee
            )
        }

        // Dates
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                DateTimeInput(
                    label = "Start Date",
                    dateTime = card.startDate(),
                    onDateTimeChange = { viewModel.updateCardDates(it, card.dueDate()) },
                    modifier = Modifier.fillMaxWidth()
                )
                DateTimeInput(
                    label = "Due Date",
                    dateTime = card.dueDate(),
                    onDateTimeChange = { viewModel.updateCardDates(card.startDate(), it) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Description
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Description", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                IconButton(onClick = { isEditingDescription = !isEditingDescription }) {
                    Icon(
                        if (isEditingDescription) Icons.Outlined.Visibility else Icons.Outlined.Edit,
                        contentDescription = "Toggle View/Edit"
                    )
                }
            }
            if (isEditingDescription) {
                OutlinedTextField(
                    value = descriptionText,
                    onValueChange = { 
                        descriptionText = it
                        viewModel.updateCardDescription(it)
                    },
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    placeholder = { Text("Add a description...") }
                )
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        card.description().ifBlank { "No description" },
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LabelSelector(
    selectedLabels: Set<Label.ID>,
    availableLabels: List<Label>,
    onToggleLabel: (Label.ID) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabelsList = availableLabels.filter { selectedLabels.contains(it.id()) }

    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        selectedLabelsList.forEach { label ->
            AssistChip(
                onClick = { onToggleLabel(label.id()) },
                label = { Text(label.title()) },
                leadingIcon = {
                    Box(Modifier.size(12.dp).background(label.color().toComposeColor(), CircleShape))
                },
                trailingIcon = {
                    Icon(Icons.Outlined.Close, contentDescription = "Remove", Modifier.size(16.dp))
                }
            )
        }
        Box {
            AssistChip(
                onClick = { expanded = true },
                label = { Text("Add label") },
                leadingIcon = { Icon(Icons.Outlined.Add, contentDescription = null, Modifier.size(16.dp)) }
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                availableLabels.forEach { label ->
                    val isSelected = selectedLabels.contains(label.id())
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(12.dp).background(label.color().toComposeColor(), CircleShape))
                                Spacer(Modifier.width(8.dp))
                                Text(label.title())
                            }
                        },
                        onClick = {
                            onToggleLabel(label.id())
                            expanded = false
                        },
                        trailingIcon = if (isSelected) {
                            { Icon(Icons.Outlined.Close, contentDescription = null) }
                        } else null
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AssigneeSelector(
    selectedAssignees: Set<User.ID>,
    searchResults: Collection<User>,
    onSearchQueryChange: (String) -> Unit,
    onToggleAssignee: (User.ID) -> Unit
) {
    var isSearching by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            selectedAssignees.forEach { userId ->
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape,
                    modifier = Modifier.clickable { onToggleAssignee(userId) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 4.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
                    ) {
                        UserAvatar(accountId = null, userId = userId, size = 24.dp)
                        Spacer(Modifier.width(8.dp))
                        Text(userId.value(), style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.width(4.dp))
                        Icon(Icons.Outlined.Close, contentDescription = "Remove", Modifier.size(14.dp))
                    }
                }
            }
            AssistChip(
                onClick = { 
                    isSearching = !isSearching
                },
                label = { Text("Assign user") },
                leadingIcon = { Icon(Icons.Outlined.Add, contentDescription = null, Modifier.size(16.dp)) }
            )
        }

        if (isSearching) {
            var expanded by remember { mutableStateOf(false) }
            
            LaunchedEffect(isSearching) {
                if (isSearching) {
                    focusRequester.requestFocus()
                }
            }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        onSearchQueryChange(it)
                        expanded = it.isNotBlank()
                    },
                    modifier = Modifier.fillMaxWidth().menuAnchor(androidx.compose.material3.ExposedDropdownMenuAnchorType.PrimaryEditable).focusRequester(focusRequester),
                    label = { Text("Search users...") },
                    trailingIcon = { 
                        IconButton(onClick = { 
                            isSearching = false 
                            query = ""
                            onSearchQueryChange("")
                        }) {
                            Icon(Icons.Outlined.Close, contentDescription = "Close search")
                        }
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )

                if (searchResults.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        searchResults.forEach { user ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        UserAvatar(accountId = null, userId = user.id(), size = 32.dp)
                                        Spacer(Modifier.width(8.dp))
                                        Text(user.displayName())
                                    }
                                },
                                onClick = {
                                    onToggleAssignee(user.id())
                                    query = ""
                                    onSearchQueryChange("")
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimeInput(
    label: String,
    dateTime: LocalDateTime?,
    onDateTimeChange: (LocalDateTime?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dateTime?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    )
    val timePickerState = rememberTimePickerState(
        initialHour = dateTime?.hour ?: 12,
        initialMinute = dateTime?.minute ?: 0
    )

    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = dateTime?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) ?: "Not set",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { 
                    if (dateTime != null) {
                        IconButton(onClick = { onDateTimeChange(null) }) {
                            Icon(Icons.Outlined.Clear, contentDescription = "Clear")
                        }
                    } else {
                        Icon(Icons.Outlined.CalendarMonth, contentDescription = null)
                    }
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showDatePicker = true }
            )
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    showTimePicker = true
                }) { Text("Next") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val date = Instant.ofEpochMilli(datePickerState.selectedDateMillis ?: System.currentTimeMillis())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    val newDateTime = LocalDateTime.of(date, java.time.LocalTime.of(timePickerState.hour, timePickerState.minute))
                    onDateTimeChange(newDateTime)
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }
}


@Composable
fun AttachmentsTab(viewModel: CardDetailsViewModel) {
    val attachments by viewModel.attachments.collectAsStateWithLifecycle()
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(attachments) { attachment ->
            ListItem(headlineContent = { Text(attachment.filename()) })
        }
    }
}

@Composable
fun CommentsTab(viewModel: CardDetailsViewModel) {
    val comments by viewModel.comments.collectAsStateWithLifecycle()
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(comments) { comment ->
            ListItem(
                headlineContent = { Text(comment.message()) },
                supportingContent = { Text(comment.author().value()) },
                leadingContent = {
                    UserAvatar(
                        accountId = null,
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
    val activities by viewModel.activities.collectAsStateWithLifecycle()
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(activities) { activity ->
            ListItem(headlineContent = { Text(activity.subject()) })
        }
    }
}
