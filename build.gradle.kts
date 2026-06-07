plugins {
    id("com.android.application") version "8.13.2" apply false
    kotlin("android") version "1.9.25" apply false
    id("org.jlleitschuh.gradle.ktlint") version "13.0.0" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
    id("com.google.devtools.ksp") version "1.9.25-1.0.20" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}
