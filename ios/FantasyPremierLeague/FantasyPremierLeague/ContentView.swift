import SwiftUI

struct ContentView: View {
    var body: some View {
        TabView {
            PlayerListView()
                .tabItem { Label("Players", systemImage: "person") }
            FixtureListView()
                .tabItem { Label("Fixtues", systemImage: "clock") }
            LeagueListView()
                .tabItem { Label("League", systemImage: "list.number") }
        }
    }
}











