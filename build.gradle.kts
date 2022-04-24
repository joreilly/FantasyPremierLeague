
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}")
        classpath("org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}")
        classpath("com.rickclephas.kmp:kmp-nativecoroutines-gradle-plugin:${Versions.kmpNativeCoroutinesVersion}")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}


