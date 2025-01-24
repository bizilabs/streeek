@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("bizilabs.convention.module")
}

android {
    namespace = "com.bizilabs.streeek.lib.domain"
}

dependencies {
    // modules
    implementation(projects.modulesUi.resources)
    // work-manager
    implementation(libs.androidx.work.runtime)
    // paging
    implementation(libs.androidx.paging.runtime)
    // firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)
}
