import SwiftUI
import FantasyPremierLeagueKit

struct SettingsView: View {
    @StateObject var viewModelStoreOwner = IOSViewModelStoreOwner()
    @State var leagueListString = ""

    var body: some View {
        NavigationStack {
            let viewModel: LeaguesViewModel = viewModelStoreOwner.viewModel()
            
            Form {
                TextField("Leagues", text: $leagueListString)
                
                Button("Save") {
                    let leagues = leagueListString.components(separatedBy:", ")
                    viewModel.updateLeagues(leagues: leagues)
                }
            }
            .navigationBarTitle("Settings")
        }
    }
}

