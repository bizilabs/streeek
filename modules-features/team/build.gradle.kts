import org.gradle.kotlin.dsl.android

plugins {
    id("bizilabs.convention.feature")
}

android {
    namespace = "com.bizilabs.streeek.feature.team"
}

dependencies {
    // coil
    implementation(libs.bundles.coil)
    // saket-swipe
    implementation(libs.saket.swipe)
    // confetti
    implementation(libs.confetti)
}
