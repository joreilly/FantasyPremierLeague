package dev.johnoreilly.common.ui.players

import androidx.compose.foundation.Image
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
import com.seiko.imageloader.rememberImagePainter
//import coil3.compose.AsyncImage
import dev.johnoreilly.common.model.Player
import dev.johnoreilly.common.ui.global.ImageSize
import dev.johnoreilly.common.ui.global.Spacing

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
            .clickable(enabled = !isDataLoading) { onPlayerSelected(player) }
            .padding(Spacing.mediumLarge)
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = semanticDescription
            }
    ) {
        val painter = rememberImagePainter(player.photoUrl)
        Image(
            painter, null,
            modifier = Modifier.size(ImageSize.medium),
            contentScale = ContentScale.Fit,
        )
        Spacer(modifier = Modifier.width(Spacing.medium))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = Spacing.small)
        ) {
            Text(
                text = player.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(Spacing.extraSmall))
            Text(
                text = player.team,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = player.points.toString(),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}