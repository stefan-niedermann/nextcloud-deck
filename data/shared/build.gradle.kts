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

    // Lombok for data layer as per AGENTS.md
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}
