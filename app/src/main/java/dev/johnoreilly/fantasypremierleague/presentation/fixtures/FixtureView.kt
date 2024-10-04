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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.accompanist.placeholder.placeholder
import dev.johnoreilly.common.model.GameFixture
import dev.johnoreilly.fantasypremierleague.presentation.global.lowfidelitygray
import dev.johnoreilly.fantasypremierleague.presentation.global.maroon200

@Composable
fun FixtureView(
    fixture: GameFixture,
    onFixtureSelected: (fixtureId: Int) -> Unit,
    isDataLoading: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
            .clickable { onFixtureSelected(fixture.id) }
            .placeholder(visible = isDataLoading, lowfidelitygray),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ClubInFixtureView(
                    fixture.homeTeam,
                    fixture.homeTeamPhotoUrl
                )
                Text(
                    text = "${fixture.homeTeamScore ?: ""}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp
                )
                HorizontalDivider(
                    modifier = Modifier
                        .heightIn(min = 20.dp, max = 30.dp)
                        .width(1.dp)
                        .background(color = maroon200)
                )
                Text(
                    text = "${fixture.awayTeamScore ?: ""}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp
                )
                ClubInFixtureView(
                    fixture.awayTeam,
                    fixture.awayTeamPhotoUrl
                )
            }
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = fixture.localKickoffTime.date.toString(),
                fontWeight = FontWeight.Light,
                fontSize = 14.sp
            )

            fixture.localKickoffTime.let { localKickoffTime ->
                val formattedTime = "%02d:%02d".format(localKickoffTime.hour, localKickoffTime.minute)
                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = formattedTime,
                    fontWeight = FontWeight.Light,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun ClubInFixtureView(
    teamName: String,
    teamPhotoUrl: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            model = teamPhotoUrl,
            contentDescription = teamName,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(60.dp)
        )
        Text(
            modifier = Modifier
                .width(100.dp)
                .padding(top = 4.dp),
            text = teamName,
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