package dev.johnoreilly.fantasypremierleague.di

import dev.johnoreilly.common.viewmodel.FixturesViewModel
import dev.johnoreilly.common.viewmodel.LeaguesViewModel
import dev.johnoreilly.common.viewmodel.PlayerListViewModel
import dev.johnoreilly.fantasypremierleague.presentation.FantasyPremierLeagueViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { FantasyPremierLeagueViewModel(get()) }

    viewModel { PlayerListViewModel() }
    viewModel { FixturesViewModel() }
    viewModel { LeaguesViewModel() }
}
