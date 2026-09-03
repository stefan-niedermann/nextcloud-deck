plugins {
    java
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

dependencies {
    implementation(project(":domain:model"))

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}
