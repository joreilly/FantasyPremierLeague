import Foundation
import FantasyPremierLeagueKit
import KMPNativeCoroutinesAsync
import AsyncAlgorithms


extension PlayerPastHistory: Identifiable { }

@MainActor
class FantasyPremierLeagueViewModel: ObservableObject {
    @Published var playerList = [Player]()
    @Published var fixtureList = [GameFixture]()
    @Published var playerHistory = [PlayerPastHistory]()
    @Published var leagueStandings: LeagueStandingsDto? = nil
    
    @Published var query: String = ""
    
    private let repository: FantasyPremierLeagueRepository
    init(repository: FantasyPremierLeagueRepository) {
        self.repository = repository
    }

    
    func getPlayers() async {
        do {
            let playerStream = asyncStream(for: repository.playerListNative)
                .map { $0.sorted { $0.points > $1.points } }
            
            let queryStream = $query
                .debounce(for: 0.5, scheduler: DispatchQueue.main)
                .values
            
            for try await (players, query) in combineLatest(playerStream, queryStream) {
                self.playerList = players
                    .filter { query.isEmpty || $0.name.localizedCaseInsensitiveContains(query) }
            }
        } catch {
            print("Failed with error: \(error)")
        }
    }
    
    
    func getPlayerStats(playerId: Int32) async {
        do {
            let playerHistory = try await asyncFunction(for: repository.getPlayerHistoryDataNative(playerId: playerId))
            self.playerHistory = playerHistory
            print(self.playerHistory)
            
        } catch {
            print("Failed with error: \(error)")
        }
    }

    
    func getLeageStandings(leagueId: Int32) async {
        do {
            let leagueStandings = try await asyncFunction(for: repository.getLeagueStandingsNative(leagueId: leagueId))
            self.leagueStandings = leagueStandings
            print(self.leagueStandings!)
            
        } catch {
            print("Failed with error: \(error)")
        }
    }

    
    func getFixtures() async {
        do {
            let stream = asyncStream(for: repository.fixtureListNative)
            for try await data in stream {
                self.fixtureList = data
            }
        } catch {
            print("Failed with error: \(error)")
        }
    }
}



