plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

group = "com.bizilabs.convention"

// Configure the build-logic plugins to target JDK 22
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.android.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("koin-convention") {
            id = "bizilabs.convention.koin"
            implementationClass = "KoinConventionPlugin"
        }
        register("application-convention") {
            id = "bizilabs.convention.application"
            implementationClass = "ApplicationConventionPlugin"
        }
        register("library-convention") {
            id = "bizilabs.convention.library"
            implementationClass = "LibraryConventionPlugin"
        }
        register("compose-application-convention") {
            id = "bizilabs.convention.compose.application"
            implementationClass = "ComposeApplicationConventionPlugin"
        }
        register("compose-library-convention") {
            id = "bizilabs.convention.compose.library"
            implementationClass = "ComposeLibraryConventionPlugin"
        }
        register("module-convention") {
            id = "bizilabs.convention.module"
            implementationClass = "ModuleConventionPlugin"
        }
        register("feature-convention") {
            id = "bizilabs.convention.feature"
            implementationClass = "FeatureConventionPlugin"
        }
    }
}