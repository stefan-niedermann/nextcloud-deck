plugins {
    java
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

dependencies {
    // Only used for with-Syntax until JEP-468 is shipped: https://openjdk.org/jeps/468
    annotationProcessor(libs.record.builder.processor)
    compileOnly(libs.record.builder.core)
}