plugins {
    id("java-library")
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

dependencies {
    implementation(project(":domain:usecases"))
    implementation(project(":domain:model"))
    implementation(project(":data:local"))
    implementation(project(":data:remote"))
    implementation(project(":data:repository"))
    implementation(project(":data:sync"))
    api(project(":auth:apptoken"))

    api(libs.dagger)
    annotationProcessor(libs.dagger.compiler)

    implementation(libs.rxjava3)
    implementation(libs.rxjava3.jdk9.interop)

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.retrofit.rxjava)
    api(libs.gson);
}
