import extensions.findProperties

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("bizilabs.convention.module")
}

android {
    namespace = "com.bizilabs.streeek.lib.remote"
    val properties = findProperties(file = "local.properties")

    defaultConfig {
        // github secrets
        buildConfigField("String", "GithubClientId", "\"${properties.getProperty("github.client.id")}\"")
        buildConfigField("String", "GithubClientName", "\"${properties.getProperty("github.client.name")}\"")
        buildConfigField("String", "GithubClientSecret", "\"${properties.getProperty("github.client.secret")}\"")
        buildConfigField("String", "GithubClientRedirectUrl", "\"${properties.getProperty("github.client.redirect.url")}\"")
        // supabase secrets
        buildConfigField("String", "SupabaseUrl", "\"${properties.getProperty("supabase.url")}\"")
        buildConfigField("String", "SupabaseKey", "\"${properties.getProperty("supabase.key")}\"")
        // streeek secrets
        buildConfigField("String", "StreeekUrl", "\"${properties.getProperty("streeek.api")}\"")
    }
}

dependencies {
    // ktor
    implementation(libs.bundles.ktor)
    // supabase
    implementation(platform(libs.supabase.bom))
    implementation(libs.bundles.supabase)
    // okhttp
    implementation(platform(libs.okhttp.bom))
    implementation(libs.bundles.okhttp)
    // datastore
    implementation(libs.androidx.datastore)
    // chucker
    debugImplementation(libs.chucker.debug)
    releaseImplementation(libs.chucker.release)
    betaImplementation(libs.chucker.release)
}
