import SwiftUI
import FantasyPremierLeagueKit


extension GameFixture: Identifiable { }

struct FixtureListView: View {
    @StateObject var viewModelStoreOwner = SharedViewModelStoreOwner<FixturesViewModel>()
    @State var gameWeek = 1
    
    var body: some View {
        VStack {
            NavigationView {
                VStack(spacing: 0) {
                    HStack {
                        Button(action: {
                            if (gameWeek > 1) { gameWeek = gameWeek - 1 }
                        }) {
                          Image(systemName: "arrow.left")
                        }
                        Text("Gameweek \(gameWeek)")
                        Button(action: {
                            if (gameWeek < 38) { gameWeek = gameWeek + 1 }
                        }) {
                          Image(systemName: "arrow.right")
                        }
                    }
                    
                    Observing(viewModelStoreOwner.instance.gameWeekFixtures) { gameWeekFixtures  in
                        List(gameWeekFixtures[KotlinInt(integerLiteral: gameWeek)] ?? []) { fixture in
                            NavigationLink(destination: FixtureDetailView(fixture: fixture)) {
                                FixtureView(fixture: fixture)
                            }
                        }
                        .listStyle(.plain)
                        .navigationBarTitle(Text("Fixtures"))
                        .onAppear {
                            UITableView.appearance().separatorStyle = .none
                        }
                    }
                }
            }
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
                Text("(\(fixture.homeTeamScore ?? 0))").font(.system(size: 20))
                Spacer()
                Text("vs").font(.system(size: 22))
                Spacer()
                Text("(\(fixture.awayTeamScore ?? 0))").font(.system(size: 20))
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
            Text(teamName).font(.system(size: 14)).lineLimit(1)
        }.frame(minWidth: 80)
    }
}
