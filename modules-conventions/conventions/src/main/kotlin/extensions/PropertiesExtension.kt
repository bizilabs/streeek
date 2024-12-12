package extensions

import org.gradle.api.Project
import org.jetbrains.kotlin.konan.properties.Properties

fun Project.findProperties(file: String): Properties {
    val properties = Properties()
    properties.load(project.rootProject.file(file).reader())
    return properties
}