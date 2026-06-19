pluginManagement {
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
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "Nextcloud Deck"
include(":app:android")
include(":app:cli")
include(":app:javafx")
include(":app:shared")
include(":domain:usecases")
include(":domain:model")
include(":domain:sync")
include(":domain:repository")
include(":data:repository")
include(":data:sync")
include(":data:local")
include(":data:remote")
include(":auth:apptoken")
include(":auth:webloginflowv2")
include(":auth:sso")
