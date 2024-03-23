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
import androidx.compose.ui.Modifier
import dev.johnoreilly.common.domain.entities.Player
import dev.johnoreilly.common.ui.PlayerDetailsViewShared


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDetailsView(player: Player, popBackStack: () -> Unit) {

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
            PlayerDetailsViewShared(player)
        }
    }
}
