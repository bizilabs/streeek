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
        versionCode = 2
        versionName = "0.0.2"
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
