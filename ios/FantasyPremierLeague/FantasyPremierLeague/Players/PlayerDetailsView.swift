import SwiftUI
import Charts
import FantasyPremierLeagueKit


struct PlayerDetailsView: View {
    var player: Player
    
    @State var playerHistory = [PlayerPastHistory]()
    
    var body: some View {
        PlayerDetailsViewShared(player: player)
        .navigationTitle(player.name)
    }
}


// This version is using Compose for iOS....
struct PlayerDetailsViewShared: UIViewControllerRepresentable {
    var player: Player
    
    func makeUIViewController(context: Context) -> UIViewController {
        return SharedViewControllers().playerDetailsViewController(player: player)
    }
    
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}


