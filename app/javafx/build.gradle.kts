plugins {
    java
    application
    alias(libs.plugins.gradleup.shadow)
    alias(libs.plugins.javafx)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(26)
    }
    modularity.inferModulePath.set(false)
}

application {
    mainClass.set("it.niedermann.nextcloud.deck.javafx.Launcher")
}

javafx {
    version = "26"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation(project(":app:shared"))
    implementation(project(":auth:webloginflowv2"))
    implementation(libs.materialColorUtilities)

    implementation(libs.dagger)
    annotationProcessor(libs.dagger.compiler)

    implementation(libs.rxjava4)

    implementation(libs.openjfx.controls)
    implementation(libs.openjfx.fxml)
    implementation(libs.ikonli.javafx)
    implementation(libs.gemsfx)
    implementation(libs.ikonli.fluentui)
    implementation(libs.jsystemthemedetector) {
        exclude(group = "net.java.dev.jna", module = "jna-platform")
    }

    implementation(libs.jpro.mdfx) {
        exclude(group = "org.openjfx", module = "jfx-incubator-input")
        exclude(group = "org.openjfx", module = "jfx-incubator-richtext")
    }

    testImplementation(platform(libs.junitBom))
    testImplementation(libs.junitJupiter)
    testRuntimeOnly(libs.junitPlatformLauncher)
    testImplementation(libs.mockito.core)
    testImplementation(libs.hamcrest)
    testImplementation(libs.testfx.core)
    testImplementation(libs.testfx.junit5)
    testImplementation(libs.testfx.monocle)
    testImplementation(libs.assertj.core)
}

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("junit.jupiter.extensions.autodetection.enabled", "true")
    systemProperty("testfx.robot", "glass")
    systemProperty("testfx.headless", "false")
    jvmArgs(
        "--add-exports=javafx.graphics/com.sun.glass.ui=ALL-UNNAMED",
        "--add-exports=javafx.graphics/com.sun.javafx.application=ALL-UNNAMED",
        "--add-opens=javafx.graphics/com.sun.glass.ui=ALL-UNNAMED",
        "--add-opens=javafx.graphics/com.sun.javafx.application=ALL-UNNAMED",
        "--add-opens=javafx.graphics/com.sun.javafx.stage=ALL-UNNAMED",
        "--add-opens=javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED",
        "--add-opens=javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED",
        "--add-opens=javafx.base/com.sun.javafx.binding=ALL-UNNAMED",
        "--add-opens=javafx.base/com.sun.javafx.event=ALL-UNNAMED"
    )
}

tasks.named<Tar>("distTar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<AbstractArchiveTask>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
