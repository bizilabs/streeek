plugins {
    id("bizilabs.convention.feature")
}

android {
    namespace = "com.bizilabs.streeek.feature.reviews"
}

dependencies {
    implementation(libs.google.inapp.reviews.ktx)
}
