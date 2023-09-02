import SwiftUI
import Combine
import FantasyPremierLeagueKit



extension LeagueResultDto: Identifiable { }
extension EventStatusDto: Identifiable { }


struct LeagueListView: View {
    @ObservedObject var viewModel: FantasyPremierLeagueViewModel
    
    var body: some View {
        NavigationStack {
            VStack(alignment: .center) {
                if let eventStatusList = viewModel.eventStatusList {
                    List {
                        Section(header: Text("Status"), content: {
                            ForEach(eventStatusList.status) { eventStatus in
                                InfoRowView(label: eventStatus.date, value: eventStatus.bonus_added.description)
                            }
                        })
                    }
                    .frame(height: 200)
                }
                
                let leagueStandingsList = viewModel.leagueStandings
                List {
                    ForEach(leagueStandingsList) { leagueStandings in
                        Section(header: Text(leagueStandings.league.name), content: {
                            ForEach(leagueStandings.standings.results) { leagueResult in
                                LeagueReesultView(leagueResult: leagueResult)
                            }
                            
                        })
                    }
                    
                }
                .refreshable {
                    await viewModel.getLeageStandings()
                }
            }
            .navigationBarTitle(Text("Leagues"))
            .navigationBarTitleDisplayMode(.inline)
            .navigationBarItems(trailing:
                NavigationLink(destination: SettingsView(viewModel: viewModel))  {
                    Image(systemName: "gearshape")
                }
            )
        }

    }
}


struct LeagueReesultView: View {
    var leagueResult: LeagueResultDto
    
    var body: some View {
        HStack {
            VStack(alignment: .leading) {
                Text(leagueResult.entryName).font(.headline)
                Text(leagueResult.playerName).font(.subheadline)
            }
            Spacer()
            Text(String(leagueResult.total))
        }
    }
}



