import SwiftUI
import FantasyPremierLeagueKit

struct FixtureDetailView: View {
    var fixture: GameFixture
    
    var body: some View {
        
        VStack {
            HStack {
                ClubInFixtureView(teamName: fixture.homeTeam, teamPhotoUrl: fixture.homeTeamPhotoUrl)
                Spacer()
                Text("(\(fixture.homeTeamScore))").font(.system(size: 22))
                Spacer()
                Text("vs").font(.system(size: 22))
                Spacer()
                Text("(\(fixture.awayTeamScore))").font(.system(size: 22))
                Spacer()
                ClubInFixtureView(teamName: fixture.awayTeam, teamPhotoUrl: fixture.awayTeamPhotoUrl)
            }
        }
        
        List {
            let formattedTime = String(format: "%02d:%02d", fixture.localKickoffTime.hour, fixture.localKickoffTime.minute)
            Section(header: Text("Info"), content: {
                InfoRowView(label: "Date", value:fixture.localKickoffTime.date.description())
                InfoRowView(label: "Kick-off Time", value: formattedTime)
                
            })
        }

    }
}




