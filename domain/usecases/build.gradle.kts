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
    api(project(":domain:repository"))
    api(project(":domain:sync"))

    api(libs.jakarta.inject)
    implementation(project(":data:remote"))
    implementation(libs.rxjava3)
    implementation(libs.rxjava3.jdk9.interop)

    testImplementation(platform(libs.junitBom))
    testImplementation(libs.junitJupiter)
    testRuntimeOnly(libs.junitPlatformLauncher)
    testAnnotationProcessor(libs.dagger.compiler)
    testImplementation(project(":app:shared"))
}

tasks.withType<Test> {
    useJUnitPlatform()
    failOnNoDiscoveredTests = false
    if (!project.hasProperty("includeE2E")) {
        exclude("**/it/niedermann/nextcloud/deck/domain/e2e/**")
    }
}
