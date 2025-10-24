package dev.johnoreilly.fantasypremierleague.presentation.players

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.placeholder.placeholder
import dev.johnoreilly.common.model.Player
import dev.johnoreilly.fantasypremierleague.presentation.global.ImageSize
import dev.johnoreilly.fantasypremierleague.presentation.global.Spacing
import dev.johnoreilly.fantasypremierleague.presentation.global.lowfidelitygray

/**
 * Displays a player item in the list with photo, name, team, and points.
 * Supports loading states with placeholder animations.
 *
 * @param player The player data to display
 * @param onPlayerSelected Callback when the player is clicked
 * @param isDataLoading Whether data is still loading (shows placeholder)
 */
@Composable
fun PlayerView(
    player: Player,
    onPlayerSelected: (player: Player) -> Unit,
    isDataLoading: Boolean
) {
    val semanticDescription = if (!isDataLoading) {
        "${player.name}, ${player.team}, ${player.points} points"
    } else {
        "Loading player data"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(Spacing.mediumLarge)
            .fillMaxWidth()
            .clickable(enabled = !isDataLoading) { onPlayerSelected(player) }
            .semantics(mergeDescendants = true) {
                contentDescription = semanticDescription
            }
    ) {
        AsyncImage(
            model = player.photoUrl,
            contentDescription = null, // Merged into parent semantics
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(ImageSize.medium)
                .placeholder(visible = isDataLoading, lowfidelitygray)
        )
        Spacer(modifier = Modifier.width(Spacing.medium))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = Spacing.small)
        ) {
            Text(
                modifier = Modifier.placeholder(visible = isDataLoading, lowfidelitygray),
                text = player.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(Spacing.extraSmall))
            Text(
                modifier = Modifier.placeholder(visible = isDataLoading, lowfidelitygray),
                text = player.team,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            modifier = Modifier.placeholder(visible = isDataLoading, lowfidelitygray),
            text = player.points.toString(),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}