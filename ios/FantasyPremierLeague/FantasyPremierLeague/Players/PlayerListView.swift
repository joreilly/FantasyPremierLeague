import SwiftUI
import Combine
import FantasyPremierLeagueKit


@propertyWrapper
struct MyPropertyWrapper: DynamicProperty {
  var wrappedValue: String

  func update() {
    // called whenever the view will evaluate its body
  }
}



extension Player: Identifiable { }

struct PlayerListView: View {
    @State var viewModel = PlayerListViewModel()
    @State var playerList = [Player]()
    
    var body: some View {
        NavigationView {
            List(playerList) { player in
                NavigationLink(destination: PlayerDetailsView(player: player)) {
                    PlayerView(player: player)
                }
            }
            //.searchable(text: $viewModel.query)
            .navigationBarTitle(Text("Players"))
            .navigationBarTitleDisplayMode(.inline)
//            .navigationBarItems(trailing:
//                NavigationLink(destination: SettingsView(viewModel: viewModel))  {
//                    Image(systemName: "gearshape")
//                }
//            )
        }
        .task {
            for await playerList in viewModel.playerList {
                self.playerList = playerList
            }

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



