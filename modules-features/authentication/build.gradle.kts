plugins {
    id("bizilabs.convention.feature")
}

android {
    namespace = "com.bizilabs.streeek.feature.authentication"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    // uri
    implementation(libs.uri)
}
