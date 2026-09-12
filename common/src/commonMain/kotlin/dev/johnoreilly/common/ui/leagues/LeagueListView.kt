@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package dev.johnoreilly.common.ui.leagues

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
import dev.johnoreilly.common.model.League
import dev.johnoreilly.common.viewmodel.LeaguesViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueListView() {
    val viewModel = koinViewModel<LeaguesViewModel>()
    val leagueStandings by viewModel.leagueStandings.collectAsStateWithLifecycle(emptyList())
    val entryId by viewModel.entryId.collectAsStateWithLifecycle(null)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Leagues") })
        }) {

        Box(Modifier.padding(it)) {
            if (entryId == null) {
                Text(
                    text = "Set your team id in Settings to see your leagues.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    leagueStandings.forEach { (league, standings) ->
                        stickyHeader {
                            Header(text = league.name, subtitle = league.rankSummary())
                        }
                        items(items = standings.standings.results) { leagueResult ->
                            LeagueResultView(leagueResult = leagueResult)
                        }
                    }
                }
            }
        }
    }
}

/** "3rd of 47" - both numbers come back on the entry call, so this costs no extra request. */
private fun League.rankSummary(): String? {
    val rank = entryRank ?: return null
    val total = entryCount ?: return "${rank.ordinal()}"
    return "${rank.ordinal()} of $total"
}

private fun Int.ordinal(): String {
    val suffix = when {
        this % 100 in 11..13 -> "th"
        this % 10 == 1 -> "st"
        this % 10 == 2 -> "nd"
        this % 10 == 3 -> "rd"
        else -> "th"
    }
    return "$this$suffix"
}


@Composable
internal fun Header(
    modifier: Modifier = Modifier,
    text: String,
    subtitle: String? = null,
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
                Column {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    subtitle?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            HorizontalDivider()
        }
    }
}
