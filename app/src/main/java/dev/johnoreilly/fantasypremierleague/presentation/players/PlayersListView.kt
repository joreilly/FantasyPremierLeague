@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.placeholder
import dev.johnoreilly.common.domain.entities.Player
import dev.johnoreilly.fantasypremierleague.presentation.FantasyPremierLeagueViewModel
import dev.johnoreilly.fantasypremierleague.presentation.global.lowfidelitygray

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
        Column(Modifier.padding(it)) {
            val isDataLoading = playerList.value.isEmpty()
            TextField(
                singleLine = true,
                value = playerSearchQuery.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
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
                onValueChange = {
                    fantasyPremierLeagueViewModel.onPlayerSearchQueryChange(it)
                }
            )
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