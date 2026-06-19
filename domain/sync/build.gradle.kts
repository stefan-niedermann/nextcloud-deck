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

    implementation(libs.rxjava3)
    implementation(libs.rxjava3.jdk9.interop)
}