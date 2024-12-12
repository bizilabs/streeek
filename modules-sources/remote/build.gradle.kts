import extensions.findProperties
import org.jetbrains.kotlin.konan.properties.Properties

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("bizilabs.convention.module")
}

android {
    namespace = "com.bizilabs.streeek.lib.remote"
    val properties = findProperties(file = "local.properties")
    buildTypes {
        debug {
            buildConfigField("String", "GithubClientId", "\"${properties.getProperty("github.client.id")}\"")
            buildConfigField("String", "GithubClientName", "\"${properties.getProperty("github.client.name")}\"")
            buildConfigField("String", "GithubClientSecret", "\"${properties.getProperty("github.client.secret")}\"")
            buildConfigField("String", "GithubClientRedirectUrl", "\"${properties.getProperty("github.client.redirect.url")}\"")
        }
    }
}

dependencies {
    // ktor
    implementation(libs.bundles.ktor)
    // firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.authentication)
    // okhttp
    implementation(platform(libs.okhttp.bom))
    implementation(libs.bundles.okhttp)
    // datastore
    implementation(libs.androidx.datastore)
}
