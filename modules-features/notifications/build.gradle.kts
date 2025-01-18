plugins {
    id("bizilabs.convention.feature")
}

android {
    namespace = "com.bizilabs.streeek.feature.notification"
}

dependencies {
    // material
    implementation(libs.compose.material)
    implementation(libs.androidx.foundation.android)
    // paging
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
    implementation(libs.firebase.messaging.ktx)
}
