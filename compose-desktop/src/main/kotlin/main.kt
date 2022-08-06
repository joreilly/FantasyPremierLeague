import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.lightColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.johnoreilly.common.data.repository.FantasyPremierLeagueRepository
import dev.johnoreilly.common.di.initKoin
import dev.johnoreilly.common.domain.entities.Player
import dev.johnoreilly.common.domain.entities.PlayerPastHistory
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
import kotlinx.coroutines.flow.*
import presentation.players.PlayerView
import presentation.players.fetchImage
import kotlin.math.ceil


private val koin = initKoin(enableNetworkLogs = true).koin

val lightThemeColors = lightColors(
    primary = Color(0xFFDD0D3C),
    primaryVariant = Color(0xFFC20029),
    secondary = Color.White,
    error = Color(0xFFD00036)
)


fun main() = application {
    val windowState = rememberWindowState()


    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "Fantasy Premier League"
    ) {
        MaterialTheme(
            colors = lightThemeColors
        ) {
            MainLayout()
        }
    }
}

@Composable
fun MainLayout() {
    val selectedPlayer by remember { mutableStateOf("") }
    val repository = koin.get<FantasyPremierLeagueRepository>()

    val searchQuery = MutableStateFlow("")
    val playerList by searchQuery.debounce(250).flatMapLatest { searchQuery ->
        repository.playerList.mapLatest { playerList ->
            playerList
                .filter { it.name.contains(searchQuery, ignoreCase = true) }
                .sortedByDescending { it.points }
        }
    }.collectAsState(emptyList())

    BoxWithConstraints {
        if (maxWidth.value > 700) {
            TwoColumnsLayout(playerList, repository)
        } else {
            PlayerListView(playerList, {})
        }
    }
}


@Composable
fun TwoColumnsLayout(playerList: List<Player>, repository: FantasyPremierLeagueRepository) {

    val currentPlayer: MutableState<Player?> = remember { mutableStateOf(null) }

    Row(Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth(0.3f), contentAlignment = Alignment.Center) {
            PlayerListView(playerList) {
                currentPlayer.value = it
            }
        }
        PlayerDetailsView(currentPlayer.value, repository)
    }
}


@Composable
fun PlayerListView(playerList: List<Player>, playerSelected: (player: Player) -> Unit) {
    Box(modifier = Modifier
            .padding(3.dp)
            .background(color = Color.White)
            .clip(shape = RoundedCornerShape(3.dp))
    ) {
        LazyColumn {
            items(items = playerList, itemContent = { player ->
                PlayerView(player, playerSelected)
            })
        }
    }
}





//@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
fun PlayerDetailsView(player: Player?, repository: FantasyPremierLeagueRepository) {

    player?.let {
//        var tickPositionState by remember {
//            mutableStateOf(
//                TickPositionState(
//                    TickPosition.Outside,
//                    TickPosition.Outside
//                )
//            )
//        }

        var playerHistory by remember { mutableStateOf(emptyList<PlayerPastHistory>()) }

        LaunchedEffect(true) {
            playerHistory = repository.getPlayerHistoryData(player.id)
            println(playerHistory)
        }



        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = player.name,
                style = MaterialTheme.typography.h6
            )
            Spacer(modifier = Modifier.size(16.dp))
            val imageAsset = fetchImage(player.photoUrl)
            imageAsset?.let {
                Image(it, contentDescription = "player image", modifier = Modifier.size(100.dp))
            }
            Spacer(modifier = Modifier.size(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)
                    .padding(4.dp)
            ) {
                Text(
                    text = "INFO",
                    fontWeight = FontWeight.Bold
                )
            }
            PlayerStatView("Team", player.team)
            PlayerStatView("CurrentPrice", player.currentPrice.toString())
            PlayerStatView("Points", player.points.toString())
            PlayerStatView("Goals Scored", player.goalsScored.toString())
            PlayerStatView("Assists", player.assists.toString())

//            Spacer(modifier = Modifier.size(8.dp))
//
//            if (playerHistory.isNotEmpty()) {
//                BarSamplePlot(playerHistory, tickPositionState, "Points by Season")
//            }

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

private data class TickPositionState(
    val verticalAxis: TickPosition,
    val horizontalAxis: TickPosition
)

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
