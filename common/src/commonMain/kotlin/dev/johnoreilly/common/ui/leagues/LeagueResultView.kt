package dev.johnoreilly.common.ui.leagues

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.johnoreilly.common.data.model.LeagueResultDto
import dev.johnoreilly.common.ui.global.Spacing

@Composable
fun LeagueResultView(leagueResult: LeagueResultDto) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(Spacing.mediumLarge)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = Spacing.small)
        ) {
            Text(leagueResult.entryName, style = MaterialTheme.typography.titleLarge)
            Text(
                leagueResult.playerName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(leagueResult.total.toString(), style = MaterialTheme.typography.titleLarge)
    }
}

