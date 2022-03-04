pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}


rootProject.name = "FantasyPremierLeague"

include(":app", "compose-desktop", ":common")
