plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kmpNativeCoroutines) apply false
    alias(libs.plugins.realm.kotlin) apply false
}

//allprojects {
//    configurations.all {
//        resolutionStrategy.dependencySubstitution {
//            substitute(module("org.jetbrains.compose.compiler:compiler")).apply {
//                using(module("androidx.compose.compiler:compiler:${libs.versions.androidxComposeCompiler.get()}"))
//            }
//        }
//    }
//}

