package dev.johnoreilly.common.di

import dev.johnoreilly.common.viewmodel.FixturesViewModel
import dev.johnoreilly.common.viewmodel.LeaguesViewModel
import dev.johnoreilly.common.viewmodel.PlayerDetailsViewModel
import dev.johnoreilly.common.viewmodel.PlayerListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::PlayerListViewModel)
    viewModelOf(::PlayerDetailsViewModel)
    viewModelOf(::FixturesViewModel)
    viewModelOf(::LeaguesViewModel)
}
