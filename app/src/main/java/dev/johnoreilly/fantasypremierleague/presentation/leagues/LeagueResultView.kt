package dev.johnoreilly.fantasypremierleague.presentation.leagues

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.johnoreilly.common.data.model.LeagueResultDto

@Composable
fun LeagueResultView(leagueResult: LeagueResultDto) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text(leagueResult.entryName, style = MaterialTheme.typography.titleLarge)
            Text(leagueResult.playerName, style = MaterialTheme.typography.titleMedium, color = Color.DarkGray)
        }
        Text(leagueResult.total.toString(), style = MaterialTheme.typography.titleLarge)
    }
}

