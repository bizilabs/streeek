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
    implementation(libs.bundles.voyager)
    // </editor-fold>

    // paging
    implementation(libs.androidx.paging.compose)

}