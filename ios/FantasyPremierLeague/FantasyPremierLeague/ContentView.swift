import SwiftUI
import FantasyPremierLeagueKit



struct ContentView: View {
    @StateObject var viewModel = FantasyPremierLeagueViewModel(repository: FantasyPremierLeagueRepository())

    var body: some View {
        TabView {
            PlayerListView(viewModel: viewModel)
                .tabItem {
                    Label("Players", systemImage: "person")
                }
            FixtureListView(viewModel: viewModel)
                .tabItem {
                    Label("Fixtues", systemImage: "clock")
                }
            if (!viewModel.leagues.isEmpty) {
                LeagueListView(viewModel: viewModel)
                    .tabItem {
                        Label("League", systemImage: "list.number")
                    }
            }
        }
    }
}











