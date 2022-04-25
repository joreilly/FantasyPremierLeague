import SwiftUI
import Combine
import FantasyPremierLeagueKit



extension GameFixture: Identifiable { }

struct FixtureListView: View {
    @ObservedObject var viewModel: FantasyPremierLeagueViewModel
    
    var body: some View {
        NavigationView {
            List(viewModel.fixtureList) { fixture in
                NavigationLink(destination: FixtureDetailView(fixture: fixture)) {
                    FixtureView(fixture: fixture)
                }
            }
            .navigationBarTitle(Text("Fixtues"))
            .onAppear {
                UITableView.appearance().separatorStyle = .none                
            }
            .task {
                await viewModel.getFixtures()
            }
        }
        .onAppear {
            UITableView.appearance().separatorStyle = .none
        }
    }
}


struct FixtureView: View {
    let fixture: GameFixture
    
    var body: some View {
        VStack {
            HStack {
                ClubInFixtureView(teamName: fixture.homeTeam, teamPhotoUrl: fixture.homeTeamPhotoUrl)
                Spacer()
                Text("(\(fixture.homeTeamScore ?? 0))").font(.system(size: 18))
                Spacer()
                Text("vs").font(.system(size: 22))
                Spacer()
                Text("(\(fixture.awayTeamScore ?? 0))").font(.system(size: 18))
                Spacer()
                ClubInFixtureView(teamName: fixture.awayTeam, teamPhotoUrl: fixture.awayTeamPhotoUrl)
            }
            let formattedTime = String(format: "%02d:%02d", fixture.localKickoffTime.hour, fixture.localKickoffTime.minute)
            Text(fixture.localKickoffTime.date.description()).font(.system(size: 14)).padding(.top, 10)
            Text(formattedTime).font(.system(size: 14))
        }.padding(8)
    }
}


struct ClubInFixtureView: View {
    let teamName: String
    let teamPhotoUrl: String
    
    var body: some View {
        VStack {
            AsyncImage(url: URL(string: teamPhotoUrl)) { image in
                 image.resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: 50, height: 50)
            } placeholder: {
                ProgressView()
            }
            Text(teamName).font(.system(size: 14))
        }.frame(minWidth: 80)
    }
}
