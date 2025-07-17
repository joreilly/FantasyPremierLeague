package dev.johnoreilly.common.di

import dev.johnoreilly.common.viewmodel.FixturesViewModel
import dev.johnoreilly.common.viewmodel.LeaguesViewModel
import dev.johnoreilly.common.viewmodel.PlayerDetailsViewModel
import dev.johnoreilly.common.viewmodel.PlayerListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::PlayerListViewModel)
    viewModel { parameters -> PlayerDetailsViewModel(parameters.get(), get()) }
    viewModelOf(::FixturesViewModel)
    viewModelOf(::LeaguesViewModel)
}
