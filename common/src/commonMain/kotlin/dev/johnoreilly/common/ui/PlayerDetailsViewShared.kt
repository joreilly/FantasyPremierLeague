package dev.johnoreilly.common.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberImagePainter
import dev.johnoreilly.common.model.Player
import dev.johnoreilly.common.model.PlayerPastHistory
import fantasypremierleague.common.generated.resources.Res
import fantasypremierleague.common.generated.resources.team
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.bar.DefaultVerticalBar
import io.github.koalaplot.core.bar.VerticalBarPlot
import io.github.koalaplot.core.bar.VerticalBarPlotEntry
import io.github.koalaplot.core.bar.verticalBarPlotEntry
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.VerticalRotation
import io.github.koalaplot.core.util.rotateVertically
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.IntLinearAxisModel
import io.github.koalaplot.core.xygraph.TickPosition
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.rememberAxisStyle
import org.jetbrains.compose.resources.stringResource


@Composable
fun PlayerDetailsViewShared(player: Player, playerHistory: List<PlayerPastHistory>) {
    val tickPositionState by remember {
        mutableStateOf(
            TickPositionState(
                TickPosition.Outside,
                TickPosition.Outside
            )
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.size(16.dp))
        val painter = rememberImagePainter(player.photoUrl)
        Image(
            painter, null,
            modifier = Modifier.size(150.dp),
            contentScale = ContentScale.Fit,
        )
        Spacer(modifier = Modifier.size(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(4.dp)
        ) {
            Text(
                text = "INFO",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        PlayerStatView(stringResource(Res.string.team),  player.team)
        //PlayerStatView("Team",  player.team)
        PlayerStatView("CurrentPrice", player.currentPrice.toString())
        PlayerStatView("Points", player.points.toString())
        PlayerStatView("Goals Scored", player.goalsScored.toString())
        PlayerStatView("Assists", player.assists.toString())

        Spacer(modifier = Modifier.size(8.dp))

        if (playerHistory.isNotEmpty()) {
            PlayerHistoryBarPlot(playerHistory, tickPositionState, "Points by Season")
        }
    }
}


@Composable
fun PlayerStatView(statName: String, statValue: String) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = statName,
                    fontWeight = FontWeight.Bold
                )
            }
            Column {
                Text(
                    text = statValue,
                    color = Color(0xFF3179EA),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        HorizontalDivider(Modifier, 1.dp, DividerDefaults.color)
    }
}



private fun barChartEntries(playerHistory: List<PlayerPastHistory>): List<VerticalBarPlotEntry<String, Int>> {
    val list = mutableListOf<VerticalBarPlotEntry<String, Int>>()

    playerHistory.forEach { player ->
        list.add(
            verticalBarPlotEntry(
                x = player.seasonName.takeLast(2),
                yMin = 0,
                yMax = player.totalPoints,
            )
        )
    }
    return list
}


@Composable
fun ChartTitle(title: String) {
    Column {
        Text(
            title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun AxisTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        title,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.titleSmall,
        modifier = modifier
    )
}

@Composable
fun AxisLabel(label: String, modifier: Modifier = Modifier) {
    Text(
        label,
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.titleSmall,
        modifier = modifier
    )
}

@Composable
fun HoverSurface(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium,
        color = Color.LightGray,
        modifier = modifier.padding(8.dp)
    ) {
        Box(modifier = Modifier.padding(8.dp)) {
            content()
        }
    }
}


private data class TickPositionState(
    val verticalAxis: TickPosition,
    val horizontalAxis: TickPosition
)

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun PlayerHistoryBarPlot(
    playerHistory: List<PlayerPastHistory>,
    tickPositionState: TickPositionState,
    title: String
) {
    val barChartEntries by remember(playerHistory) { mutableStateOf(barChartEntries(playerHistory)) }

    ChartLayout(
        modifier = Modifier.padding(8.dp),
        title = { ChartTitle(title) }
    ) {

        XYGraph(
            xAxisModel = CategoryAxisModel(playerHistory.map {
                it.seasonName.takeLast(2)
            }),
            yAxisModel = IntLinearAxisModel(
                0..playerHistory.maxOf { it.totalPoints },
                minimumMajorTickIncrement = 1,
                minorTickCount = 0
            ),
            xAxisStyle = rememberAxisStyle(
                tickPosition = tickPositionState.horizontalAxis,
                color = Color.LightGray
            ),
            xAxisLabels = {
                AxisLabel(it, Modifier.padding(top = 2.dp))
            },
            xAxisTitle = { AxisTitle("Season") },
            yAxisStyle = rememberAxisStyle(tickPosition = tickPositionState.verticalAxis),
            yAxisLabels = {
                AxisLabel(it.toString(), Modifier.absolutePadding(right = 2.dp))
            },
            yAxisTitle = {
                AxisTitle(
                    "Points",
                    modifier = Modifier.rotateVertically(VerticalRotation.COUNTER_CLOCKWISE)
                        .padding(bottom = 8.dp)
                )
            },
            verticalMajorGridLineStyle = null
        ) {
            VerticalBarPlot(
                barChartEntries,
                bar = { index ->
                    DefaultVerticalBar(
                        brush = SolidColor(Color.Blue),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        HoverSurface { Text(barChartEntries[index].y.yMax.toString()) }
                    }
                }
            )
        }
    }
}

