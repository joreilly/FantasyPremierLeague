import SwiftUI
import Charts
import FantasyPremierLeagueKit


struct PlayerDetailsView: View {
    @ObservedObject var viewModel: FantasyPremierLeagueViewModel
    var player: Player
    
    @State var playerHistory = [PlayerPastHistory]()
    
    var body: some View {
        PlayerDetailsViewShared(player: player, playerHistory: $viewModel.playerHistory)
        .task {
            await viewModel.getPlayerStats(playerId: player.id)
        }
        .navigationTitle(player.name)
    }
}


// This version is using Compose for iOS....
struct PlayerDetailsViewShared: UIViewControllerRepresentable {
    var player: Player
    @Binding var playerHistory: [PlayerPastHistory]
    
    func makeUIViewController(context: Context) -> UIViewController {
        return SharedViewControllers().playerDetailsViewController(player: player)
    }
    
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        SharedViewControllers().updatePlayerHistory(playerHistory: playerHistory)
    }
}



struct PlayerDetailsViewSwiftUI: View {
    @ObservedObject var viewModel: FantasyPremierLeagueViewModel
    var player: Player
    
    var body: some View {
        
        VStack(alignment: .center) {
            VStack {
                Text(player.name).font(.title).multilineTextAlignment(.center)
                
                AsyncImage(url: URL(string: player.photoUrl)) { image in
                     image.resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: 100, height: 100)
                } placeholder: {
                    ProgressView()
                }
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
            
            
            let playerHistory = viewModel.playerHistory

            Chart(playerHistory) {
                BarMark(
                    x: .value("Season", $0.seasonName),
                    y: .value("Points", $0.totalPoints)
                )

            }
            .chartYAxisLabel("Points")
            .chartXAxisLabel("Season", alignment: Alignment.center)
            .frame(height: 250)

            Spacer()
        }
        .task {
            await viewModel.getPlayerStats(playerId: player.id)
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


