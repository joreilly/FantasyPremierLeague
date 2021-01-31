package dev.johnoreilly.fantasypremierleague.ui.players.playerDetails

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.chrisbanes.accompanist.coil.CoilImage
import dev.johnoreilly.common.repository.Player
import dev.johnoreilly.fantasypremierleague.ui.players.PlayersViewModel

@Composable
fun PlayerDetailsView(
    player: Player,
    playersViewModel: PlayersViewModel
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .preferredHeight(150.dp)
                .fillMaxWidth()
        ) {
            CoilImage(
                data = player.photoUrl,
                modifier = Modifier.preferredSize(150.dp),
                contentDescription = player.name
            )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .preferredHeight(100.dp)
                    .fillMaxWidth()
            ) {
                Text(player.name, style = MaterialTheme.typography.h6)
                Text(player.team, style = MaterialTheme.typography.subtitle1, color = Color.DarkGray)
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        Row {
            Text(text = "POINTS:", style = MaterialTheme.typography.subtitle1)
            Text(
                player.points.toString(),
                modifier = Modifier.align(Alignment.CenterVertically),
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold
            )
        }
    }
}