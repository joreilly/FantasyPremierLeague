package dev.johnoreilly.fantasypremierleague.presentation.players

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.coil.rememberCoilPainter
import dev.johnoreilly.common.domain.entities.Player

@Composable
fun PlayerView(
    player: Player,
    onPlayerSelected: (playerId: Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { onPlayerSelected(player.id) }
    ) {
        Image(
            painter = rememberCoilPainter(player.photoUrl),
            modifier = Modifier.size(60.dp),
            contentDescription = player.name
        )
        Spacer(modifier = Modifier.size(12.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text(player.name, style = MaterialTheme.typography.h6)
            Text(player.team, style = MaterialTheme.typography.subtitle1, color = Color.DarkGray)
        }
        Text(player.points.toString(), style = MaterialTheme.typography.h6)
    }
}