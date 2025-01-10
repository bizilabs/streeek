plugins {
    id("bizilabs.convention.feature")
}

android {
    namespace = "com.bizilabs.streeek.feature.leaderboard"
}

dependencies {

    // paging
    implementation(libs.bundles.paging)

    // coil
    implementation(libs.bundles.coil)

}
