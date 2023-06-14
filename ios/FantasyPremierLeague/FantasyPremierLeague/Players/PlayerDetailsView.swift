import SwiftUI
import Charts
import FantasyPremierLeagueKit


struct PlayerDetailsView: View {
    @ObservedObject var viewModel: FantasyPremierLeagueViewModel
    var player: Player
   
    @State private var selectedSeason: String?

    
    var body: some View {

        VStack(alignment: .center) {
            VStack {
                //Text(player.name).font(.title).multilineTextAlignment(.center)
                
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

            Chart {
                ForEach(playerHistory) {
                    BarMark(
                        x: .value("Season", $0.seasonName),
                        y: .value("Points", $0.totalPoints)
                    )
                }

                if let selectedSeason {
                    RuleMark(
                        x: .value("Selection", selectedSeason)
                    )
                    .foregroundStyle(Color.gray)
                    .annotation(
                        position: .leading, alignment: .center, spacing: 0,
                        overflowResolution: .init(
                            x: .fit(to: .chart),
                            y: .disabled
                        )
                    ) {
                        
                        VStack(alignment: .center) {
                            Text(selectedSeason)
                            Divider()
                            if let seasonHistory = playerHistory.first(where: { $0.seasonName == selectedSeason }) {
                                Text("\(seasonHistory.totalPoints) points")
                                Text("\(seasonHistory.totalGoals) goals")
                                Text("\(seasonHistory.totalAssists) assists")
                            }
                        }
                        .padding()
                        .background(Color(uiColor: .secondarySystemBackground))
                    }

                }

            }
            .chartScrollableAxes(.horizontal)
            .chartXVisibleDomain(length: 5)
            .chartYAxisLabel("Points")
            .chartXAxisLabel("Season", alignment: Alignment.center)
            .chartXSelection(value: $selectedSeason)
            .frame(height: 250)
            .padding()
            

            Spacer()
        }
        .task {
            await viewModel.getPlayerStats(playerId: player.id)
        }
        .navigationBarTitle(Text(player.name), displayMode: .inline)
        
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


