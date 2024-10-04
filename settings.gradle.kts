pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "FantasyPremierLeague"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app", ":common")
include("compose-desktop")
