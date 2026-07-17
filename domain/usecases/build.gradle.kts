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
}