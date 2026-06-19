plugins {
    id("java-library")
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
dependencies {
    implementation(project(":domain:model"))

    api(libs.jakarta.inject)

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.retrofit.rxjava)
    implementation(libs.gson);

    implementation(libs.rxjava3)
    implementation(libs.rxjava3.jdk9.interop)

    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
}