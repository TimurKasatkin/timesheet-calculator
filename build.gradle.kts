import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.jar.Attributes

plugins {
    kotlin("jvm") version "1.4.21"
    application
    id("org.openjfx.javafxplugin") version "0.0.9"
}

group = "ru.tkasatkin.timesheet_calculator"
version = "1.0-SNAPSHOT"

val javaFxVersion = "15.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("no.tornado", "tornadofx", "1.7.17")

    runtimeOnly("org.openjfx:javafx-graphics:$javaFxVersion:win")
    runtimeOnly("org.openjfx:javafx-graphics:$javaFxVersion:linux")
    runtimeOnly("org.openjfx:javafx-graphics:$javaFxVersion:mac")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

javafx {
    version = javaFxVersion
    modules = listOf("javafx.controls")
}

application {
    mainClass.set("ru.tkasatkin.timesheet_calculator.TimeSheetCalculatorAppKt")
}


val jar by tasks.getting(Jar::class) {
    manifest {
        attributes[Attributes.Name.MAIN_CLASS.toString()] = application.mainClass.get()
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }) {
        exclude("META-INF/MANIFEST.MF", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    }
}