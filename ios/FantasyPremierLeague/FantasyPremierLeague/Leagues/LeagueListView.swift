import SwiftUI
import Combine
import FantasyPremierLeagueKit



extension LeagueResultDto: Identifiable { }

struct LeagueListView: View {
    @ObservedObject var viewModel: FantasyPremierLeagueViewModel
    
    var body: some View {
        NavigationView {
            if let leagueStandings = viewModel.leagueStandings {
                List(leagueStandings.standings.results) { leagueResult in
                    LeagueReesultView(leagueResult: leagueResult)
                }
                .navigationBarTitle(Text(leagueStandings.league.name))
            }
        }
        .task {
            await viewModel.getLeageStandings()
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



