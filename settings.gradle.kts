pluginManagement {
    listOf(repositories, dependencyResolutionManagement.repositories).forEach {
        it.apply {
            google()
            gradlePluginPortal()
            mavenCentral()
            maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
            maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
            maven("https://androidx.dev/storage/compose-compiler/repository")
        }
    }
}

rootProject.name = "FantasyPremierLeague"

include(":app", ":common")
include("compose-desktop")
