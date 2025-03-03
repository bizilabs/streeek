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

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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
        fun createPlugin(value: String, className: String) {
            plugins.register(value) {
                id = value
                implementationClass = className
            }
        }
        createPlugin("bizilabs.multiplatform.module", "conventions.MultiplatformModuleConvention")
    }
}