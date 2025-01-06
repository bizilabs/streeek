@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("bizilabs.convention.module")
}

android {
    namespace = "com.bizilabs.streeek.lib.data"
}

dependencies {

    // modules
    implementation(projects.modulesSources.remote)
    implementation(projects.modulesSources.local)
    implementation(projects.modulesData.domain)

    // paging
    implementation(libs.androidx.paging.runtime)

}