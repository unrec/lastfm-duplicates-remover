import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

version = "0.0.1"
group = "com.unrec"
description = "lastfm-duplicates-remover"
java.sourceCompatibility = JavaVersion.VERSION_11

object Versions {

    const val KOTLIN = "1.8.0"
    const val COROUTINES = "1.6.4"
    const val JACKSON = "2.14.0"
}

plugins {
    kotlin("jvm") version "1.8.0"
    id("maven-publish")
    application
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation(group = "io.kotest", name = "kotest-assertions-core-jvm", version = "5.5.1")

    implementation("org.seleniumhq.selenium:selenium-java:4.8.1")

    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", Versions.JACKSON)
    implementation("com.fasterxml.jackson.dataformat", "jackson-dataformat-csv", Versions.JACKSON)
    implementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", Versions.JACKSON)

    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation("ch.qos.logback:logback-core:1.4.5")
}

application {
    mainClass.set("com.unrec.lastfm.duplicates.remover.AppKt")
}

tasks {

    val fatJar = register<Jar>("fatJar") {
        dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources"))

        archiveClassifier.set("standalone")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest {
            attributes(mapOf("Main-Class" to application.mainClass))
        }

        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) } + sourcesMain.output
        from(contents)
    }

    build {
        dependsOn(fatJar)
    }

    test {
        useJUnitPlatform()
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xinline-classes")
        }
    }
}

publishing {
    publications.create<MavenPublication>("artifact").from(components["java"])
    repositories.mavenLocal()
}

repositories {
    mavenCentral()
}
