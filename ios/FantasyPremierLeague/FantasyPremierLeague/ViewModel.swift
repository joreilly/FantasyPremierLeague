import Foundation
import FantasyPremierLeagueKit
import KMPNativeCoroutinesAsync
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
    
    @Published public var leagues = [String]()
    
    @Published var query: String = ""
    
    private let repository: FantasyPremierLeagueRepository
    init(repository: FantasyPremierLeagueRepository) {
        self.repository = repository
        
        Task {
            // TEMP to set a particular league until settings screen added
            repository.updateLeagues(leagues: ["622004", "2263"])
            
            
            do {
                let leagueStream = asyncSequence(for: repository.leagues)
                for try await data in leagueStream {
                    self.leagues = data
                }
                
            } catch {
                print("Failed with error: \(error)")
            }
        }
        
        Task {
            self.eventStatusList = try await asyncFunction(for: repository.getEventStatus())
        }
    }

    
    func getPlayers() async {
        do {
            let playerStream = asyncSequence(for: repository.playerList)
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
            let playerHistory = try await asyncFunction(for: repository.getPlayerHistoryData(playerId: playerId))
            self.playerHistory = playerHistory
            print(self.playerHistory)
            
        } catch {
            print("Failed with error: \(error)")
        }
    }

    
    func getLeageStandings() async {
        do {
            self.leagueStandings = try await leagues.asyncCompactMap { leagueIdString in
                if let leagueId = Int32(leagueIdString) {
                    return try await asyncFunction(for: repository.getLeagueStandings(leagueId: Int32(leagueId)))
                    //self.leagueStandings = leagueStandings
                    //print(self.leagueStandings!)
                } else {
                    return nil
                }
            }
        } catch {
            print("Failed with error: \(error)")            
        }
    }

    
    func getFixtures() async {
        do {
            let stream = asyncSequence(for: repository.fixtureList)
            for try await data in stream {
                self.fixtureList = data
            }
        } catch {
            print("Failed with error: \(error)")
        }
    }

    
    func getGameWeekFixtures() async {
        do {
            let stream = asyncSequence(for: repository.gameweekToFixtures)
            for try await data in stream {
                self.gameWeekFixtures = data as! [Int : [GameFixture]]
            }
        } catch {
            print("Failed with error: \(error)")
        }
    }

    
}



