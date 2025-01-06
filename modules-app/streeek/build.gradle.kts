plugins {
    id("bizilabs.convention.application")
    id("bizilabs.convention.koin")
    id("bizilabs.convention.compose.application")
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {

    namespace = "com.bizilabs.app.streeek.mobile"

    defaultConfig {
        applicationId = "com.bizilabs.app.streeek.mobile"
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}

dependencies {
    // work-manager
    implementation(libs.androidx.work.runtime)
}
