plugins {
    id("bizilabs.multiplatform.module")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // ktor
            implementation(libs.bundles.kmp.ktor)
            // supabase
//            implementation(platform(libs.supabase.bom))
//            implementation(libs.bundles.supabase)
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
