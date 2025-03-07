import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // android
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    // kotlin
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose.compiler) apply false
    alias(libs.plugins.kotlin.compose.multiplatform) apply false
    // google
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    // others
    alias(libs.plugins.buildconfig) apply false
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kover)
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    ktlint {
        enableExperimentalRules.set(true)
        additionalEditorconfig.set(
            mapOf(
                "ktlint_standard_package-naming" to "disabled",
            ),
        )
        reporters {
            reporter(ReporterType.JSON)
        }
        filter {
            exclude {
                projectDir.toURI().relativize(it.file.toURI()).path.contains("/generated/")
            }
            exclude {
                projectDir.toURI().relativize(it.file.toURI()).path.contains("/build/")
            }
        }
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlinx.kover")
}

dependencies {
    kover(project(":shared-datasources:remote"))
}

kover {
    reports {

        total {
            xml {
                onCheck = true
                xmlFile = layout.projectDirectory.file("kover.xml")
            }
        }
        filters {
            excludes {
                androidGeneratedClasses()
                classes("**/build/**")
            }
        }
        verify {
            rule {
                bound {
                    minValue = 50
                    maxValue = 75
                }
            }
        }
    }
}
