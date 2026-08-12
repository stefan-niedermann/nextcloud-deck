package it.niedermann.nextcloud.deck.ui.board

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import it.niedermann.nextcloud.deck.domain.model.FilterInformation
import it.niedermann.nextcloud.deck.domain.model.Label
import it.niedermann.nextcloud.deck.domain.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    availableLabels: List<Label>,
    availableUsers: List<User>,
    filter: FilterInformation,
    onApply: (FilterInformation) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var internalFilter by remember(filter) { mutableStateOf(filter) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Labels", "Users", "Done", "Due Date")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            PrimaryTabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    0 -> LabelsFilter(
                        availableLabels = availableLabels,
                        selectedLabelIds = internalFilter.labelIds(),
                        onToggle = { labelId ->
                            val newIds = HashSet(internalFilter.labelIds())
                            if (newIds.contains(labelId)) {
                                newIds.remove(labelId)
                            } else {
                                newIds.add(labelId)
                            }
                            internalFilter = FilterInformation(
                                newIds,
                                internalFilter.assigneeIds(),
                                internalFilter.doneState(),
                                internalFilter.dueDateFilter()
                            )
                        }
                    )

                    1 -> UsersFilter(
                        availableUsers = availableUsers,
                        selectedUserIds = internalFilter.assigneeIds(),
                        onToggle = { userId ->
                            val newIds = HashSet(internalFilter.assigneeIds())
                            if (newIds.contains(userId)) {
                                newIds.remove(userId)
                            } else {
                                newIds.add(userId)
                            }
                            internalFilter = FilterInformation(
                                internalFilter.labelIds(),
                                newIds,
                                internalFilter.doneState(),
                                internalFilter.dueDateFilter()
                            )
                        }
                    )

                    2 -> DoneFilter(
                        selectedState = internalFilter.doneState(),
                        onSelected = { state ->
                            internalFilter = FilterInformation(
                                internalFilter.labelIds(),
                                internalFilter.assigneeIds(),
                                state,
                                internalFilter.dueDateFilter()
                            )
                        }
                    )

                    3 -> DueDateFilter(
                        selectedFilter = internalFilter.dueDateFilter(),
                        onSelected = { filter ->
                            internalFilter = FilterInformation(
                                internalFilter.labelIds(),
                                internalFilter.assigneeIds(),
                                internalFilter.doneState(),
                                filter
                            )
                        }
                    )
                }
            }

            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = {
                    internalFilter = FilterInformation.EMPTY
                    onReset()
                }) {
                    Text("Reset")
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {
                    onApply(internalFilter)
                    onDismiss()
                }) {
                    Text("Apply")
                }
            }
        }
    }
}

@Composable
private fun LabelsFilter(
    availableLabels: List<Label>,
    selectedLabelIds: Set<Label.ID>,
    onToggle: (Label.ID) -> Unit
) {
    LazyColumn {
        items(availableLabels) { label ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = selectedLabelIds.contains(label.id()),
                    onCheckedChange = { onToggle(label.id()) }
                )
                Text(text = label.title(), modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
private fun UsersFilter(
    availableUsers: List<User>,
    selectedUserIds: Set<User.ID>,
    onToggle: (User.ID) -> Unit
) {
    LazyColumn {
        items(availableUsers) { user ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = selectedUserIds.contains(user.id()),
                    onCheckedChange = { onToggle(user.id()) }
                )
                Text(text = user.displayName(), modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
private fun DoneFilter(
    selectedState: FilterInformation.DoneState,
    onSelected: (FilterInformation.DoneState) -> Unit
) {
    Column {
        FilterInformation.DoneState.entries.forEach { state ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedState == state,
                    onClick = { onSelected(state) }
                )
                Text(
                    text = when (state) {
                        FilterInformation.DoneState.ALL -> "All"
                        FilterInformation.DoneState.DONE -> "Done"
                        FilterInformation.DoneState.NOT_DONE -> "Not Done"
                    },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun DueDateFilter(
    selectedFilter: FilterInformation.DueDateFilter,
    onSelected: (FilterInformation.DueDateFilter) -> Unit
) {
    Column {
        FilterInformation.DueDateFilter.entries.forEach { filter ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedFilter == filter,
                    onClick = { onSelected(filter) }
                )
                Text(
                    text = when (filter) {
                        FilterInformation.DueDateFilter.ALL -> "All"
                        FilterInformation.DueDateFilter.OVERDUE -> "Overdue"
                        FilterInformation.DueDateFilter.TODAY -> "Today"
                        FilterInformation.DueDateFilter.NEXT_7_DAYS -> "Next 7 days"
                        FilterInformation.DueDateFilter.NEXT_30_DAYS -> "Next 30 days"
                        FilterInformation.DueDateFilter.NO_DUE_DATE -> "No due date"
                    },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
