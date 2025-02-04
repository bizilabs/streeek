import com.android.build.api.dsl.ApplicationExtension
import extensions.getLibrary
import extensions.implementation
import helpers.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class ApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }
            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = AndroidSdk.TARGET_SDK
                buildTypes {
                    debug {
                        applicationIdSuffix = ".debug"
                        versionNameSuffix = "-debug"
                    }
                    release {
                        isMinifyEnabled = true
                        isShrinkResources = true
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro",
                        )
                    }
                    create("beta") {
                        applicationIdSuffix = ".beta"
                        versionNameSuffix = "-beta"
                        isMinifyEnabled = true
                        isShrinkResources = true
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro",
                        )
                    }
                }
            }
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            dependencies {
                // modules
                implementation(project(AndroidModules.Ui.presentation))
                implementation(project(AndroidModules.Ui.common))
                implementation(project(AndroidModules.Data.data))

                // work manager
                implementation(libs.getLibrary("androidx-work-runtime"))
                implementation(libs.getLibrary("androidx-work-testing"))
            }
        }
    }
}