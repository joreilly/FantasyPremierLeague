package dev.johnoreilly.common.ui.fixtures

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberImagePainter
import dev.johnoreilly.common.model.GameFixture
import dev.johnoreilly.common.ui.global.CornerRadius
import dev.johnoreilly.common.ui.global.DividerThickness
import dev.johnoreilly.common.ui.global.ImageSize
import dev.johnoreilly.common.ui.global.Spacing

/**
 * Displays a fixture card showing teams, scores, and kickoff time.
 *
 * @param fixture The fixture data to display
 */
@Composable
fun FixtureView(fixture: GameFixture) {
    val scoreText = if (fixture.homeTeamScore != null && fixture.awayTeamScore != null) {
        "${fixture.homeTeam} ${fixture.homeTeamScore} - ${fixture.awayTeamScore} ${fixture.awayTeam}"
    } else {
        "${fixture.homeTeam} vs ${fixture.awayTeam}, scheduled for ${fixture.localKickoffTime?.date}"
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = Spacing.mediumLarge, top = Spacing.mediumLarge, end = Spacing.mediumLarge)
            .semantics(mergeDescendants = true) {
                contentDescription = "Fixture: $scoreText"
            },
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(CornerRadius.large)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.mediumLarge),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ClubInFixtureView(
                    teamName = fixture.homeTeam,
                    teamPhotoUrl = fixture.homeTeamPhotoUrl
                )
                Text(
                    text = "${fixture.homeTeamScore ?: ""}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider(
                    modifier = Modifier
                        .heightIn(min = 20.dp, max = 30.dp)
                        .width(DividerThickness.standard)
                        .background(color = MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = "${fixture.awayTeamScore ?: ""}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                ClubInFixtureView(
                    teamName = fixture.awayTeam,
                    teamPhotoUrl = fixture.awayTeamPhotoUrl
                )
            }

            fixture.localKickoffTime?.let { localKickoffTime ->
                Text(
                    modifier = Modifier.padding(top = Spacing.mediumLarge),
                    text = localKickoffTime.date.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Light
                )

                val formattedTime = "${localKickoffTime.hour.toString().padStart(2, '0')}:${localKickoffTime.minute.toString().padStart(2, '0')}"
                Text(
                    modifier = Modifier.padding(bottom = Spacing.mediumLarge),
                    text = formattedTime,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}

/**
 * Displays a team's badge and name in a fixture.
 *
 * @param teamName The name of the team
 * @param teamPhotoUrl URL for the team's badge image
 */
@Composable
fun ClubInFixtureView(
    teamName: String,
    teamPhotoUrl: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.semantics(mergeDescendants = true) { }
    ) {
        val painter = rememberImagePainter(teamPhotoUrl)
        Image(
            painter, null,
            modifier = Modifier.size(ImageSize.medium),
            contentScale = ContentScale.Fit,
        )
        Text(
            modifier = Modifier
                .width(100.dp)
                .padding(top = Spacing.extraSmall),
            text = teamName,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

