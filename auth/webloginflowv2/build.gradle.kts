plugins {
    id("java-library")
}
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
dependencies {
    implementation(libs.jakarta.inject)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
}