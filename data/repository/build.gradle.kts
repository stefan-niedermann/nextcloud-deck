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
    implementation(project(":domain:repository"))
    implementation(project(":data:local"))
    implementation(project(":data:remote"))

    api(libs.jakarta.inject)
    implementation(libs.rxjava3)
    implementation(libs.rxjava3.jdk9.interop)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.10.2")
}
