// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
}

subprojects {
    tasks.withType<Jar> {
        manifest {
            attributes("Automatic-Module-Name" to "it.niedermann.nextcloud.deck.${project.path.substring(1).replace(":", ".")}")
        }
    }
    afterEvaluate {
        if (plugins.hasPlugin("java")) {
            extensions.configure<JavaPluginExtension> {
                modularity.inferModulePath.set(false)
            }
        }
    }
}
