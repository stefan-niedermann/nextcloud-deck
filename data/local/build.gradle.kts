import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.androidx.room3)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

room3 {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    api(project(":domain:model"))

    api(libs.jakarta.inject)

    implementation(libs.androidx.sqlite)
    api(libs.androidx.room3.runtime)
    ksp(libs.androidx.room3.compiler)
    implementation(libs.kotlinx.coroutines.rx3)
    implementation(libs.kotlinx.coroutines.core.jvm)
    implementation(libs.rxjava3)
    implementation(libs.rxjava3.jdk9.interop)

    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
}
