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
    modularity.inferModulePath.set(false)
}

sourceSets {
    main {
        java {
            srcDir("src/main/resources")
        }
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
    moduleName.set("it.niedermann.nextcloud.deck.javafx")
    mergedModuleName.set("it.niedermann.nextcloud.deck.javafx")
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    launcher {
        name = "Deck PC"
    }
    mergedModule {
        additive = true
        forceMerge("jna")
        forceMerge("javafx")
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
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<Tar>("distTar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<AbstractArchiveTask>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
