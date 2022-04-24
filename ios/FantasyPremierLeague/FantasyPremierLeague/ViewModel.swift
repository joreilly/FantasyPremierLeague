import Foundation
import FantasyPremierLeagueKit
import Combine
import KMPNativeCoroutinesAsync
import AsyncAlgorithms


@MainActor
class FantasyPremierLeagueViewModel: ObservableObject {
    @Published var playerList = [Player]()
    @Published var fixtureList = [GameFixture]()
    
    @Published var query: String = ""
    
    private let repository: FantasyPremierLeagueRepository
    init(repository: FantasyPremierLeagueRepository) {
        self.repository = repository
        
        Task {
            let playerStream = asyncStream(for: repository.playerListNative)
                .map { $0.sorted { $0.points > $1.points } }
            
            for try await (players, searchQuery) in combineLatest(playerStream, $query.values) {
                self.playerList = players
                    .filter { searchQuery.isEmpty || $0.name.localizedCaseInsensitiveContains(query) }
            }
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

