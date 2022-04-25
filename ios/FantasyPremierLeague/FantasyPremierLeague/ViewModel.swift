import Foundation
import FantasyPremierLeagueKit
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



