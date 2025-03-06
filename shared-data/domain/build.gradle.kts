plugins {
    alias(libs.plugins.bizilabs.multiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.uri)
            implementation(libs.paging.cashapp.common)
        }
        commonTest.dependencies {
            implementation(libs.paging.cashapp.testing)
        }
        androidMain.dependencies {}
        iosMain.dependencies {}
    }
}

android {
    namespace = "com.bizilabs.streeek.lib.domain"
}
