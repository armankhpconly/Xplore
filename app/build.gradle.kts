import com.android.build.api.dsl.Packaging
import com.android.build.api.variant.BuildConfigField
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.xplore"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.xplore"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())
        buildConfigField("String", "MapsAPI", properties.getProperty("Maps"))
        buildConfigField("String", "Sentiment", properties.getProperty("Sentiment"))



    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    packaging {
        resources.excludes.add("META-INF/INDEX.LIST")
        resources.excludes.add("META-INF/DEPENDENCIES")
    }
}

configurations.all {
    resolutionStrategy {

    }
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.androidx.appcompat.v141)
    implementation(libs.material.v150)
    implementation(libs.androidx.constraintlayout.v213)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    implementation(libs.firebase.auth) {
    }

    implementation(libs.com.google.firebase.firebase.firestore.ktx)
    implementation(libs.firebase.analytics)

    implementation(libs.android.maps.utils.v223)
    implementation(libs.play.services.maps.v1802)
    implementation(libs.play.services.location.v1901)
    implementation(libs.play.services.places.v1700)

    implementation(libs.glide.v4130)
    implementation(libs.androidx.navigation.fragment.ktx)
    annotationProcessor(libs.compiler.v4130)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    implementation(libs.google.auth.library.oauth2.http.v180) {
    }

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v113)
    androidTestImplementation(libs.androidx.espresso.core.v340)
}
