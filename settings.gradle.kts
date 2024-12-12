enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    includeBuild("modules-conventions/conventions")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "streeek"

// module folders
include("modules-ui")
include("modules-app")
include("modules-data")
include("modules-sources")
include("modules-features")

// sources modules
include(":modules-sources:remote")
include(":modules-sources:local")

// data modules
include(":modules-data:data")
include(":modules-data:domain")

// ui modules
include(":modules-ui:presentation")
include(":modules-ui:resources")
include(":modules-ui:design")
include(":modules-ui:common")

include(":modules-app:streeek")
include(":modules-features:landing")
include(":modules-features:authentication")
include(":modules-features:tabs")
