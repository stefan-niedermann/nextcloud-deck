plugins {
    java
    application
    alias(libs.plugins.gradleup.shadow)
    alias(libs.plugins.javafx)
    alias(libs.plugins.jlink)
    alias(libs.plugins.graalvm)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(26)
    }
}

application {
    mainClass.set("it.niedermann.nextcloud.deck.javafx.JavaFxApplication")
}

javafx {
    version = "26"
    modules = listOf("javafx.controls", "javafx.fxml")
}

jlink {
    options = listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages")
    launcher {
        name = "Deck PC"
        jvmArgs = listOf("--add-modules javafx.controls,javafx.fxml")
    }
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("deck-pc")

            buildArgs.addAll(
                listOf(
                    "--no-fallback",
                    "-H:+ReportExceptionStackTraces"
                )
            )
        }
    }
}

dependencies {
    implementation(project(":app:shared"))
    implementation(project(":auth:webloginflowv2"))

    implementation(libs.dagger)
    annotationProcessor(libs.dagger.compiler)

    implementation(libs.rxjava4)

    implementation(libs.openjfx.controls)
    implementation(libs.openjfx.fxml)
    implementation(libs.ikonli.javafx)
    implementation(libs.gemsfx)
    implementation(libs.ikonli.fluentui)
    implementation(libs.jsystemthemedetector)

    implementation(libs.jpro.mdfx)

    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
}

tasks.named<Tar>("distTar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<AbstractArchiveTask>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<JavaExec> {
    jvmArgs = listOf(
        "--module-path", configurations.runtimeClasspath.get().asPath,
        "--add-modules", "javafx.controls,javafx.fxml",
        "--enable-native-access", "javafx.graphics,ALL-UNNAMED"
    )
}