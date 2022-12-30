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

compose {
    kotlinCompilerPlugin.set("1.4.0-dev-k1.8.0-RC-4c1865595ed")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
        )
    }
}

application {
    mainClass.set("MainKt")
}