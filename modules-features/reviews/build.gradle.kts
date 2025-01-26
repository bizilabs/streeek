plugins {
    id("bizilabs.convention.feature")
}

android {
    namespace = "com.bizilabs.streeek.feature.reviews"
}
dependencies {
    // reviews
    implementation(libs.google.inapp.reviews.ktx)
}
