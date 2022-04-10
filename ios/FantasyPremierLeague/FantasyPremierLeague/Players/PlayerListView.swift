import SwiftUI
import Combine
import FantasyPremierLeagueKit


struct PlayerListView: View {
    @ObservedObject var viewModel: FantasyPremierLeagueViewModel
    
    var body: some View {
        NavigationView {
            
            List {
                TextField("Query", text: $viewModel.query)
                ForEach(viewModel.playerList, id: \.id) { player in
                    NavigationLink(destination: PlayerDetailsView(player: player)) {
                        PlayerView(player: player)
                    }
                }
            }
            .navigationBarTitle(Text("Players"))
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



