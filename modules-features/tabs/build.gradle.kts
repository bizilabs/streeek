plugins {
    id("bizilabs.convention.feature")
}

android {
    namespace = "com.bizilabs.streeek.feature.tabs"
}

dependencies {
    // coil
    implementation(libs.bundles.coil)
    // calendar
    implementation(libs.calendar)
    // material
    implementation(libs.compose.material)
    implementation(libs.androidx.foundation.android)
    // confetti
    implementation(libs.confetti)
    // paging
    implementation(libs.bundles.paging)

    // in app reviews
    implementation(projects.modulesFeatures.reviews)
}
