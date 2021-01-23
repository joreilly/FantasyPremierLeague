import SwiftUI
import FantasyPremierLeagueKit

@main
struct FantasyPremierLeagueApp: App {
    init() {
        KoinKt.doInitKoin()
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
