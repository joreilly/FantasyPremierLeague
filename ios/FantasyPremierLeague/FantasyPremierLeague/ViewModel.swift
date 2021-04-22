import Foundation
import Combine
import FantasyPremierLeagueKit


class FantasyPremierLeagueViewModel: ObservableObject {
    @Published var playerList = [Player]()
    @Published var fixtureList = [GameFixture]()
    
    private let repository: FantasyPremierLeagueRepository
    init(repository: FantasyPremierLeagueRepository) {
        self.repository = repository
    }
    
    func getPlayers() {
        repository.getPlayers { playerList in
            self.playerList = playerList
        }
    }
    
    func getFixtures() {
        repository.getFixtures { fixtureList in
            self.fixtureList = fixtureList
        }
    }

}


