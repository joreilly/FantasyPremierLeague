import SwiftUI
import FantasyPremierLeagueKit


struct ContentView: View {
    var body: some View {
        ComposeUI()
            .ignoresSafeArea(.all)
            .onOpenURL { url in
                SharedViewControllers.shared.handleDeepLink(url: url.absoluteString)
            }
    }
}


struct ComposeUI: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return SharedViewControllers.shared.mainViewController()
    }
    
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}











