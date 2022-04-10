import SwiftUI
import FantasyPremierLeagueKit

struct PlayerDetailsView: View {
    var player: Player
    
    var body: some View {
        
        VStack(alignment: .center, spacing: 32) {
            Text(player.name).font(.title).multilineTextAlignment(.center)
            
            AsyncImage(url: URL(string: player.photoUrl)) { image in
                 image.resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: 128, height: 128)
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


