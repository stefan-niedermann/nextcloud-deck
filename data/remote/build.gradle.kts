import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    id("java-library")
    alias(libs.plugins.openapiGenerator)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

val generatedSourcesDir = "$projectDir/build/openapi"

fun GenerateTask.configureCommon(spec: String, pkg: String) {
    generatorName.set("java")
    inputSpec.set("$projectDir/src/main/resources/$spec")
    outputDir.set(generatedSourcesDir)
    apiPackage.set("it.niedermann.nextcloud.remote.$pkg.api")
    modelPackage.set("it.niedermann.nextcloud.remote.$pkg.dto")
    generateApiTests.set(false)
    generateModelTests.set(false)
    globalProperties.set(mapOf(
        "models" to "",
        "modelDocs" to "false",
        "apis" to "false",
        "supportingFiles" to "false"
    ))
    configOptions.set(mapOf(
        "dateLibrary" to "java8",
        "serializationLibrary" to "gson",
        "useRuntimeException" to "true",
        "library" to "retrofit2",
        "useJakartaHtmlTag" to "true",
        "openApiNullable" to "false"
    ))
}

val generateDeckApi = tasks.register<GenerateTask>("generateDeckApi") {
    configureCommon("deck-api.yaml", "deck")
}

val generateOcsApi = tasks.register<GenerateTask>("generateOcsApi") {
    configureCommon("ocs-api.yaml", "ocs")
}

// Disable the default task or configure it to do nothing
tasks.named("openApiGenerate") {
    enabled = false
    dependsOn(generateDeckApi, generateOcsApi)
}

sourceSets {
    main {
        java {
            srcDir("$generatedSourcesDir/src/main/java")
        }
    }
}

dependencies {
    api(project(":domain:model"))
    api(project(":data:shared"))

    api(libs.jakarta.inject)

    api(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.retrofit.rxjava)
    implementation(libs.gson)

    implementation(libs.rxjava3)
    implementation(libs.rxjava3.jdk9.interop)

    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
    
    // Dependencies for generated code
    implementation("io.swagger.core.v3:swagger-annotations:2.2.22")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    testImplementation(platform(libs.junitBom))
    testImplementation(libs.junitJupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.named("compileJava") {
    dependsOn(generateDeckApi, generateOcsApi)
}
