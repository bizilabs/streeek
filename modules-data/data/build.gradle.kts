@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("bizilabs.convention.module")
}

android {
    namespace = "com.bizilabs.streeek.lib.data"
}

dependencies {
    // modules
    implementation(projects.sharedDatasources.remote)
    implementation(projects.sharedDatasources.local)
    implementation(projects.sharedData.domain)
    implementation(projects.modulesUi.resources)
    // work-manager
    implementation(libs.androidx.work.runtime)
    // paging
    implementation(libs.androidx.paging.runtime)
    // uri
    implementation(libs.uri)
    // firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)
}
