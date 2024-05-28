//
//  SharedViewModel.swift
//  FantasyPremierLeague
//
//  Created by François Dabonot on 28/05/2024.
//

import Foundation
import FantasyPremierLeagueKit

/**
 Wrapping the kotlin viewmodel inside an ObservableObject to simulate the lifecycle of a SwiftUI viewModel
 */
class SharedViewModel<T : Lifecycle_viewmodelViewModel> : ObservableObject {
    
    let instance: T
    
    init(_ viewModel: T = .init()) {
        self.instance = viewModel
    }
    
    deinit {
        // cancel the scope of the viewmodel
        self.instance.cancelViewModelScope()
        // call `onCleared` viewmodel event
        self.instance.onCleared()
    }
}
