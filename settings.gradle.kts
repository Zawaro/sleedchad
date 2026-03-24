pluginManagement {
    plugins {
        id("org.jlleitschuh.gradle.ktlint") version "14.0.0"
    }
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

rootProject.name = "SleepChad"
include(":app")
