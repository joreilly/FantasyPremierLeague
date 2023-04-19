import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version Versions.composeDesktop
    application
}

group = "me.joreilly"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    //implementation("io.github.koalaplot:koalaplot-core:0.1.0-SNAPSHOT")
    implementation(compose.desktop.currentOs)
    implementation(project(":common"))
}

tasks.withType<KotlinCompile>() {
    kotlinOptions {
        freeCompilerArgs += listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
        )
    }
}

application {
    mainClass.set("MainKt")
}