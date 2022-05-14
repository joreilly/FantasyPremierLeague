package dev.johnoreilly.common

import io.ktor.client.engine.darwin.*
import org.koin.dsl.module


actual fun platformModule() = module {
    single { Darwin.create() }
}



