import SwiftUI
import FantasyPremierLeagueKit


struct ContentView: View {
    @StateObject var viewModel = FantasyPremierLeagueViewModel(repository: FantasyPremierLeagueRepository())
    
    var body: some View {
        NavigationView {
            List(viewModel.playerList, id: \.id) { player in
                PlayerView(player: player)
            }
            .navigationBarTitle(Text("Fantasy PL"))
            .onAppear {
                viewModel.getPlayers()
            }
        }
    }
}

struct PlayerView: View {
    var player: Player
    
    var body: some View {
        HStack {
            ImageView(withURL: player.photoUrl, width: 64, height: 64)
            VStack(alignment: .leading) {
                Text(player.name).font(.headline)
                Text(player.team).font(.subheadline)
            }
            Spacer()
            Text(String(player.points))
        }
    }
}

