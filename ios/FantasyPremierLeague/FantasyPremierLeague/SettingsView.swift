import SwiftUI
import FantasyPremierLeagueKit

struct SettingsView: View {
    @StateObject var viewModel = SharedViewModel<LeaguesViewModel>()
    @State var leagueListString = ""
    
    var body: some View {
        NavigationStack {
            Form {
                TextField("Leagues", text: $leagueListString)
                
                Button("Save") {
                    let leagues = leagueListString.components(separatedBy:", ")
                    viewModel.instance.updateLeagues(leagues: leagues)
                }
            }
            .navigationBarTitle("Settings")
        }
    }
}

