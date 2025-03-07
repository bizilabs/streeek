plugins {
    alias(libs.plugins.bizilabs.multiplatform)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.androidx.room)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.androidx.room.runtime)
                implementation(libs.bundles.androidx.datastore)
            }

            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
        }
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosX64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
}

tasks.named("runKtlintCheckOverCommonMainSourceSet") {
    mustRunAfter("kspCommonMainKotlinMetadata")
}

android {
    namespace = "com.bizilabs.streeek.shared.lib.local"
}

room {
    schemaDirectory("$projectDir/schemas")
}
