package extensions

import org.gradle.api.artifacts.VersionCatalog

fun VersionCatalog.getPlugin(alias: String) = this.findPlugin(alias).get().get().pluginId

fun VersionCatalog.getLibrary(alias: String) = this.findLibrary(alias).get()

fun VersionCatalog.getBundle(alias: String) = this.findBundle(alias).get()
