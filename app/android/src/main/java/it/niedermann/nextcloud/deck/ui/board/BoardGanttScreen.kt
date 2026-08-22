package it.niedermann.nextcloud.deck.ui.board

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.androidpoet.drafter.gant.GanttChart
import io.androidpoet.drafter.gant.GanttChartData
import io.androidpoet.drafter.gant.GanttTask
import io.androidpoet.drafter.gant.SimpleGanttChartRenderer
import it.niedermann.nextcloud.deck.domain.model.query.PreviewCard
import java.time.temporal.ChronoUnit

@Composable
fun BoardGanttScreen(
    cards: List<PreviewCard>,
    onCardClick: (Long) -> Unit
) {
    val datedCards = remember<List<PreviewCard>>(cards) {
        cards.filter { it.startDate != null || it.dueDate != null }
    }

    if (datedCards.isEmpty()) {
        Text(
            text = "No cards with start or end date found in this board.",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
        return
    }

    val minDate = remember(datedCards) {
        datedCards.minOf { it.startDate ?: it.dueDate!! }
    }

    val ganttData = remember(datedCards, minDate) {
        val tasks = datedCards.map { card ->
            val start = card.startDate ?: card.dueDate!!
            val end = card.dueDate ?: card.startDate!!
            val startOffset = ChronoUnit.DAYS.between(minDate, start).toFloat()
            val duration = ChronoUnit.DAYS.between(start, end).toFloat().coerceAtLeast(1f)
            GanttTask(card.title(), startOffset, duration)
        }
        val colors = datedCards.map { card ->
            card.color()?.let { Color(it.argb()) } ?: Color.Gray
        }
        GanttChartData(tasks = tasks, taskColors = colors)
    }

    GanttChart(
        data = ganttData,
        renderer = SimpleGanttChartRenderer(),
        modifier = Modifier.fillMaxSize()
    )
}
