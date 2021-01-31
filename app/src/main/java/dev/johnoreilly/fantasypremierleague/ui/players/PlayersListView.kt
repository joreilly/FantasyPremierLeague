package dev.johnoreilly.fantasypremierleague.ui.players

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun PlayerListView(
    playersViewModel: PlayersViewModel,
    onPlayerSelected: (playerId: Int) -> Unit
) {
    val playerList = playersViewModel.players.observeAsState()
    val playerSearchQuery = playersViewModel.query

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Fantasy Premier League")
                }
            )
        },
        bodyContent = {
            Column {
                OutlinedTextField(
                    singleLine = true,
                    value = playerSearchQuery.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
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
                    onImeActionPerformed = { action, softKeyboardController ->
                        if (action == ImeAction.Search) {
                            playersViewModel.onPlayerSearchQueryChange(
                                playerSearchQuery.value
                            )
                            softKeyboardController?.hideSoftwareKeyboard()
                        }
                    },
                    onValueChange = {
                        playerSearchQuery.value = it
                        playersViewModel.onPlayerSearchQueryChange(it)
                    }
                )

                playerList.value?.let {
                    LazyColumn {
                        items(items = it, itemContent = { player ->
                            PlayerView(player, onPlayerSelected)
                        })
                    }
                }
            }
        }
    )
}