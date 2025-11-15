@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    id("com.android.library")
}

kotlin {
    jvmToolchain(17)
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    namespace = "dev.johnoreilly.common"
}

kotlin {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            export(libs.androidx.lifecycle.viewmodel)
            baseName = "FantasyPremierLeagueKit"
        }
    }

    androidTarget()
    jvm()

    sourceSets {
        all {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }

        @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines)
            implementation(libs.kotlinx.serialization)
            api(libs.kotlinx.datetime)

            implementation(libs.androidx.navigation3.ui)
            //implementation(libs.androidx.navigation3.event)
            implementation(libs.androidx.navigation3.material3.adaptive)

            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.composeVM)
            implementation(libs.koin.test)

            implementation(libs.bundles.ktor.common)
            api(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.compose)
            implementation(libs.androidx.room.runtime)
            implementation(libs.sqlite.bundled)
            implementation(libs.datastore.preferences)
            api(libs.kermit)

            implementation(compose.ui)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.materialIconsExtended)

            implementation(libs.koalaplot.core)
            implementation(libs.image.loader)

            implementation(libs.koog.agents)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(libs.androidx.compose.ui.tooling)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.ios)
        }

        jvmMain.dependencies {
            implementation(libs.ktor.client.java)
            implementation(libs.slf4j)

            implementation(libs.kotlin.statistics)
            implementation(libs.okalgo)
            implementation(libs.multik.api)
            implementation(libs.multik.jvm)
        }
    }
}

dependencies {
    ksp(libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}
