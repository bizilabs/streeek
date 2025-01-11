import org.gradle.kotlin.dsl.android

plugins {
    id("bizilabs.convention.feature")
}

android {
    namespace = "com.bizilabs.streeek.feature.issues"
}

dependencies {
    // coil
    implementation(libs.bundles.coil)
    // paging
    implementation(libs.bundles.paging)
}
