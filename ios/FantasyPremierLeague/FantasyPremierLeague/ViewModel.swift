import Foundation
import Combine
import FantasyPremierLeagueKit


class FantasyPremierLeagueViewModel: ObservableObject {
    @Published var playerList = [Player]()
    
    private let repository: FantasyPremierLeagueRepository
    init(repository: FantasyPremierLeagueRepository) {
        self.repository = repository
    }
    
    func getPlayers() {
        repository.getPlayers() { data, error in
            if let playerList = data {
                self.playerList = playerList
            }
        }
    }
}


