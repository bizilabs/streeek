import extensions.findProperties

plugins {
    id("bizilabs.multiplatform.module")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // ktor
            implementation(libs.bundles.kmp.ktor)
            // supabase
            implementation(project.dependencies.platform(libs.supabase.bom))
            implementation(libs.bundles.supabase)
            // uri
            implementation(libs.uri)
            // datastore
            implementation(libs.bundles.androidx.datastore)
            // multiplatform settings
            implementation(libs.bundles.multiplatform.settings)
        }
        commonTest.dependencies {
            implementation(libs.ktor.client.mock)
            implementation(libs.multiplatform.settings.tests)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "com.bizilabs.streeek.lib.remote"
}

buildConfig {
    className("StreeekRemoteConfig")
    val properties = findProperties(file = "local.properties")
    // github secrets
    buildConfigField("GithubClientId", "\"${properties.getProperty("github.client.id")}\"")
    buildConfigField("GithubClientName", "\"${properties.getProperty("github.client.name")}\"")
    buildConfigField("GithubClientSecret", "\"${properties.getProperty("github.client.secret")}\"")
    buildConfigField("GithubClientRedirectUrl", "\"${properties.getProperty("github.client.redirect.url")}\"")
    // supabase secrets
    buildConfigField("SupabaseUrl", "\"${properties.getProperty("supabase.url")}\"")
    buildConfigField("SupabaseKey", "\"${properties.getProperty("supabase.key")}\"")
}
