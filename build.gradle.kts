import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    id("com.android.application") version "8.13.2" apply false
    kotlin("android") version "1.9.25" apply false
    id("org.jlleitschuh.gradle.ktlint") version "13.0.0"
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    configure<KtlintExtension> {
        android.set(true)
        filter {
            exclude("**/generated/**")
        }
    }
}
