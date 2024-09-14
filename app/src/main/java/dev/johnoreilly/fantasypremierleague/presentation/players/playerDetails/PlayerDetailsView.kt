@file:OptIn(ExperimentalMaterial3Api::class)

package dev.johnoreilly.fantasypremierleague.presentation.players.playerDetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.johnoreilly.common.model.Player
import dev.johnoreilly.common.model.PlayerPastHistory
import dev.johnoreilly.common.ui.PlayerDetailsViewShared
import dev.johnoreilly.common.viewmodel.PlayerDetailsViewModel
import org.koin.compose.koinInject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDetailsView(playerId: Int, popBackStack: () -> Unit) {
    val viewModel = koinInject<PlayerDetailsViewModel>()

    val player by produceState<Player?>(initialValue = null) {
        value = viewModel.getPlayer(playerId)
    }

    player?.let { player ->
        var playerHistory by remember { mutableStateOf(emptyList<PlayerPastHistory>()) }
        LaunchedEffect(player) {
            playerHistory = viewModel.getPlayerHistory(player.id)
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(text = player.name)
                    },
                    navigationIcon = {
                        IconButton(onClick = { popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }) {
            Column(Modifier.padding(it)) {
                PlayerDetailsViewShared(player, playerHistory)
            }
        }
    }
}
