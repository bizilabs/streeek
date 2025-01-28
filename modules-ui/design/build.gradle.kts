plugins {
    id("bizilabs.convention.library")
    id("bizilabs.convention.compose.library")
}

android {
    namespace = "com.bizilabs.streeek.lib.design"
}

dependencies {

    /**
     * MODULES
     */
    implementation(projects.modulesUi.resources)

    /**
     * LIBRARIES
     */

    implementation(libs.androidx.core.splashscreen)
    implementation(libs.google.firebase.database)
}
