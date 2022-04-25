import SwiftUI
import Combine
import FantasyPremierLeagueKit



extension Player: Identifiable { }

struct PlayerListView: View {
    @ObservedObject var viewModel: FantasyPremierLeagueViewModel
    
    var body: some View {
        NavigationView {            
            List {
                ForEach(viewModel.playerList) { player in
                    NavigationLink(destination: PlayerDetailsView(player: player)) {
                        PlayerView(player: player)
                    }
                }
            }
            .searchable(text: $viewModel.query)
            .navigationBarTitle(Text("Players"))
        }
        .task {
            await viewModel.getPlayers()
        }
    }
}


struct PlayerView: View {
    var player: Player
    
    var body: some View {
        HStack {
            AsyncImage(url: URL(string: player.photoUrl)) { image in
                 image.resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: 64, height: 64)
            } placeholder: {
                ProgressView()
            }
            
            VStack(alignment: .leading) {
                Text(player.name).font(.headline)
                Text(player.team).font(.subheadline)
            }
            Spacer()
            Text(String(player.points))
        }
    }
}



