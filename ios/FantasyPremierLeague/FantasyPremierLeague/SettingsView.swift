import SwiftUI

struct SettingsView: View {
    @ObservedObject var viewModel: FantasyPremierLeagueViewModel
    
    var body: some View {
        NavigationStack {
            Form {
                TextField("Leagues", text: $viewModel.leagueListString)
                
                Button("Save") {
                    viewModel.setLeagues()
                }
            }
            .navigationBarTitle("Settings")
        }
    }
}

