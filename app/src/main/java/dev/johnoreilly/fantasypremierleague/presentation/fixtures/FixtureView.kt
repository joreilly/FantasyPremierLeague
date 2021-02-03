package dev.johnoreilly.fantasypremierleague.presentation.fixtures

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.chrisbanes.accompanist.coil.CoilImage
import dev.johnoreilly.common.domain.entities.GameFixture

@Composable
fun FixtureView(fixture: GameFixture) {
    Column(
        modifier = Modifier.padding(10.dp)
    ) {
        Spacer(modifier = Modifier.size(10.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .preferredHeight(200.dp),
            color = MaterialTheme.colors.surface,
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = "Date: ${fixture.kickoffTime.split("T").first()}",
                    color = MaterialTheme.colors.onSurface
                )
                Text(
                    text = "Time: ${fixture.kickoffTime.split("T")[1].split(":00Z").first()}",
                    color = MaterialTheme.colors.onSurface
                )
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ClubInFixtureView(
                        fixture.homeTeam,
                        fixture.homeTeamPhotoUrl
                    )
                    Text(
                        text = "VS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp
                    )
                    ClubInFixtureView(
                        fixture.awayTeam,
                        fixture.awayTeamPhotoUrl
                    )
                }
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
        CoilImage(
            data = teamPhotoUrl,
            modifier = Modifier.preferredSize(60.dp),
            contentDescription = teamName
        )
        Text(text = teamName)
    }
}