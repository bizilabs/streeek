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

}
