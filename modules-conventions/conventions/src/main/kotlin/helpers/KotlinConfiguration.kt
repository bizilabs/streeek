package helpers

import AndroidSdk
import com.android.build.api.dsl.CommonExtension
import extensions.kotlinOptions
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.provideDelegate

internal fun Project.configureKotlinAndroid(
    extension: CommonExtension<*, *, *, *, *>,
) {
    extension.apply {
        compileSdk = AndroidSdk.COMPILE_SDK
        defaultConfig {
            minSdk = AndroidSdk.MINIMUM_SDK
        }
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }
        buildFeatures {
            buildConfig = true
        }
        kotlinOptions {
            val warningsAsErrors: String? by project
            allWarningsAsErrors = warningsAsErrors.toBoolean()
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-opt-in=kotlinx.coroutines.FlowPreview",
                "-opt-in=kotlin.Experimental",
            )
            jvmTarget = JavaVersion.VERSION_21.toString()
        }
    }
}