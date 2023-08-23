@file:OptIn(ExperimentalMaterial3Api::class)

package dev.johnoreilly.fantasypremierleague.presentation.players

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.placeholder.placeholder
import dev.johnoreilly.common.domain.entities.Player
import dev.johnoreilly.fantasypremierleague.presentation.FantasyPremierLeagueViewModel
import dev.johnoreilly.fantasypremierleague.presentation.global.lowfidelitygray

@Composable
fun PlayerListView(
    fantasyPremierLeagueViewModel: FantasyPremierLeagueViewModel,
    onPlayerSelected: (playerId: Int) -> Unit,
    onShowSettings: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val allPlayers = fantasyPremierLeagueViewModel.allPlayers.collectAsStateWithLifecycle()
    val playerList = fantasyPremierLeagueViewModel.visiblePlayerList.collectAsStateWithLifecycle()
    val playerSearchQuery = fantasyPremierLeagueViewModel.searchQuery.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Fantasy Premier League")
                },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = {
                        onShowSettings()
                    }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            )
        }) {
        Column(Modifier.padding(it)) {
            val isDataLoading = allPlayers.value.isEmpty()
            TextField(
                singleLine = true,
                value = playerSearchQuery.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .placeholder(
                        visible = isDataLoading,
                        color = lowfidelitygray
                    ),
                label = {
                    Text(text = "Search")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                ),
                onValueChange = { searchQuery ->
                    fantasyPremierLeagueViewModel.onPlayerSearchQueryChange(searchQuery)

                },
                trailingIcon = {
                    if (playerSearchQuery.value.isNotEmpty()) {
                        Icon(
                            modifier = Modifier.clickable {
                                fantasyPremierLeagueViewModel.onPlayerSearchQueryChange("")
                            },
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search"
                        )
                    }
                }
            )
            if (!isDataLoading && playerList.value.isEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "No players found")
                }
            } else {
                LazyColumn {
                    if (isDataLoading) {
                        items(
                            items = placeHolderPlayerList, itemContent = { player ->
                                PlayerView(player, onPlayerSelected, isDataLoading)
                            })
                    } else {
                        items(items = playerList.value, itemContent = { player ->
                            PlayerView(player, onPlayerSelected, isDataLoading)
                        })
                    }
                }
            }
        }
    }
}

private val placeHolderPlayerList = listOf(
    Player(
        1, "Jordan Henderson", "Liverpool",
        "", 95, 10.0, 14, 1
    ),
    Player(
        1, "Jordan Henderson", "Liverpool",
        "", 95, 10.0, 14, 1
    ),
    Player(
        1, "Jordan Henderson", "Liverpool",
        "", 95, 10.0, 14, 1
    ),
    Player(
        1, "Jordan Henderson", "Liverpool",
        "", 95, 10.0, 14, 1
    ),
    Player(
        1, "Jordan Henderson", "Liverpool",
        "", 95, 10.0, 14, 1
    ),
    Player(
        1, "Jordan Henderson", "Liverpool",
        "", 95, 10.0, 14, 1
    )
)