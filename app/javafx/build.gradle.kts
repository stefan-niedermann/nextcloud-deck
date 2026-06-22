plugins {
    java
    application
    alias(libs.plugins.gradleup.shadow)
    alias(libs.plugins.javafx)
    alias(libs.plugins.jlink)
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
        name = "Deck Desktop"
        jvmArgs = listOf("--add-modules javafx.controls,javafx.fxml")
    }
}

dependencies {
    implementation(project(":app:shared"))
    implementation(project(":domain:usecases"))
    implementation(project(":domain:model"))
    implementation(project(":data:local"))
    implementation(project(":data:remote"))
    implementation(project(":auth:webloginflowv2"))
    implementation(project(":auth:apptoken"))

    implementation(libs.dagger)
    annotationProcessor(libs.dagger.compiler)

    implementation(libs.rxjava4)

    implementation(libs.openjfx.controls)
    implementation(libs.openjfx.fxml)
    implementation(libs.ikonli.javafx)
    implementation(libs.ikonli.fluentui)
    implementation(libs.jsystemthemedetector)

    implementation(libs.jpro.mdfx)
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