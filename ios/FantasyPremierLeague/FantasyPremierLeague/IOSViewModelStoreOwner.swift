import FantasyPremierLeagueKit
import Foundation

/// A ViewModelStoreOwner specifically for iOS to be an ObservableObject.
class IOSViewModelStoreOwner: ObservableObject, ViewModelStoreOwner {

    var viewModelStore = ViewModelStore()

    /// This function allows retrieving the androidx ViewModel from the store with generics
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

    /// This can be called from outside when using the `ViewModelStoreOwnerProvider`
    func clear() {
        viewModelStore.clear()
    }

    /// This is called when this class is used as a `@StateObject`
    deinit {
        viewModelStore.clear()
    }
}
