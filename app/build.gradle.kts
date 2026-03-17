plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose)
    alias(libs.plugins.google.services)
    alias(libs.plugins.google.crashlytics)
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    namespace = "luph.vulcanizerv3.updates"

    androidResources {
        generateLocaleConfig = true
    }

    defaultConfig {
        applicationId = "luph.vulcanizerv3.updates"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 2
        versionName = "v3a3.5d6"
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resourceConfigurations.plus(
            listOf(
                "ar", "cs", "es-rES", "he", "it", "pl", "ro", "sk", "tr", "vi",
                "bg", "de", "fr", "id", "ko", "pt-rBR", "ru", "sq", "uk", "zh-rCN"
            )
        )
    }

    signingConfigs {
        // Important: change the keystore for a production deployment
        val userKeystore = File(System.getProperty("user.home"), ".android/debug.keystore")
        val localKeystore = rootProject.file("debug_2.keystore")
        val hasKeyInfo = userKeystore.exists()
        create("release") {
            // get from env variables
            storeFile = if (hasKeyInfo) userKeystore else localKeystore
            storePassword = if (hasKeyInfo) "android" else System.getenv("compose_store_password")
            keyAlias = if (hasKeyInfo) "androiddebugkey" else System.getenv("compose_key_alias")
            keyPassword = if (hasKeyInfo) "android" else System.getenv("compose_key_password")
        }
    }

    configurations.all {
        exclude("org.jetbrains", "annotations-java5")
    }



    buildTypes {
        getByName("debug") {

        }

        getByName("release") {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }

    // Tests can be Robolectric or instrumented tests
    sourceSets {
        val sharedTestDir = "src/sharedTest/java"
        getByName("test") {
            java.srcDir(sharedTestDir)
        }
        getByName("androidTest") {
            java.srcDir(sharedTestDir)
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}
kotlin {
    jvmToolchain(17)
}

dependencies {
    // AndroidX Libraries
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.inappmessaging.display)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive.navigationSuite)
    implementation(libs.androidx.compose.materialWindow)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.window)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.animation.graphics)
    implementation(libs.androidx.foundation)
    implementation("com.google.accompanist:accompanist-pager:0.36.0")

    // Google Libraries
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    // Kotlin Libraries
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.core)

    // Compose Libraries
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Jetpack Libraries
    implementation(libs.jetpack.loading)
    implementation(libs.pencilloader)

    // download Libraries
    implementation(libs.photo.compos)
    implementation(libs.photo.zoomable)
    implementation(libs.coil.kt)
    implementation(libs.coil.kt.base)
    implementation(libs.coil.kt.compose)
    implementation(libs.coil.gif)
    implementation(libs.ketch)

    // Ktor Libraries
    implementation(libs.ktor.core)
    implementation(libs.ktor.cio)
    implementation(libs.ktor.negotiation)
    implementation(libs.ktor.json)

    implementation(libs.compose.shimmer)
    implementation(libs.konfetti.compose)
    implementation(libs.compose.markdown)
    implementation(libs.library)
    implementation(libs.lottie.compose)
    implementation(libs.versioncompare)
    implementation(libs.twyper)
    implementation(libs.ohteepee)
    implementation(libs.sdk)
    implementation(libs.haze)
    implementation(libs.fileutils)
    val ackpineVersion = "0.10.1"
    implementation("ru.solrudev.ackpine:ackpine-core:$ackpineVersion")
    implementation("ru.solrudev.ackpine:ackpine-ktx:$ackpineVersion")
    implementation("ru.solrudev.ackpine:ackpine-splits:$ackpineVersion")
    implementation("ru.solrudev.ackpine:ackpine-splits-ktx:$ackpineVersion")
    implementation("ru.solrudev.ackpine:ackpine-assets:$ackpineVersion")}
