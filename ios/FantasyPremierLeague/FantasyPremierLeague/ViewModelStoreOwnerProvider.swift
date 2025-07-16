import FantasyPremierLeagueKit
import SwiftUI

/// A SwiftUI `View` that provides a `ViewModelStoreOwner` to its content.
///
/// Manages the lifecycle of `ViewModel` instances, scoping them to this view hierarchy.
/// Clears the associated `ViewModelStore` when the provider disappears.
///
/// You can have a shorter scope of a ViewModel when nesting this provider.
struct ViewModelStoreOwnerProvider<Content: View>: View {
    @StateObject private var viewModelStoreOwner = IOSViewModelStoreOwner()

    private let content: Content

    /// Initializes the provider with its content, creating a new `IOSViewModelStoreOwner`.
    init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }

    var body: some View {
        content
            .environmentObject(viewModelStoreOwner)
            .onDisappear {
                viewModelStoreOwner.clear()
            }
    }
}

/// A ViewModelStoreOwner specifically for iOS to be an ObservableObject.
class IOSViewModelStoreOwner: ObservableObject, ViewModelStoreOwner {

    var viewModelStore = ViewModelStore()
    
    /// This function allows retrieving the androidx ViewModel from the store.
    func viewModel<T: ViewModel>(
        key: String? = nil,
        extras: CreationExtras? = nil
    ) -> T {
        do {
            return try koinResolveViewModel(
                vmClass: T.self,
                owner: self,
                key: key,
                extras: extras
            ) as! T
        } catch {
            fatalError("Failed to create ViewModel of type \(T.self)")
        }
    }
    
    func clear() {
        viewModelStore.clear()
    }
}
