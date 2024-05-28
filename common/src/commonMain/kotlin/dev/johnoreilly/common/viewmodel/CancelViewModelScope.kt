package dev.johnoreilly.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Logger.Companion.i
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import org.koin.core.parameter.parametersOf
import org.koin.mp.KoinPlatform.getKoin

/**
 * Cancel view model scope as it can't be automatically cancelled in iOS
 *
 * Should be call from swift iOS code to simulate the cleanup of the view model
 * iOS Target
 */
fun ViewModel.cancelViewModelScope() {
    if (viewModelScope.isActive) {
        viewModelScope.cancel("Manually cancelling viewModelScope for iOS Target")
    }
}
