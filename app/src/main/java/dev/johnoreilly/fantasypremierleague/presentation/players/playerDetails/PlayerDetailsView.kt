@file:OptIn(ExperimentalMaterial3Api::class)

package dev.johnoreilly.fantasypremierleague.presentation.players.playerDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
//import io.github.koalaplot.core.ChartLayout
//import io.github.koalaplot.core.bar.BarChartEntry
//import io.github.koalaplot.core.bar.DefaultBarChartEntry
//import io.github.koalaplot.core.bar.DefaultVerticalBar
//import io.github.koalaplot.core.bar.VerticalBarChart
//import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
//import io.github.koalaplot.core.util.VerticalRotation
//import io.github.koalaplot.core.util.generateHueColorPalette
//import io.github.koalaplot.core.util.rotateVertically
//import io.github.koalaplot.core.util.toString
//import io.github.koalaplot.core.xychart.CategoryAxisModel
//import io.github.koalaplot.core.xychart.LinearAxisModel
//import io.github.koalaplot.core.xychart.TickPosition
//import io.github.koalaplot.core.xychart.XYChart
//import io.github.koalaplot.core.xychart.rememberAxisStyle

import com.patrykandpatryk.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatryk.vico.compose.axis.vertical.startAxis
import com.patrykandpatryk.vico.compose.chart.Chart
import com.patrykandpatryk.vico.compose.chart.column.columnChart
import com.patrykandpatryk.vico.compose.component.lineComponent
import com.patrykandpatryk.vico.core.DefaultDimens
import com.patrykandpatryk.vico.core.component.shape.Shapes
import com.patrykandpatryk.vico.core.entry.ChartEntry
import com.patrykandpatryk.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatryk.vico.core.entry.FloatEntry
import com.patrykandpatryk.vico.core.entry.composed.ComposedChartEntryModelProducer
import com.patrykandpatryk.vico.core.entry.entryModelOf
import dev.johnoreilly.common.domain.entities.Player
import dev.johnoreilly.common.domain.entities.PlayerPastHistory
import dev.johnoreilly.fantasypremierleague.presentation.FantasyPremierLeagueViewModel
import kotlin.random.Random
import kotlin.random.Random.Default.nextFloat


@Composable
fun PlayerDetailsView(viewModel: FantasyPremierLeagueViewModel, player: Player, popBackStack: () -> Unit) {

    var playerHistory by remember { mutableStateOf(emptyList<PlayerPastHistory>()) }

    LaunchedEffect(true) {
        playerHistory = viewModel.getPlayerHistory(player.id)
        println(playerHistory)
    }


//    var tickPositionState by remember {
//        mutableStateOf(
//            TickPositionState(
//                TickPosition.Outside,
//                TickPosition.Outside
//            )
//        )
//    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = player.name)
                },
                navigationIcon = {
                    IconButton(onClick = { popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.size(16.dp))
                AsyncImage(
                    model = player.photoUrl,
                    contentDescription = player.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(150.dp)
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
                PlayerStatView("Team", player.team)
                PlayerStatView("CurrentPrice", player.currentPrice.toString())
                PlayerStatView("Points", player.points.toString())
                PlayerStatView("Goals Scored", player.goalsScored.toString())
                PlayerStatView("Assists", player.assists.toString())

                Spacer(modifier = Modifier.size(8.dp))

                if (playerHistory.isNotEmpty()) {

                    //BarSamplePlot(playerHistory, tickPositionState, "Points by Season")

                    val producer = ChartEntryModelProducer(playerHistory.mapIndexed { x, y ->
                        FloatEntry(x = x.toFloat(), y = y.totalPoints.toFloat())
                    })

                    val columnChart = columnChart(
                        columns = listOf(lineComponent(
                            color = Color(0xFF3179EA),
                            thickness = 36.dp
                        )),
                        spacing = 10.dp
                    )

                    Chart(
                        chart = columnChart,
                        chartModelProducer = producer,
                        startAxis = startAxis(),
                        bottomAxis = bottomAxis(),
                    )
                }
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
        Divider(thickness = 1.dp)
    }
}


/*
private fun barChartEntries(playerHistory: List<PlayerPastHistory>): List<BarChartEntry<String, Float>> {
    val list = mutableListOf<BarChartEntry<String, Float>>()

    playerHistory.forEachIndexed { index, player ->
        list.add(
            DefaultBarChartEntry(
                xValue = player.seasonName,
                yMin = 0f,
                yMax = player.totalPoints.toFloat(),
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
            color = MaterialTheme.colors.onBackground,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun AxisTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        title,
        color = MaterialTheme.colors.onBackground,
        style = MaterialTheme.typography.subtitle1,
        modifier = modifier
    )
}

@Composable
fun AxisLabel(label: String, modifier: Modifier = Modifier) {
    Text(
        label,
        color = MaterialTheme.colors.onBackground,
        style = MaterialTheme.typography.caption,
        modifier = modifier
    )
}

private val YAxisRange = 0f..25f

internal val padding = 8.dp
internal val paddingMod = Modifier.padding(padding)

internal val fibonacci = mutableStateListOf(1.0f, 1.0f, 2.0f, 3.0f, 5.0f, 8.0f, 13.0f, 21.0f)

private val colors = generateHueColorPalette(fibonacci.size)
private const val BarWidth = 0.8f

@Composable
fun HoverSurface(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(
        elevation = 2.dp,
        shape = MaterialTheme.shapes.medium,
        color = Color.LightGray,
        modifier = modifier.padding(padding)
    ) {
        Box(modifier = Modifier.padding(padding)) {
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
private fun BarSamplePlot(
    playerHistory: List<PlayerPastHistory>,
    tickPositionState: TickPositionState,
    title: String
) {
    val barChartEntries = remember() { barChartEntries(playerHistory) }

    ChartLayout(
        modifier = paddingMod,
        title = { ChartTitle(title) }
    ) {

        XYChart<String, Float>(
            xAxisModel = CategoryAxisModel(playerHistory.map { it.seasonName }),
            yAxisModel = LinearAxisModel(
                0f..playerHistory.maxOf { it.totalPoints }.toFloat(),
                minimumMajorTickIncrement = 1f,
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
                AxisLabel(it.toString(1), Modifier.absolutePadding(right = 2.dp))
            },
            yAxisTitle = {
                AxisTitle(
                    "Points",
                    modifier = Modifier.rotateVertically(VerticalRotation.COUNTER_CLOCKWISE)
                        .padding(bottom = padding)
                )
            },
            verticalMajorGridLineStyle = null
        ) {
            VerticalBarChart(
                series = listOf(barChartEntries),
                bar = { series, _, value ->
                    DefaultVerticalBar(
                        brush = SolidColor(colors[series]),
                        modifier = Modifier.fillMaxWidth(BarWidth),
                    ) {
                        HoverSurface { Text(value.yMax.toString()) }
                    }
                }

            )
        }
    }
}

*/