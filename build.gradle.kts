// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false

}

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal() // Add this for the protobuf Gradle plugin

    }
    dependencies {
        classpath(libs.secrets.gradle.plugin)
    }
}

