@file:OptIn(ExperimentalMaterial3Api::class)

package dev.johnoreilly.common.ui.players

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.johnoreilly.common.model.Player
import dev.johnoreilly.common.ui.global.ErrorType
import dev.johnoreilly.common.ui.global.ErrorView
import dev.johnoreilly.common.viewmodel.PlayerListUIState
import dev.johnoreilly.common.viewmodel.PlayerListViewModel
import org.koin.compose.viewmodel.koinViewModel

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
                    IconButton(
                        onClick = { onShowSettings() }
                    ) {
                        Icon(
                            Icons.Filled.Settings,
                            contentDescription = "Open settings"
                        )
                    }
                }
            )
        }) {
        Column(Modifier.padding(it)) {
            val isDataLoading = allPlayers.value.isEmpty()
            TextField(
                singleLine = true,
                value = playerSearchQuery.value,
                modifier = Modifier.fillMaxWidth(),
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
                        IconButton(
                            onClick = {
                                playerListViewModel.onPlayerSearchQueryChange("")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search query"
                            )
                        }
                    }
                }
            )

            when (val uiState = playerListUIState.value) {
                is PlayerListUIState.Error -> {
                    ErrorView(
                        message = uiState.message,
                        errorType = ErrorType.SERVER,
                        onRetry = null // No retry mechanism available in current implementation
                    )
                }

                PlayerListUIState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
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

