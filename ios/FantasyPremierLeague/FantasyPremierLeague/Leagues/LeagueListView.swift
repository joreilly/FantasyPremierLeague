import SwiftUI
import FantasyPremierLeagueKit



extension LeagueResultDto: Identifiable { }
extension EventStatusDto: Identifiable { }
extension LeagueStandingsDto: Identifiable { }


struct LeagueListView: View {
    @EnvironmentObject var viewModelStoreOwner : IOSViewModelStoreOwner
    
    @State var leagueStandingsList = [LeagueStandingsDto]()
    @State var eventStatusList = [EventStatusDto]()

    var body: some View {
        let viewModel : LeaguesViewModel = viewModelStoreOwner.viewModel()
        
        NavigationStack {
            VStack(alignment: .center) {
                List {
                    Section(header: Text("Status"), content: {
                        ForEach(eventStatusList) { eventStatus in
                            HStack {
                                Text(eventStatus.date)
                                Spacer()
                                if eventStatus.bonus_added {
                                    Image(systemName: "checkmark")
                                }
                            }
                        }
                    })
                    
                    ForEach(leagueStandingsList) { leagueStandings in
                        Section(header: Text(leagueStandings.league.name), content: {
                            ForEach(leagueStandings.standings.results) { leagueResult in
                                LeagueReesultView(leagueResult: leagueResult)
                            }
                        })
                    }
                }
            }
            .navigationBarTitle(Text("Leagues"))
            .navigationBarTitleDisplayMode(.inline)
            .navigationBarItems(trailing:
                NavigationLink(destination: SettingsView())  {
                    Image(systemName: "gearshape")
                }
            )
            .task {
                do {
                    try await eventStatusList = viewModel.getEventStatus()
                    
                    for await data in viewModel.leagueStandings {
                        leagueStandingsList = data
                    }
                } catch {
                    print("Error")
                }
            }
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



