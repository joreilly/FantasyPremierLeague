import SwiftUI
import FantasyPremierLeagueKit

struct SettingsView: View {
    @State var viewModel = LeaguesViewModel()
    @State var leagueListString = ""
    
    var body: some View {
        NavigationStack {
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

