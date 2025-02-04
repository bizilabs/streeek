package extensions

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

fun CommonExtension<*, *, *, *, *, *, *>.kotlinOptions(block: KotlinCompilerOptions.() -> Unit) {
    (this as ExtensionAware).extensions.configure("kotlinOptions", block)
}