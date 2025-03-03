import com.android.build.gradle.LibraryExtension
import extensions.getBundle
import extensions.getLibrary
import extensions.implementation
import extensions.testImplementation
import helpers.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class FeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(AndroidConvention.library)
                apply(AndroidConvention.koin)
                apply(AndroidConvention.Compose.library)
            }
            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = AndroidSdk.targetSdk
            }
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            configurations.configureEach {
                resolutionStrategy {
                    force(libs.findLibrary("junit").get())
                    // Temporary workaround for https://issuetracker.google.com/174733673
                    force("org.objenesis:objenesis:2.6")
                }
            }
            dependencies {
                // modules
                implementation(project(AndroidModules.Ui.common))
                implementation(project(AndroidModules.Data.domain))
                // androidx
                implementation(libs.getLibrary("androidx-core-ktx"))
                implementation(libs.getLibrary("androidx-appcompat"))
                // coroutines
                implementation(libs.getLibrary("kotlinx-coroutines-android"))
                testImplementation(libs.getLibrary("kotlinx-coroutines-test"))
                // voyager
                implementation(libs.getBundle("voyager"))
            }
        }
    }
}