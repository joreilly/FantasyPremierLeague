package dev.johnoreilly.fantasypremierleague.presentation.players.playerDetails

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.patrykandpatryk.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatryk.vico.compose.axis.vertical.startAxis
import com.patrykandpatryk.vico.compose.chart.Chart
import com.patrykandpatryk.vico.compose.chart.column.columnChart
import com.patrykandpatryk.vico.compose.component.shape.lineComponent
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


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PlayerDetailsView(viewModel: FantasyPremierLeagueViewModel, player: Player, popBackStack: () -> Unit) {

    var playerHistory by remember { mutableStateOf(emptyList<PlayerPastHistory>()) }

    LaunchedEffect(true) {
        playerHistory = viewModel.getPlayerHistory(player.id)
        println(playerHistory)
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Player")
                },
                navigationIcon = {
                    IconButton(onClick = { popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }) {
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
                        fontWeight = FontWeight.Bold
                    )
                }
                PlayerStatView("Team", player.team)
                PlayerStatView("CurrentPrice", player.currentPrice.toString())
                PlayerStatView("Points", player.points.toString())
                PlayerStatView("Goals Scored", player.goalsScored.toString())
                PlayerStatView("Assists", player.assists.toString())

                Spacer(modifier = Modifier.size(8.dp))

                if (playerHistory.isNotEmpty()) {
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