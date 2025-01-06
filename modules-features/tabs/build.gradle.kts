import org.gradle.kotlin.dsl.android

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
}
