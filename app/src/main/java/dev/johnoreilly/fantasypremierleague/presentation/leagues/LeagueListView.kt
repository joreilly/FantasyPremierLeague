@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package dev.johnoreilly.fantasypremierleague.presentation.leagues

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.johnoreilly.common.viewmodel.LeaguesViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueListView() {
    val viewModel = koinViewModel<LeaguesViewModel>()
    val leagueStandings by viewModel.leagueStandings.collectAsStateWithLifecycle(emptyList())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Leagues") })
        }) {

        Box(Modifier.padding(it)) {
            LazyColumn(Modifier.fillMaxSize()) {
                leagueStandings.forEach { league ->
                    stickyHeader {
                        Header(text = league.league.name)
                    }
                    items(items = league.standings.results) { leagueResult ->
                        LeagueResultView(leagueResult = leagueResult)
                    }
                }
            }
        }
    }
}


@Composable
internal fun Header(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector? = null
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column {
            HorizontalDivider()
            Row(
                modifier = Modifier
                    .padding(
                        horizontal = 16.dp,
                        vertical = 8.dp,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                icon?.let { icon ->
                    Icon(
                        modifier = Modifier
                            .padding(end = 8.dp),
                        imageVector = icon,
                        contentDescription = null,
                    )
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
            HorizontalDivider()
        }
    }
}
