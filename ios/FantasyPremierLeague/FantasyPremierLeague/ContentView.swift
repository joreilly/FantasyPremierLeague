import SwiftUI
import FantasyPremierLeagueKit


struct ContentView: View {
    var body: some View {
        ComposeUI().ignoresSafeArea(.all)
    }
}


struct ComposeUI: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return SharedViewControllers.shared.mainViewController()
    }
    
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}











