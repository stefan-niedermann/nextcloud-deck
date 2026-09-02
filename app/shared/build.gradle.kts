plugins {
    id("java-library")
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

dependencies {
    api(project(":domain:usecases"))
    api(project(":domain:model"))
    api(project(":domain:state"))
    api(project(":data:local"))
    api(project(":data:remote"))
    api(project(":data:repository"))
    api(project(":data:sync"))
    api(project(":auth:webloginflowv2"))

    api(libs.dagger)
    annotationProcessor(libs.dagger.compiler)

    api(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)

    implementation(libs.rxjava3)
    implementation(libs.rxjava3.jdk9.interop)

    implementation(libs.retrofit)
    api(libs.retrofit.gson)
    implementation(libs.retrofit.rxjava)
    api(libs.gson);

    testImplementation(platform(libs.junitBom))
    testImplementation(libs.junitJupiter)
    testRuntimeOnly(libs.junitPlatformLauncher)
}

tasks.test {
    useJUnitPlatform()
}
