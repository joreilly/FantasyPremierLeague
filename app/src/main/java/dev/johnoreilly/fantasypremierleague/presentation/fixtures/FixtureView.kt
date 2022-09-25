package dev.johnoreilly.fantasypremierleague.presentation.fixtures

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.accompanist.placeholder.placeholder
import dev.johnoreilly.common.domain.entities.GameFixture
import dev.johnoreilly.fantasypremierleague.presentation.global.lowfidelitygray

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
        color = MaterialTheme.colors.surface,
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
                    text = "(${fixture.homeTeamScore})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    color = MaterialTheme.colors.onSurface
                )
                Text(
                    modifier = Modifier.placeholder(visible = isDataLoading, lowfidelitygray),
                    text = "vs",
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp
                )
                Text(
                    text = "(${fixture.awayTeamScore})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    color = MaterialTheme.colors.onSurface
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
                fontSize = 14.sp,
                color = MaterialTheme.colors.onSurface
            )

            fixture.localKickoffTime.let { localKickoffTime ->
                val formattedTime = "%02d:%02d".format(localKickoffTime.hour, localKickoffTime.minute)
                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = formattedTime,
                    fontWeight = FontWeight.Light,
                    fontSize = 14.sp,
                    color = MaterialTheme.colors.onSurface
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
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colors.onSurface
        )
    }
}