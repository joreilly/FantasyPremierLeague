@file:OptIn(ExperimentalMaterial3Api::class)

package dev.johnoreilly.fantasypremierleague.presentation.players

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.placeholder.placeholder
import dev.johnoreilly.common.model.Player
import dev.johnoreilly.common.viewmodel.PlayerListUIState
import dev.johnoreilly.common.viewmodel.PlayerListViewModel
import dev.johnoreilly.fantasypremierleague.presentation.global.lowfidelitygray
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerListView(
    onPlayerSelected: (player: Player) -> Unit,
    onShowSettings: () -> Unit
) {
    val playerListViewModel = koinViewModel<PlayerListViewModel>()
    val playerListUIState = playerListViewModel.playerListUIState.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val allPlayers = playerListViewModel.allPlayers.collectAsStateWithLifecycle()
    val playerSearchQuery = playerListViewModel.searchQuery.collectAsStateWithLifecycle()

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
                    playerListViewModel.onPlayerSearchQueryChange(searchQuery)

                },
                trailingIcon = {
                    if (playerSearchQuery.value.isNotEmpty()) {
                        Icon(
                            modifier = Modifier.clickable {
                                playerListViewModel.onPlayerSearchQueryChange("")
                            },
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search"
                        )
                    }
                }
            )

            when (val uiState = playerListUIState.value) {
                is PlayerListUIState.Error -> {
                    Text("Error: ${uiState.message}")
                }

                PlayerListUIState.Loading -> {
                    // show placeholder UI
                    LazyColumn {
                        items(
                            items = placeHolderPlayerList, itemContent = { player ->
                                PlayerView(player, onPlayerSelected, isDataLoading)
                            })
                    }
                }

                is PlayerListUIState.Success -> {
                    LazyColumn {
                        items(items = uiState.result, itemContent = { player ->
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