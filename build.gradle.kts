import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.jar.Attributes

plugins {
    kotlin("jvm") version "1.3.31"
    application
}

group = "ru.tkasatkin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("no.tornado", "tornadofx", "1.7.17")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "ru.tkasatkin.timesheet_calculator.TimeSheetCalculatorApp"
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes[Attributes.Name.MAIN_CLASS.toString()] = application.mainClassName
        attributes[Attributes.Name.CLASS_PATH.toString()] =
            configurations.compile.joinToString(separator = " ") { it.name }
    }
    from(configurations.runtimeClasspath.map { if (it.isDirectory) it else zipTree(it) }) {
        exclude("META-INF/MANIFEST.MF", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    }
}