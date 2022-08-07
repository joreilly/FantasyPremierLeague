package dev.johnoreilly.fantasypremierleague.presentation.leagues

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.johnoreilly.common.data.model.LeagueResultDto
import dev.johnoreilly.fantasypremierleague.BuildConfig
import dev.johnoreilly.fantasypremierleague.presentation.FantasyPremierLeagueViewModel

@Composable
fun LeagueListView(viewModel: FantasyPremierLeagueViewModel) {

    var leagueStandings by remember { mutableStateOf(emptyList<LeagueResultDto>()) }
    var leagueName by remember { mutableStateOf("") }

    val leagues by viewModel.leagues.collectAsState(emptyList())

    LaunchedEffect(leagues) {
        if (leagues.isNotEmpty()) {
            val league = leagues[0] // 1 league for now
            val result = viewModel.getLeagueStandings(league.toInt())

            leagueName = result.league.name
            leagueStandings = result.standings.results
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(leagueName) })
        }) {
            Column {
                LazyColumn {
                    items(items = leagueStandings, itemContent = { leagueResult ->
                        LeagueResultView(leagueResult = leagueResult)
                    })
                }
            }
        }
}