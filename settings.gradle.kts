pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev/")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev/")
    }
}

rootProject.name = "FantasyPremierLeague"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app", ":common")
include("compose-desktop")
include("mcp-server")
