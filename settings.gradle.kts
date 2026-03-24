pluginManagement {
    plugins {
        id("com.android.application") version "8.13.2" apply false
        kotlin("android") version "1.9.25" apply false
    }
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

rootProject.name = "SleepChad"
include(":app")
