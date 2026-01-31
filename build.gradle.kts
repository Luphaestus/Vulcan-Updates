plugins {
    alias(libs.plugins.gradle.versions)
    alias(libs.plugins.version.catalog.update)
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.google.crashlytics) apply false
    alias(libs.plugins.firebase.perf) apply false

}

apply("${project.rootDir}/buildscripts/toml-updater-config.gradle")
