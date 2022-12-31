package dev.johnoreilly.fantasypremierleague.presentation.leagues

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import dev.johnoreilly.common.data.model.LeagueResultDto
import dev.johnoreilly.common.domain.entities.GameFixture

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

