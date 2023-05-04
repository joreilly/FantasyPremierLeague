@file:OptIn(ExperimentalMaterial3Api::class)

package dev.johnoreilly.fantasypremierleague.presentation.leagues

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.johnoreilly.fantasypremierleague.presentation.FantasyPremierLeagueViewModel

@Composable
fun LeagueListView(viewModel: FantasyPremierLeagueViewModel) {

    val leagueStandings by viewModel.leagueStandings.collectAsStateWithLifecycle(emptyList())
    val leagueName by viewModel.leagueName.collectAsStateWithLifecycle("")

    val leagues by viewModel.leagues.collectAsStateWithLifecycle(emptyList())

    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    LaunchedEffect(leagues) {
        if (leagues.isNotEmpty()) {
            viewModel.getLeagueStandings()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(leagueName) })
        }) {
            SwipeRefresh(
                modifier = Modifier.padding(it),
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