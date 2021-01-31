import SwiftUI
import FantasyPremierLeagueKit


struct ContentView: View {
    @StateObject var viewModel = FantasyPremierLeagueViewModel(repository: FantasyPremierLeagueRepository())
    
    var body: some View {
        NavigationView {
            List(viewModel.playerList, id: \.id) { player in
                NavigationLink(destination: PlayerDetailsView(player: player)) {
                    PlayerView(player: player)
                }
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


struct PlayerDetailsView: View {
    var player: Player
    
    var body: some View {
        
        VStack(alignment: .center, spacing: 32) {
            Text(player.name).font(.title).multilineTextAlignment(.center)
            ImageView(withURL: player.photoUrl, width: 128, height: 128)
        }
        
        List {
            Section(header: Text("Info"), content: {
                InfoRowView(label: "Team", value: player.team)
                InfoRowView(label: "Current Price", value: String(player.currentPrice))
                InfoRowView(label: "Points", value: String(player.points))
                InfoRowView(label: "Goals Scored", value: String(player.goalsScored))
                InfoRowView(label: "Assists", value: String(player.assists))
            })
        }
    }
}


struct InfoRowView: View {
    let label: String
    let value: String
    
    var body: some View {
        HStack {
            Text(label)
            Spacer()
            Text(value)
                .foregroundColor(.accentColor)
                .fontWeight(.semibold)
        }
    }
}
