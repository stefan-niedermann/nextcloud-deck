package it.niedermann.nextcloud.deck.ui.board

import android.content.ClipData
import android.content.ClipDescription
import android.view.View
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import it.niedermann.nextcloud.deck.domain.model.Account
import it.niedermann.nextcloud.deck.domain.model.Card
import it.niedermann.nextcloud.deck.domain.model.Column
import it.niedermann.nextcloud.deck.domain.model.FilterInformation
import it.niedermann.nextcloud.deck.domain.model.Label
import it.niedermann.nextcloud.deck.domain.model.User
import it.niedermann.nextcloud.deck.domain.model.query.PreviewCard
import it.niedermann.nextcloud.deck.ui.components.AppTopBar
import it.niedermann.nextcloud.deck.ui.components.UserAvatar
import it.niedermann.nextcloud.deck.ui.pickstack.PickStackDialog
import it.niedermann.nextcloud.deck.ui.pickstack.PickStackViewModel
import it.niedermann.nextcloud.deck.ui.util.LocalColorUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

private sealed class BoardItem {
    abstract val key: Any

    data class CardData(val card: PreviewCard) : BoardItem() {
        override val key: Long = card.id().value()
    }

    data object Placeholder : BoardItem() {
        override val key: String = "placeholder"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardScreen(
    boardId: Long,
    onCardClick: (Long) -> Unit,
    onAddAccount: () -> Unit,
    onGoToBoardList: () -> Unit,
    viewModel: BoardViewModel = hiltViewModel()
) {
    val columns by viewModel.columns.collectAsStateWithLifecycle()
    val cardsByColumn by viewModel.cardsByColumn.collectAsStateWithLifecycle()
    val labels by viewModel.labels.collectAsStateWithLifecycle()
    val users by viewModel.users.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()
    val viewMode by viewModel.viewMode.collectAsStateWithLifecycle()
    val currentAccount by viewModel.currentAccount.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val syncStatus by viewModel.syncStatus.collectAsStateWithLifecycle()
    val compactMode by viewModel.compactMode.collectAsStateWithLifecycle()
    val state = rememberPullToRefreshState()
    var showAddCardDialog by remember { mutableStateOf<Long?>(null) }
    var showAddColumnDialog by remember { mutableStateOf(false) }
    var showFilter by remember { mutableStateOf(false) }
    var showPickStack by remember { mutableStateOf<Pair<Card.ID, PickStackViewModel.Mode>?>(null) }

    val screenWidth = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp() }.value
    val isSmallScreen = screenWidth < 600
    val lazyListState = rememberLazyListState()
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)

    var autoScrollSpeed by remember { mutableFloatStateOf(0f) }
    var rowWidth by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val threshold = with(density) { 50.dp.toPx() }
    val onDragLocation: (Offset) -> Unit = { position ->
        val x = position.x
        autoScrollSpeed = when {
            x < threshold -> -1f * (1f - x / threshold.coerceAtLeast(1f))
            x > rowWidth - threshold -> 1f * (1f - (rowWidth - x) / threshold.coerceAtLeast(1f))
            else -> 0f
        }
    }
    val onDragExited: () -> Unit = {
        autoScrollSpeed = 0f
    }

    LaunchedEffect(autoScrollSpeed) {
        if (autoScrollSpeed != 0f) {
            while (isActive) {
                lazyListState.scrollBy(autoScrollSpeed * 10f)
                delay(16)
            }
        }
    }

    LaunchedEffect(boardId) {
        viewModel.loadBoard(boardId)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                onAddAccount = onAddAccount,
                onCardClick = onCardClick,
                onGoToBoardList = onGoToBoardList,
                extraActions = {
                    IconButton(onClick = {
                        viewModel.updateViewMode(if (viewMode == BoardViewModel.ViewMode.KANBAN) BoardViewModel.ViewMode.GANTT else BoardViewModel.ViewMode.KANBAN)
                    }) {
                        Icon(
                            imageVector = if (viewMode == BoardViewModel.ViewMode.KANBAN) Icons.Default.DateRange else Icons.AutoMirrored.Filled.List,
                            contentDescription = "Switch View"
                        )
                    }
                    IconButton(onClick = { showFilter = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddColumnDialog = true }) {
                Icon(Icons.Outlined.Add, contentDescription = "Add Column")
            }
        },
        contentWindowInsets = WindowInsets.navigationBars
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refresh() },
            state = state,
            indicator = {
                PullToRefreshDefaults.IndicatorBox(
                    state = state,
                    isRefreshing = isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                ) {
                    Crossfade(targetState = isRefreshing, label = "SyncProgress") { refreshing ->
                        if (refreshing) {
                            val currentStatus = syncStatus
                            val total = currentStatus?.boardsTotal ?: 0
                            if (currentStatus != null && total > 0) {
                                CircularProgressIndicator(
                                    progress = { currentStatus.boardsFinished.toFloat() / total },
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 3.dp,
                                )
                            } else {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 3.dp,
                                )
                            }
                        } else {
                            CircularProgressIndicator(
                                progress = { state.distanceFraction.coerceIn(0f, 1f) },
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 3.dp,
                            )
                        }
                    }
                }
            },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (viewModel.isLoading && columns.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (columns.isEmpty()) {
                    Text(
                        text = "No columns. Create one!",
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    Crossfade(targetState = viewMode, label = "ViewMode") { mode ->
                        when (mode) {
                            BoardViewModel.ViewMode.KANBAN -> {
                                LazyRow(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .onGloballyPositioned {
                                            rowWidth = it.size.width
                                        }
                                        .dragAndDropTarget(
                                            shouldStartDragAndDrop = { true },
                                            target = remember(onDragLocation, onDragExited) {
                                                object : DragAndDropTarget {
                                                    override fun onMoved(event: DragAndDropEvent) {
                                                        val dragEvent = event.toAndroidDragEvent()
                                                        onDragLocation(Offset(dragEvent.x, dragEvent.y))
                                                    }

                                                    override fun onExited(event: DragAndDropEvent) {
                                                        onDragExited()
                                                    }

                                                    override fun onEnded(event: DragAndDropEvent) {
                                                        onDragExited()
                                                    }

                                                    override fun onDrop(event: DragAndDropEvent): Boolean = false
                                                }
                                            }
                                        ),
                                    state = lazyListState,
                                    contentPadding = PaddingValues(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    flingBehavior = if (isSmallScreen) snapFlingBehavior else ScrollableDefaults.flingBehavior()
                                ) {
                                    itemsIndexed(columns, key = { _, column -> column.id.value() }) { _, column ->
                                        BoardColumn(
                                            column = column,
                                            cards = cardsByColumn[column.id.value()] ?: emptyList(),
                                            draggingCardId = viewModel.draggingCardId,
                                            dropTargetColumnId = viewModel.dropTargetColumnId,
                                            dropTargetIndex = viewModel.dropTargetIndex,
                                            onCardClick = onCardClick,
                                            onAddCardClick = { showAddCardDialog = column.id.value() },
                                            onAssignToggle = { cardId, assigned -> viewModel.toggleAssignment(cardId, assigned) },
                                            onMove = { cardId -> showPickStack = cardId to PickStackViewModel.Mode.MOVE },
                                            onCopy = { cardId -> showPickStack = cardId to PickStackViewModel.Mode.COPY },
                                            currentAccount = currentAccount,
                                            compactMode = compactMode,
                                            onDragStart = { viewModel.draggingCardId = it },
                                            onDragOver = { colId, index ->
                                                viewModel.dropTargetColumnId = colId
                                                viewModel.dropTargetIndex = index
                                            },
                                            onDrop = { cardId, colId, index ->
                                                viewModel.moveCard(cardId, colId, index)
                                                viewModel.draggingCardId = null
                                                viewModel.dropTargetColumnId = null
                                                viewModel.dropTargetIndex = -1
                                            },
                                            onDragLocation = onDragLocation,
                                            onDragExited = onDragExited
                                        )
                                    }
                                }
                            }

                            BoardViewModel.ViewMode.GANTT -> {
                                BoardGanttScreen(
                                    cards = cardsByColumn.values.flatten(),
                                    onCardClick = onCardClick
                                )
                            }
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
        }

    if (showPickStack != null) {
        PickStackDialog(
            cardId = showPickStack!!.first,
            mode = showPickStack!!.second,
            onDismiss = { showPickStack = null }
        )
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

    if (showFilter) {
        FilterBottomSheet(
            availableLabels = labels.values.toList(),
            availableUsers = users,
            filter = filter,
            onApply = { viewModel.updateFilter(it) },
            onReset = { viewModel.updateFilter(FilterInformation.EMPTY) },
            onDismiss = { showFilter = false }
        )
    }
}

@Composable
fun BoardColumn(
    column: Column,
    cards: List<PreviewCard>,
    currentAccount: Account?,
    compactMode: Boolean,
    draggingCardId: Card.ID?,
    dropTargetColumnId: Column.ID?,
    dropTargetIndex: Int,
    onCardClick: (Long) -> Unit,
    onAddCardClick: () -> Unit,
    onAssignToggle: (Card.ID, Boolean) -> Unit,
    onMove: (Card.ID) -> Unit,
    onCopy: (Card.ID) -> Unit,
    onDragStart: (Card.ID) -> Unit,
    onDragOver: (Column.ID, Int) -> Unit,
    onDrop: (Card.ID, Column.ID, Int) -> Unit,
    onDragLocation: (Offset) -> Unit,
    onDragExited: () -> Unit
) {
    val isTarget = dropTargetColumnId == column.id
    var lazyColumnCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val lazyListState = rememberLazyListState()

    // Filter out the card currently being dragged to avoid layout shifts during reordering
    val activeCards = remember(cards, draggingCardId) {
        cards.filter { it.id() != draggingCardId }
    }

    val columnContent = remember(activeCards, isTarget, dropTargetIndex) {
        buildList {
            activeCards.forEachIndexed { index, card ->
                if (isTarget && dropTargetIndex == index) {
                    add(BoardItem.Placeholder)
                }
                add(BoardItem.CardData(card))
            }
            if (isTarget && dropTargetIndex >= activeCards.size) {
                add(BoardItem.Placeholder)
            }
        }
    }

    Column(
        modifier = Modifier
            .width(maxOf(LocalConfiguration.current.screenWidthDp.dp - 36.dp, 300.dp))
            .fillMaxHeight()
            .dragAndDropTarget(
                shouldStartDragAndDrop = { event ->
                    event.mimeTypes().contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                },
                target = remember(column.id, activeCards, lazyColumnCoordinates, lazyListState, onDragOver, onDrop, onDragLocation, onDragExited) {
                    object : DragAndDropTarget {
                        private fun calculateTargetIndex(event: DragAndDropEvent): Int {
                            val dragEvent = event.toAndroidDragEvent()
                            val rootDragY = dragEvent.y

                            val lazyColumnRootPos = lazyColumnCoordinates?.positionInRoot() ?: Offset.Zero
                            val relativeDragY = rootDragY - lazyColumnRootPos.y

                            val layoutInfo = lazyListState.layoutInfo
                            val visibleItems = layoutInfo.visibleItemsInfo

                            // Find placeholder info if present to compensate for its layout shift
                            val placeholderItem = visibleItems.find { it.key == "placeholder" }
                            val placeholderHeightWithSpacing = if (placeholderItem != null) {
                                placeholderItem.size + layoutInfo.mainAxisItemSpacing
                            } else 0

                            // Filter for cards to find the insertion point relative to them
                            val visibleCards = visibleItems.filter { it.key is Long }

                            var targetIndex = activeCards.size

                            for (item in visibleCards) {
                                var itemCenter = item.offset + item.size / 2

                                // If the card is below the placeholder, it has been shifted down.
                                // We subtract the placeholder's height to find its "natural" trigger point.
                                if (placeholderItem != null && item.offset > placeholderItem.offset) {
                                    itemCenter -= placeholderHeightWithSpacing
                                }

                                if (relativeDragY < itemCenter) {
                                    val indexInCards = activeCards.indexOfFirst { it.id().value() == item.key }
                                    if (indexInCards != -1) {
                                        targetIndex = indexInCards
                                        break
                                    }
                                }
                            }
                            return targetIndex
                        }

                        override fun onEntered(event: DragAndDropEvent) {
                            val targetIndex = calculateTargetIndex(event)
                            onDragOver(column.id, targetIndex)

                            val dragEvent = event.toAndroidDragEvent()
                            onDragLocation(Offset(dragEvent.x, dragEvent.y))
                        }

                        override fun onMoved(event: DragAndDropEvent) {
                            val targetIndex = calculateTargetIndex(event)
                            onDragOver(column.id, targetIndex)

                            val dragEvent = event.toAndroidDragEvent()
                            onDragLocation(Offset(dragEvent.x, dragEvent.y))
                        }

                        override fun onExited(event: DragAndDropEvent) {
                            onDragExited()
                        }

                        override fun onDrop(event: DragAndDropEvent): Boolean {
                            onDragExited()
                            val cardIdStr =
                                event.toAndroidDragEvent().clipData.getItemAt(0).text.toString()
                            val cardId = Card.ID(cardIdStr.toLong())

                            val targetIndex = calculateTargetIndex(event)
                            onDrop(cardId, column.id, targetIndex)
                            return true
                        }
                    }
                }
            )
            .padding(8.dp)
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
                    Icon(Icons.Outlined.Add, contentDescription = "Add Card")
                }
            }
        }
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .onGloballyPositioned { lazyColumnCoordinates = it },
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(columnContent, key = { it.key }) { item ->
                when (item) {
                    is BoardItem.CardData -> {
                        CardItem(
                            card = item.card,
                            currentAccount = currentAccount,
                            compactMode = compactMode,
                            isDragging = draggingCardId == item.card.id(),
                            onDragStart = { onDragStart(item.card.id()) },
                            onAssignToggle = { onAssignToggle(item.card.id(), item.card.assignedToMe()) },
                            onMove = { onMove(item.card.id()) },
                            onCopy = { onCopy(item.card.id()) },
                            onClick = { onCardClick(item.card.id().value()) }
                        )
                    }

                    BoardItem.Placeholder -> {
                        PlaceholderCard(column.id.value())
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CardItem(
    card: PreviewCard,
    currentAccount: Account?,
    compactMode: Boolean,
    isDragging: Boolean,
    onDragStart: () -> Unit,
    onAssignToggle: () -> Unit,
    onMove: () -> Unit,
    onCopy: () -> Unit,
    onClick: () -> Unit
) {
    val colorUtil = LocalColorUtil.current
    val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
    val harmonizedColor = remember(card.color(), primaryColor) {
        card.color()?.let { colorUtil.harmonize(it.argb(), primaryColor) }
    }
    val containerColor = if (harmonizedColor != null) Color(harmonizedColor) else MaterialTheme.colorScheme.surface
    val contentColor = if (harmonizedColor != null) {
        Color(colorUtil.getForegroundColorForBackgroundColor(harmonizedColor))
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val interactionSource = remember { MutableInteractionSource() }
    val scope = rememberCoroutineScope()
    var showMenu by remember { mutableStateOf(false) }

    val isDarkTheme = isSystemInDarkTheme()
    val secondaryContentColor = if (harmonizedColor != null) {
        contentColor.copy(alpha = 0.7f)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val cardModifier = Modifier
        .fillMaxWidth()
        .dragAndDropSource(
            block = {
                detectTapGestures(
                    onPress = { offset ->
                        val press = PressInteraction.Press(offset)
                        scope.launch {
                            interactionSource.emit(press)
                        }
                        tryAwaitRelease()
                        scope.launch {
                            interactionSource.emit(PressInteraction.Release(press))
                        }
                    },
                    onTap = { onClick() },
                    onLongPress = { offset ->
                        onDragStart()
                        startTransfer(
                            DragAndDropTransferData(
                                ClipData.newPlainText("card_id", card.id().value().toString()),
                                flags = View.DRAG_FLAG_GLOBAL
                            )
                        )
                    }
                )
            }
        )
        .indication(interactionSource, ripple())
        .graphicsLayer {
            alpha = if (isDragging) 0.5f else 1.0f
        }

    val cardContent: @Composable ColumnScope.() -> Unit = {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = card.title(),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
                Box {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Outlined.MoreVert, contentDescription = "Menu")
                    }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (card.assignedToMe()) "Unassign from me" else "Assign to me") },
                                onClick = {
                                    onAssignToggle()
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Move") },
                                onClick = {
                                    onMove()
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Copy") },
                                onClick = {
                                    onCopy()
                                    showMenu = false
                                }
                            )
                        }
                }
                if (card.dueDate() != null) {
                    val locale = LocalConfiguration.current.locales[0]
                    val formatter = remember(locale) { DateTimeFormatter.ofPattern("MMM dd", locale) }
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.extraSmall,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = card.dueDate().format(formatter),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            if (card.excerpt().isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = card.excerpt(),
                    style = MaterialTheme.typography.bodySmall,
                    color = secondaryContentColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (card.labels().isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    card.labels().forEach { labelPreview ->
                        LabelChipPreview(labelPreview, compactMode)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val taskStatus = if (card.checkboxTotalCount() > 0) {
                "${card.checkboxDoneCount()}/${card.checkboxTotalCount()}"
            } else null

            val commentsCount = card.commentCount()
            val attachmentsCount = card.attachmentCount()

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Remote ID
                    if (card.remoteId() != null) {
                        Text(
                            text = "#${card.remoteId().value()}",
                            style = MaterialTheme.typography.labelMedium,
                            color = secondaryContentColor
                        )
                    }
                    // Comments
                    if (commentsCount > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.ModeComment,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = secondaryContentColor
                            )
                            Text(
                                text = commentsCount.toString(),
                                modifier = Modifier.padding(start = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = secondaryContentColor
                            )
                        }
                    }
                    // Tasks
                    if (taskStatus != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.CheckBox,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = secondaryContentColor
                            )
                            Text(
                                text = taskStatus,
                                modifier = Modifier.padding(start = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = secondaryContentColor
                            )
                        }
                    }
                    // Attachments
                    if (attachmentsCount > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.AttachFile,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = secondaryContentColor
                            )
                            Text(
                                text = attachmentsCount.toString(),
                                modifier = Modifier.padding(start = 4.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = secondaryContentColor
                            )
                        }
                    }
                }

                if (card.assignedToMe() && currentAccount != null) {
                    UserAvatar(
                        account = currentAccount,
                        userId = User.ID(currentAccount.username()),
                        size = 24.dp
                    )
                } else if (card.assigneeCount() > 0) {
                    Text(
                        text = "${card.assigneeCount()} assignees",
                        style = MaterialTheme.typography.labelSmall,
                        color = secondaryContentColor
                    )
                }
            }
        }
    }

    if (isDarkTheme) {
        OutlinedCard(
            modifier = cardModifier,
            colors = CardDefaults.outlinedCardColors(
                containerColor = containerColor,
                contentColor = contentColor
            ),
            content = cardContent
        )
    } else {
        ElevatedCard(
            modifier = cardModifier,
            colors = CardDefaults.elevatedCardColors(
                containerColor = containerColor,
                contentColor = contentColor
            ),
            content = cardContent
        )
    }
}

@Composable
fun LabelChipPreview(label: PreviewCard.LabelPreview, compactMode: Boolean = false) {
    val colorUtil = LocalColorUtil.current
    val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
    val harmonizedColor = colorUtil.harmonize(label.color().argb(), primaryColor)

    if (compactMode) {
        Box(
            modifier = Modifier
                .width(38.dp)
                .height(3.dp)
                .background(Color(harmonizedColor), shape = MaterialTheme.shapes.extraSmall)
        )
    } else {
        val backgroundColor = Color(harmonizedColor)
        val foregroundColor = Color(colorUtil.getForegroundColorForBackgroundColor(harmonizedColor))
        Surface(
            color = backgroundColor,
            contentColor = foregroundColor,
            shape = MaterialTheme.shapes.extraSmall,
        ) {
            Text(
                text = label.title(),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun LabelChip(label: Label) {
    val colorUtil = LocalColorUtil.current
    val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
    val harmonizedColor = colorUtil.harmonize(label.color().argb(), primaryColor)

    val backgroundColor = Color(harmonizedColor)
    val foregroundColor = Color(colorUtil.getForegroundColorForBackgroundColor(harmonizedColor))
    Surface(
        color = backgroundColor,
        contentColor = foregroundColor,
        shape = MaterialTheme.shapes.extraSmall,
    ) {
        Text(
            text = label.title(),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun PlaceholderCard(@Suppress("UNUSED_PARAMETER") columnId: Long) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        border = BorderStroke(
            2.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        ),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {}
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
