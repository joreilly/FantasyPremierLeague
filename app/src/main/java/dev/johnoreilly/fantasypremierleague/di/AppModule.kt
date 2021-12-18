package dev.johnoreilly.fantasypremierleague.di

import dev.johnoreilly.fantasypremierleague.presentation.FantasyPremierLeagueViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { FantasyPremierLeagueViewModel(get()) }
}
