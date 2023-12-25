import Foundation
import FantasyPremierLeagueKit
import AsyncAlgorithms
import CollectionConcurrencyKit


extension PlayerPastHistory: Identifiable { }
extension LeagueStandingsDto: Identifiable { }


@MainActor
class FantasyPremierLeagueViewModel: ObservableObject {
    @Published var playerList = [Player]()
    @Published var fixtureList = [GameFixture]()
    @Published var gameWeekFixtures = [Int: [GameFixture]]()
    
    @Published var playerHistory = [PlayerPastHistory]()
    @Published var leagueStandings = [LeagueStandingsDto]()
    @Published var eventStatusList: EventStatusListDto? = nil
    
    @Published var leagues = [String]()
    @Published var leagueListString = ""
    
    @Published var query: String = ""
    
    private let repository: FantasyPremierLeagueRepository
    init(repository: FantasyPremierLeagueRepository) {
        self.repository = repository
        
        Task {
            for await data in repository.leagues {
                leagues = data
                leagueListString = leagues.joined(separator: ",")
                await getLeageStandings()
            }
        }
        
        Task {
            self.eventStatusList = try await repository.getEventStatus()
        }
    }

    
    func getPlayers() async {
        let playerStream = repository.playerList
            .map { $0.sorted { $0.points > $1.points } }
        
        let queryStream = $query
            .debounce(for: 0.5, scheduler: DispatchQueue.main)
            .values
        
        for await (players, query) in combineLatest(playerStream, queryStream) {
            self.playerList = players
                .filter { query.isEmpty || $0.name.localizedCaseInsensitiveContains(query) }
        }
    }
    
    
    func getPlayerStats(playerId: Int32) async {
        do {
            let playerHistory = try await repository.getPlayerHistoryData(playerId: playerId)
            self.playerHistory = playerHistory
            print(self.playerHistory)
            
        } catch {
            print("Failed with error: \(error)")
        }
    }

    
    func getLeageStandings() async {
        do {
            print("getLeageStandings, leagues = \(leagues)")
            self.leagueStandings = try await leagues.asyncCompactMap { leagueIdString in
                if let leagueId = Int32(leagueIdString) {
                    return try await repository.getLeagueStandings(leagueId: Int32(leagueId))
                } else {
                    return nil
                }
            }
        } catch {
            print("Failed with error: \(error)")            
        }
    }

    
    func getFixtures() async {
        for await data in repository.fixtureList {
            self.fixtureList = data
        }
    }

    
    func getGameWeekFixtures() async {
        for await data in repository.gameweekToFixtures {
            self.gameWeekFixtures = data as! [Int : [GameFixture]]
        }
    }

    
    func setLeagues() {
        let leagues = leagueListString.components(separatedBy:", ")
        print("setLeagues, leagues = \(leagues)")
        repository.updateLeagues(leagues: leagues)
    }
}



