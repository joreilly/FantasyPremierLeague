import Foundation
import Combine
import FantasyPremierLeagueKit


class FantasyPremierLeagueViewModel: ObservableObject {
    @Published var playerList = [Player]()
    @Published var fixtureList = [GameFixture]()
    
    @Published var query: String = ""
    
    private var subscription: AnyCancellable?
    
    private let repository: FantasyPremierLeagueRepository
    init(repository: FantasyPremierLeagueRepository) {
        self.repository = repository
        
        let playerPublisher = PlayerPublisher(repository: repository)
                
        $query
            .debounce(for: 0.5, scheduler: DispatchQueue.main)
            .flatMap { query in
                playerPublisher.map({ $0.filter({ query.isEmpty ||  $0.name.lowercased().contains(query.lowercased()) }) })
            }
            .map { $0.sorted { $0.points > $1.points } }
            .receive(on: DispatchQueue.main)
            .assign(to: &$playerList)

    }
    
    func getFixtures() {
        repository.getFixtures { fixtureList in
            self.fixtureList = fixtureList
        }
    }

}



public struct PlayerPublisher: Publisher {
    public typealias Output = [Player]
    public typealias Failure = Never
    
    private let repository: FantasyPremierLeagueRepository
    public init(repository: FantasyPremierLeagueRepository) {
        self.repository = repository
    }

    public func receive<S: Subscriber>(subscriber: S) where S.Input == [Player], S.Failure == Failure {
        let subscription = PlayerSubscription(repository: repository, subscriber: subscriber)
        subscriber.receive(subscription: subscription)
    }
    
    final class PlayerSubscription<S: Subscriber>: Subscription where S.Input == [Player], S.Failure == Failure {
        private var subscriber: S?
        private var job: Kotlinx_coroutines_coreJob? = nil

        private let repository: FantasyPremierLeagueRepository

        init(repository: FantasyPremierLeagueRepository, subscriber: S) {
            self.repository = repository
            self.subscriber = subscriber
          
            job = repository.getPlayersFlow().subscribe(
                scope: repository.iosScope,
                onEach: { playerList in
                    subscriber.receive(playerList! as! Array<Player>)
                },
                onComplete: { subscriber.receive(completion: .finished) },
                onThrow: { error in debugPrint(error) }
            )
        }
      
        func cancel() {
            subscriber = nil
            job?.cancel(cause: nil)
        }

        func request(_ demand: Subscribers.Demand) {}
    }
}



