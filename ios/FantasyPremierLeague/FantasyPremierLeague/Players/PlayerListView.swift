import SwiftUI
import Combine
import FantasyPremierLeagueKit


struct PlayerListView: View {
    @ObservedObject var viewModel: FantasyPremierLeagueViewModel
    
    var body: some View {
        NavigationView {
            List(viewModel.playerList, id: \.id) { player in
                NavigationLink(destination: PlayerDetailsView(player: player)) {
                    PlayerView(player: player)
                }
            }
            .navigationBarTitle(Text("Players"))
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



