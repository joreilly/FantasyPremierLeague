@file:OptIn(ExperimentalMaterial3Api::class)

package dev.johnoreilly.fantasypremierleague.presentation.players.playerDetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import dev.johnoreilly.common.ui.PlayerDetailsViewShared
import dev.johnoreilly.common.viewmodel.PlayerDetailsViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDetailsView(playerId: Int, popBackStack: () -> Unit) {
    val viewModel = koinViewModel<PlayerDetailsViewModel>(parameters = { parametersOf(playerId) })

    val state = viewModel.state.collectAsState()

    state.value?.let { state ->
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(text = state.player.name)
                    },
                    navigationIcon = {
                        IconButton(onClick = { popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }) {
            Column(Modifier.padding(it)) {
                PlayerDetailsViewShared(state.player, state.history)
            }
        }
    } ?: CircularProgressIndicator()
}
