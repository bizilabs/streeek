package conventions

import com.android.build.gradle.LibraryExtension
import extensions.androidLibrary
import extensions.getBundle
import extensions.getLibrary
import extensions.getPlugin
import extensions.multiplatform
import extensions.plugins
import org.gradle.api.JavaVersion
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

private fun KotlinMultiplatformExtension.configureIOS() {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            isStatic = true
        }
    }
}

private fun KotlinMultiplatformExtension.configureAndroid() {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
}

private fun KotlinMultiplatformExtension.sources(block: NamedDomainObjectContainer<KotlinSourceSet>.()-> Unit) {
    sourceSets.block()
}

private fun LibraryExtension.configureAndroidLibrary() {
    compileSdk = AndroidSdk.compileSdk
    defaultConfig {
        minSdk = AndroidSdk.compileSdk
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

class MultiplatformModuleConvention : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val catalogs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            // setup module plugins
            plugins {
                apply(catalogs.getPlugin("kotlin-multiplatform"))
                apply(catalogs.getPlugin("android-library"))
                apply(catalogs.getPlugin("kotlin-serialization"))
                apply(catalogs.getPlugin("ktlint"))
            }
            // setup multiplatform
            multiplatform {
                configureAndroid()
                configureIOS()
                sources {
                    commonMain.dependencies {
                        // kotlinx
                        implementation(catalogs.getLibrary("kotlinx-datetime"))
                        implementation(catalogs.getLibrary("kotlinx-coroutines-core"))
                        implementation(catalogs.getLibrary("kotlinx-serialization-json"))
                        // koin
                        implementation(project.dependencies.platform(catalogs.getLibrary("koin-bom")))
                        implementation(catalogs.getBundle("kmp-koin"))
                    }
                    commonTest.dependencies {
                        implementation(catalogs.getLibrary("kotlinx-coroutines-test"))
                        // koin
                        implementation(catalogs.getLibrary("koin-test"))
                        implementation(catalogs.getLibrary("koin-test-junit4"))
                        implementation(catalogs.getLibrary("koin-test-junit5"))
                    }
                    androidMain.dependencies {
                        implementation(catalogs.getLibrary("kotlinx-coroutines-android"))
                    }
                }
            }
            // setup android
            androidLibrary {
                configureAndroidLibrary()
            }
        }
    }
}
