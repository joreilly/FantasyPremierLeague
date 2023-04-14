plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("com.android.library")
    id("io.realm.kotlin")
    id("org.jetbrains.kotlin.native.cocoapods")
    id("com.google.devtools.ksp")
    id("com.rickclephas.kmp.nativecoroutines")
}

// CocoaPods requires the podspec to have a version.
version = "1.0"

android {
    compileSdk = AndroidSdk.compile
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = AndroidSdk.min
        targetSdk = AndroidSdk.target

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    namespace = "dev.johnoreilly.common"
}

kotlin {
    val iosTarget: (String, org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget.() -> Unit) -> org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget = when {
        System.getenv("SDK_NAME")?.startsWith("iphoneos") == true -> ::iosArm64
        System.getenv("NATIVE_ARCH")?.startsWith("arm") == true -> ::iosSimulatorArm64 // available to KT 1.5.30
        else -> ::iosX64
    }
    iosTarget("iOS") {}


    android()
    jvm()

    cocoapods {
        summary = "Fantasy Football Premier League"
        homepage = "Link to a Kotlin/Native module homepage"
        framework {
            baseName = "FantasyPremierLeagueKit"
        }
    }

    sourceSets {

        sourceSets["commonMain"].dependencies {
            with(Deps.Ktor) {
                implementation(clientCore)
                implementation(clientJson)
                implementation(clientLogging)
                implementation(clientSerialization)
                implementation(contentNegotiation)
                implementation(json)
            }

            with(Deps.Kotlinx) {
                implementation(coroutinesCore)
                implementation(serializationCore)
                api(dateTime)
            }

            // Realm
            implementation(Deps.realm)

            // koin
            api(Deps.Koin.core)
            api(Deps.Koin.test)

            api(Deps.multiplatformSettings)
            api(Deps.multiplatformSettingsCoroutines)


            // kermit
            api(Deps.kermit)
        }
        sourceSets["commonTest"].dependencies {
        }

        sourceSets["androidMain"].dependencies {
            implementation(Deps.Ktor.clientAndroid)
        }

        sourceSets["jvmMain"].dependencies {
            implementation(Deps.Ktor.clientJava)
            implementation(Deps.Ktor.slf4j)

            implementation("org.nield:kotlin-statistics:1.2.1")
            implementation("org.ojalgo:okalgo:0.0.2")
            implementation("org.jetbrains.kotlinx:multik-api:0.1.1")
            implementation("org.jetbrains.kotlinx:multik-jvm:0.1.0")
        }

        sourceSets["iOSMain"].dependencies {
            implementation(Deps.Ktor.clientIos)
        }
        sourceSets["iOSTest"].dependencies {
        }
    }
}


tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}


tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xuse-experimental=kotlin.time.ExperimentalTime")
    }
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
}

