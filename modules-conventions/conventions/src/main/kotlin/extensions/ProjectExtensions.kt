package extensions

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.PluginManager
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *>,
) {

    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

    commonExtension.apply {

        buildFeatures {
            compose = true
        }

        kotlinOptions {
            freeCompilerArgs = freeCompilerArgs
        }

        dependencies {
            // compose
            val bom = libs.findLibrary("compose-bom").get()
            implementation(platform(bom))
            implementation(libs.getBundle("compose"))
            androidTestImplementation(platform(bom))
            debugImplementation(libs.getLibrary("compose-ui-tooling"))
            debugImplementation(libs.getLibrary("compose-ui-test-manifest"))
            androidTestImplementation(libs.getLibrary("compose-ui-test-junit4"))
        }
    }
}

internal fun Project.plugins(block: PluginManager.() -> Unit) {
    with(pluginManager) { block() }
}

internal fun Project.multiplatform(block: KotlinMultiplatformExtension.() -> Unit) {
    extensions.configure<KotlinMultiplatformExtension> { block() }
}

internal fun Project.androidLibrary(block: LibraryExtension.() -> Unit) {
    extensions.configure<LibraryExtension> { block() }
}

internal fun Project.androidApplication(block: ApplicationExtension.() -> Unit) {
    extensions.configure<ApplicationExtension> { block() }
}
