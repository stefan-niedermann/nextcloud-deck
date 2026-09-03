plugins {
    id("java-library")
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
dependencies {
    api(project(":domain:model"))
    api(project(":domain:state"))
    api(project(":domain:repository"))
    api(project(":domain:sync"))

    api(libs.jakarta.inject)
    implementation(libs.rxjava3)
    implementation(libs.rxjava3.jdk9.interop)

    testAnnotationProcessor(libs.dagger.compiler)
    testRuntimeOnly(libs.junitPlatformLauncher)
    testImplementation(libs.junitJupiter)
    testImplementation(platform(libs.junitBom))
    testImplementation(project(":app:shared"))
    testImplementation(project(":auth:apptoken"))
}

tasks.withType<Test> {
    useJUnitPlatform()
    failOnNoDiscoveredTests = false
    if (project.hasProperty("includeE2E")) {
        val parallelLimit = project.findProperty("e2eParallel")?.toString()?.toInt() ?: 1
        maxParallelForks = parallelLimit
        systemProperty("junit.jupiter.execution.parallel.enabled", "false")
    } else {
        maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
        exclude("**/it/niedermann/nextcloud/deck/domain/e2e/**")
    }
}
