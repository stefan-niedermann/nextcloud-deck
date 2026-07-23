plugins {
    application
    alias(libs.plugins.gradleup.shadow)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass.set("it.niedermann.nextcloud.deck.cli.CliApplication")
}

tasks.named<Tar>("distTar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
tasks.withType<AbstractArchiveTask>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

dependencies {
    implementation(project(":app:shared"))

    implementation(libs.dagger)
    annotationProcessor(libs.dagger.compiler)

    implementation(libs.info.picocli)
    annotationProcessor(libs.info.picocli.codegen)

    implementation(libs.rxjava3)
    implementation(libs.rxjava3.jdk9.interop)
}