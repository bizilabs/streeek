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
        versionCode = System.getenv("VERSION_CODE")?.toInt() ?: 2
        versionName = System.getenv("VERSION_NAME")?.toString() ?: "0.0.2"
    }
}

dependencies {
    // work-manager
    implementation(libs.androidx.work.runtime)
}
