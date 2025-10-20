package dev.johnoreilly.fantasypremierleague.presentation.fixtures

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.placeholder.placeholder
import dev.johnoreilly.common.model.GameFixture
import dev.johnoreilly.fantasypremierleague.presentation.global.CornerRadius
import dev.johnoreilly.fantasypremierleague.presentation.global.DividerThickness
import dev.johnoreilly.fantasypremierleague.presentation.global.ImageSize
import dev.johnoreilly.fantasypremierleague.presentation.global.Spacing
import dev.johnoreilly.fantasypremierleague.presentation.global.lowfidelitygray
import dev.johnoreilly.fantasypremierleague.presentation.global.maroon200

/**
 * Displays a fixture card showing teams, scores, and kickoff time.
 *
 * @param fixture The fixture data to display
 * @param onFixtureSelected Callback when the fixture is clicked
 * @param isDataLoading Whether data is still loading (shows placeholder)
 */
@Composable
fun FixtureView(
    fixture: GameFixture,
    onFixtureSelected: (fixtureId: Int) -> Unit,
    isDataLoading: Boolean
) {
    val scoreText = if (fixture.homeTeamScore != null && fixture.awayTeamScore != null) {
        "${fixture.homeTeam} ${fixture.homeTeamScore} - ${fixture.awayTeamScore} ${fixture.awayTeam}"
    } else {
        "${fixture.homeTeam} vs ${fixture.awayTeam}, scheduled for ${fixture.localKickoffTime?.date}"
    }

    val semanticDescription = if (!isDataLoading) {
        "Fixture: $scoreText"
    } else {
        "Loading fixture data"
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = Spacing.mediumLarge, top = Spacing.mediumLarge, end = Spacing.mediumLarge)
            .clickable(enabled = !isDataLoading) { onFixtureSelected(fixture.id) }
            .placeholder(visible = isDataLoading, lowfidelitygray)
            .semantics(mergeDescendants = true) {
                contentDescription = semanticDescription
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

                val formattedTime = "%02d:%02d".format(localKickoffTime.hour, localKickoffTime.minute)
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
        AsyncImage(
            model = teamPhotoUrl,
            contentDescription = null, // Team name will be read from text below
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(ImageSize.medium)
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

@Preview
@Composable
fun PreviewFixtureView() {
    val placeholderKickoffTime = kotlinx.datetime.LocalDateTime(2022, 9, 5, 13, 30, 0)
    Column(modifier = Modifier.height(200.dp)) {
        FixtureView(
            fixture = GameFixture(
                id = 1,
                localKickoffTime = placeholderKickoffTime,
                homeTeam = "Liverpool",
                "Spurs",
                "",
                "",
                3,
                0,
                5
            ),
            onFixtureSelected = {},
            isDataLoading = false
        )
    }
}