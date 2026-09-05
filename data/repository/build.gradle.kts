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
    api(project(":data:shared"))
    implementation(project(":domain:state"))
    implementation(project(":domain:repository"))
    implementation(project(":data:local"))
    implementation(project(":data:remote"))

    api(libs.jakarta.inject)
    implementation(libs.rxjava3)
    implementation(libs.rxjava3.jdk9.interop)
    implementation(libs.kotlinx.coroutines.jdk8)
    implementation(libs.openpdf)
}
