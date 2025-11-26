@file:OptIn(ExperimentalMaterial3Api::class)

package dev.johnoreilly.common.ui.players.playerDetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.johnoreilly.common.ui.LocalBackButtonVisibility
import dev.johnoreilly.common.viewmodel.PlayerDetailsUiState
import dev.johnoreilly.common.viewmodel.PlayerDetailsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDetailsView(viewModel: PlayerDetailsViewModel, popBackStack: () -> Unit) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is PlayerDetailsUiState.Loading -> {
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
        is PlayerDetailsUiState.Success -> {
            PlayerDetailsViewSuccess(state, popBackStack)
        }
        is PlayerDetailsUiState.Error -> {
            println(state.message)
        }

    }
}


@Composable
fun PlayerDetailsViewSuccess(uiState: PlayerDetailsUiState.Success, popBackStack: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = uiState.player.name)
                },
                navigationIcon = {
                    if (LocalBackButtonVisibility.current) {
                        IconButton(onClick = { popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        }) {
        Column(Modifier.padding(it)) {
            PlayerDetailsViewShared(uiState.player, uiState.history)
        }
    }
}