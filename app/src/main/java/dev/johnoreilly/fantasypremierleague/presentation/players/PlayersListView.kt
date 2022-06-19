package dev.johnoreilly.fantasypremierleague.presentation.players

import android.annotation.SuppressLint
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.johnoreilly.fantasypremierleague.presentation.FantasyPremierLeagueViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PlayerListView(
    fantasyPremierLeagueViewModel: FantasyPremierLeagueViewModel,
    onPlayerSelected: (playerId: Int) -> Unit
) {
    val playerList = fantasyPremierLeagueViewModel.playerList.collectAsState()
    val playerSearchQuery = fantasyPremierLeagueViewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Fantasy Premier League")
                }
            )
        }) {
            Column {
                TextField(
                    singleLine = true,
                    value = playerSearchQuery.value,
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
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
                    onValueChange = {
                        //playerSearchQuery.value = it
                        fantasyPremierLeagueViewModel.onPlayerSearchQueryChange(it)
                    }
                )
                LazyColumn {
                    items(items = playerList.value, itemContent = { player ->
                        PlayerView(player, onPlayerSelected)
                    })
                }
            }
        }
}