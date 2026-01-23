plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.detekt) apply false
    //alias(libs.plugins.skie) apply false
}


subprojects {
    afterEvaluate {
        if (plugins.hasPlugin("org.jetbrains.kotlin.jvm") ||
            plugins.hasPlugin("org.jetbrains.kotlin.android") ||
            plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
            apply(plugin = "dev.detekt")

            extensions.configure<dev.detekt.gradle.extensions.DetektExtension> {
                debug = false
                allRules = false
                disableDefaultRuleSets = true
                buildUponDefaultConfig = false
                baseline.set(file("baseline.xml"))
            }

            dependencies {
                "detektPlugins"("dev.uitest:compose-rules:0.1.4.13")
            }
        }
    }
}
