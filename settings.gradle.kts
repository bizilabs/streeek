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
include(":modules-features:setup")
include(":modules-features:profile")
include(":modules-features:team")
include(":modules-features:issues")
include(":modules-features:issue")
include(":modules-features:leaderboard")
include(":modules-features:points")
include(":modules-features:updater")
include(":modules-features:join")
include(":modules-features:notifications")
include(":modules-features:services")
include(":modules-features:onboarding")
include(":modules-features:reminders")
include(":modules-features:reviews")
