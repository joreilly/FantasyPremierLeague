package dev.johnoreilly.fantasypremierleague.di

import dev.johnoreilly.fantasypremierleague.presentation.players.PlayersViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { PlayersViewModel(get(),get()) }
}
