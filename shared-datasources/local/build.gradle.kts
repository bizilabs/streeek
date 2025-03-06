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

            kotlin.srcDir("build/generated/ksp/metadata/commoMain/kotlin")
        }
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspCommonMainMetadata", libs.androidx.room.compiler)
}

tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*>>().configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

tasks.named("runKtlintCheckOverCommonMainSourceSet") {
    mustRunAfter("kspCommonMainKotlinMetadata")
}

android {
    namespace = "com.bizilabs.streeek.lib.local"
}

room {
    schemaDirectory("$projectDir/schemas")
}
