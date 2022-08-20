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
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.johnoreilly.common.data.model.LeagueResultDto
import dev.johnoreilly.fantasypremierleague.BuildConfig
import dev.johnoreilly.fantasypremierleague.presentation.FantasyPremierLeagueViewModel

@Composable
fun LeagueListView(viewModel: FantasyPremierLeagueViewModel) {

    val leagueStandings by viewModel.leagueStandings.collectAsState(emptyList())
    val leagueName by viewModel.leagueName.collectAsState("")

    val leagues by viewModel.leagues.collectAsState(emptyList())

    val isRefreshing by viewModel.isRefreshing.collectAsState()

    LaunchedEffect(leagues) {
        if (leagues.isNotEmpty()) {
            viewModel.getLeagueStandings()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(leagueName) })
        }) {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = { viewModel.getLeagueStandings() },
            ) {
                LazyColumn {
                    items(items = leagueStandings) { leagueResult ->
                        LeagueResultView(leagueResult = leagueResult)
                    }
                }
            }
        }
}