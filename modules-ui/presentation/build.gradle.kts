plugins {
    id("bizilabs.convention.library")
    id("bizilabs.convention.koin")
    id("bizilabs.convention.compose.library")
}

android {
    namespace = "com.bizilabs.streeek.lib.presentation"
}

dependencies {

    // modules
    implementation(projects.modulesUi.common)
    implementation(projects.modulesData.data)
    implementation(projects.modulesData.domain)

    // features
    implementation(projects.modulesFeatures.landing)
    implementation(projects.modulesFeatures.authentication)
    implementation(projects.modulesFeatures.tabs)
    implementation(projects.modulesFeatures.setup)
    implementation(projects.modulesFeatures.profile)
    implementation(projects.modulesFeatures.team)
    implementation(projects.modulesFeatures.notifications)

    // splash screen
    implementation(libs.androidx.core.splashscreen)

    // voyager
    implementation(libs.bundles.voyager)

    // firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)
}
