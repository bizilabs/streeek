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
            }

            kotlin.srcDir("build/generated/ksp/metadata")
        }
    }
}

dependencies {
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
