package it.niedermann.nextcloud.deck.ui.card

import android.content.ActivityNotFoundException
import android.content.Intent
import android.provider.ContactsContract
import android.text.format.DateUtils
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.niedermann.nextcloud.deck.domain.model.Card
import it.niedermann.nextcloud.deck.domain.model.Comment
import it.niedermann.nextcloud.deck.domain.model.Label
import it.niedermann.nextcloud.deck.domain.model.User
import it.niedermann.nextcloud.deck.ui.components.UserAvatar
import it.niedermann.nextcloud.deck.ui.util.LocalColorUtil
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
    val icons = listOf(Icons.Outlined.Info, Icons.Outlined.AttachFile, Icons.Outlined.ModeComment, Icons.Outlined.Bolt)
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let { viewModel.addAttachment(it) } }
    )
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> uri?.let { viewModel.addAttachment(it) } }
    )
    val contactPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                result.data?.data?.let { viewModel.addAttachment(it) }
            }
        }
    )

    var fabExpanded by remember { mutableStateOf(false) }

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
        floatingActionButton = {
            if (pagerState.currentPage == 1) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    FloatingActionButton(
                        onClick = { fabExpanded = !fabExpanded }
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = "Add attachment")
                    }
                    DropdownMenu(
                        expanded = fabExpanded,
                        onDismissRequest = { fabExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Photo") },
                            onClick = {
                                fabExpanded = false
                                photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                            leadingIcon = { Icon(Icons.Outlined.Image, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("File") },
                            onClick = {
                                fabExpanded = false
                                filePickerLauncher.launch("*/*")
                            },
                            leadingIcon = { Icon(Icons.Outlined.InsertDriveFile, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Contact") },
                            onClick = {
                                fabExpanded = false
                                val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI).apply {
                                    putExtra("android.intent.extra.USE_SYSTEM_CONTACTS_PICKER", true)
                                    putStringArrayListExtra(
                                        "android.intent.extra.REQUESTED_DATA_FIELDS",
                                        arrayListOf(
                                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
                                        )
                                    )
                                }
                                try {
                                    contactPickerLauncher.launch(intent)
                                } catch (_: ActivityNotFoundException) {
                                    try {
                                        contactPickerLauncher.launch(Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI))
                                    } catch (_: ActivityNotFoundException) {
                                        Toast.makeText(context, "No contacts app found", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) }
                        )
                    }
                }
            }
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
            val backgroundColor = label.color().toComposeColor()
            val foregroundColor = Color(LocalColorUtil.current.getForegroundColorForBackgroundColor(label.color().argb()))
            AssistChip(
                onClick = { onToggleLabel(label.id()) },
                label = { Text(label.title()) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = backgroundColor,
                    labelColor = foregroundColor
                ),
                border = null,
                trailingIcon = {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = "Remove",
                        modifier = Modifier.size(16.dp),
                        tint = foregroundColor
                    )
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
                    val backgroundColor = label.color().toComposeColor()
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(12.dp).background(backgroundColor, CircleShape))
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
        items(attachments, key = { it.id().value() }) { attachment ->
            ListItem(headlineContent = { Text(attachment.filename()) })
        }
    }
}

@Composable
fun CommentsTab(viewModel: CardDetailsViewModel) {
    val comments by viewModel.comments.collectAsStateWithLifecycle()
    val commentMessage by viewModel.commentMessage.collectAsStateWithLifecycle()
    val respondingTo by viewModel.respondingToComment.collectAsStateWithLifecycle()
    val editing by viewModel.editingComment.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboard.current
    val scope = rememberCoroutineScope()
    var commentToDelete by remember { mutableStateOf<Comment?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = false
        ) {
            items(comments, key = { it.id().value() }) { comment ->
                val parentComment = if (comment.parentId() != null) {
                    comments.find { it.id() == comment.parentId() }
                } else null

                CommentItem(
                    comment = comment,
                    parentComment = parentComment,
                    onReply = { viewModel.respondToComment(comment) },
                    onEdit = { viewModel.editComment(comment) },
                    onDelete = { commentToDelete = comment },
                    onCopy = {
                        scope.launch {
                            clipboardManager.setClipEntry(
                                androidx.compose.ui.platform.ClipEntry(
                                    android.content.ClipData.newPlainText(
                                        "Comment",
                                        comment.message()
                                    )
                                )
                            )
                        }
                    }
                )
            }
        }

        CommentInput(
            message = commentMessage,
            onMessageChange = viewModel::onCommentMessageChange,
            respondingTo = respondingTo,
            editing = editing,
            onCancelAction = viewModel::cancelCommentAction,
            onSubmit = viewModel::submitComment
        )
    }

    if (commentToDelete != null) {
        AlertDialog(
            onDismissRequest = { commentToDelete = null },
            title = { Text("Delete Comment") },
            text = { Text("Are you sure you want to delete this comment?") },
            confirmButton = {
                TextButton(onClick = {
                    commentToDelete?.let { viewModel.deleteComment(it.id()) }
                    commentToDelete = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { commentToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CommentItem(
    comment: Comment,
    parentComment: Comment?,
    onReply: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCopy: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        if (parentComment != null) {
            Row(
                modifier = Modifier
                    .padding(start = 56.dp, end = 16.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(24.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = parentComment.message(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontStyle = FontStyle.Italic
                )
            }
        }

        ListItem(
            headlineContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = comment.author().value(),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = DateUtils.getRelativeTimeSpanString(
                            comment.created().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                            System.currentTimeMillis(),
                            DateUtils.SECOND_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_RELATIVE
                        ).toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Respond") },
                                onClick = {
                                    onReply()
                                    showMenu = false
                                },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.Reply, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                onClick = {
                                    onEdit()
                                    showMenu = false
                                },
                                leadingIcon = { Icon(Icons.Default.Edit, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Copy") },
                                onClick = {
                                    onCopy()
                                    showMenu = false
                                },
                                leadingIcon = { Icon(Icons.Default.ContentCopy, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = {
                                    onDelete()
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Delete,
                                        null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            )
                        }
                    }
                }
            },
            supportingContent = {
                Text(
                    text = comment.message(),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
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

@Composable
fun CommentInput(
    message: String,
    onMessageChange: (String) -> Unit,
    respondingTo: Comment?,
    editing: Comment?,
    onCancelAction: () -> Unit,
    onSubmit: () -> Unit
) {
    Surface(
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            if (respondingTo != null || editing != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (respondingTo != null) Icons.AutoMirrored.Filled.Reply else Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when {
                            respondingTo != null -> "Replying to ${respondingTo.author().value()}"
                            else -> "Editing comment"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onCancelAction,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = "Cancel",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = message,
                    onValueChange = onMessageChange,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    placeholder = { Text("Add a comment...") },
                    maxLines = 5
                )
                IconButton(
                    onClick = onSubmit,
                    enabled = message.isNotBlank(),
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Icon(
                        imageVector = if (editing != null) Icons.Default.Edit else Icons.AutoMirrored.Filled.Send,
                        contentDescription = if (editing != null) "Update" else "Send",
                        tint = if (message.isNotBlank()) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun ActivityTab(viewModel: CardDetailsViewModel) {
    val activities by viewModel.activities.collectAsStateWithLifecycle()
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(activities, key = { it.id().value() }) { activity ->
            ListItem(headlineContent = { Text(activity.subject()) })
        }
    }
}
