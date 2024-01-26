import org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_1_9

plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("com.android.library")
    id("org.jetbrains.compose") version libs.versions.composeMultiplatform
    id("io.realm.kotlin")
    id("com.google.devtools.ksp")
    id("co.touchlab.skie")
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
            baseName = "FantasyPremierLeagueKit"
        }
    }

    androidTarget()
    jvm()


    compilerOptions {
        languageVersion.set(KOTLIN_1_9)
    }

    sourceSets {

        @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines)
            implementation(libs.kotlinx.serialization)
            api(libs.kotlinx.datetime)

            api(libs.koin.core)
            implementation(libs.koin.test)

            implementation(libs.bundles.ktor.common)
            implementation(libs.realm)
            api(libs.bundles.multiplatformSettings)
            api(libs.kermit)

            implementation(compose.ui)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)

            implementation("io.github.koalaplot:koalaplot-core:0.5.2")
            implementation(libs.image.loader)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.android)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.ios)
        }

        jvmMain.dependencies {
            implementation(libs.ktor.client.java)
            implementation(libs.slf4j)

            implementation("org.nield:kotlin-statistics:1.2.1")
            implementation("org.ojalgo:okalgo:0.0.2")
            implementation("org.jetbrains.kotlinx:multik-api:0.1.1")
            implementation("org.jetbrains.kotlinx:multik-jvm:0.1.1")
        }
    }
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
}

compose {
    kotlinCompilerPlugin.set(libs.versions.jbComposeCompiler.get())
    kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=1.9.20")
}