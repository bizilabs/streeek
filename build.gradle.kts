import kotlinx.kover.gradle.plugin.dsl.AggregationType
import kotlinx.kover.gradle.plugin.dsl.KoverReportExtension
import kotlinx.kover.gradle.plugin.dsl.MetricType
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
    apply(plugin = "org.jetbrains.kotlinx.kover")
    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
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
            exclude("**/generated/**")
            exclude("**/build/**")
            exclude("**/shared_datasources/**")
        }
    }
    configure<KoverReportExtension> {
        defaults {
            xml {
                onCheck = true
                setReportFile(layout.buildDirectory.file("custom/reports/kover/report.xml"))
            }
        }
        filters {
            excludes {
                classes("**/build/**")
            }
        }
        verify {
            rule("Basic Line Coverage") {
                isEnabled = true
                bound {
                    minValue = 75
                    metric = MetricType.LINE
                    aggregation = AggregationType.COVERED_PERCENTAGE
                }
            }

            rule("Branch Coverage") {
                isEnabled = true
                bound {
                    minValue = 75
                    metric = MetricType.BRANCH
                }
            }
        }
    }
}
