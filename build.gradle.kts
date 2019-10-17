import org.asciidoctor.gradle.AsciidoctorTask

plugins {
    id("org.asciidoctor.convert") version "1.5.9.2"
}

version = "1.0"

tasks {

    named<AsciidoctorTask>("asciidoctor") {
        sourceDir = file(rootDir.resolve("adoc"))
        outputDir = file(rootDir.resolve("docs"))
    }

}
