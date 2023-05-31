import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version libs.versions.composeMultiplatform
    application
}

group = "me.joreilly"
version = "1.0-SNAPSHOT"

dependencies {
    //implementation("io.github.koalaplot:koalaplot-core:0.1.0-SNAPSHOT")
    implementation(compose.desktop.currentOs)
    implementation(project(":common"))
}

application {
    mainClass.set("MainKt")
}

compose {
    kotlinCompilerPlugin.set(libs.versions.jbComposeCompiler)
    kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=1.9.0-Beta")
}
