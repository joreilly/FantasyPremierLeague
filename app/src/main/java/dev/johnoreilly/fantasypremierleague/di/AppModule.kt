package dev.johnoreilly.fantasypremierleague.di

import dev.johnoreilly.common.viewmodel.FixturesViewModel
import dev.johnoreilly.common.viewmodel.LeaguesViewModel
import dev.johnoreilly.common.viewmodel.PlayerDetailsViewModel
import dev.johnoreilly.common.viewmodel.PlayerListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { PlayerListViewModel() }
    viewModel { PlayerDetailsViewModel() }
    viewModel { FixturesViewModel() }
    viewModel { LeaguesViewModel() }
}
