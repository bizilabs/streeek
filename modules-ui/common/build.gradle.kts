plugins {
    id("bizilabs.convention.library")
    id("bizilabs.convention.compose.library")
}

android {
    namespace = "com.bizilabs.streeek.lib.common"
}

dependencies {

    // <editor-fold desc="modules">
    api(projects.modulesUi.resources)
    api(projects.modulesUi.design)
    // </editor-fold>

    // <editor-fold desc="libraries">
    // voyager
    implementation(libs.bundles.voyager)
    // paging
    implementation(libs.androidx.paging.compose)
    // coil
    implementation(libs.bundles.coil)
    // </editor-fold>

}
