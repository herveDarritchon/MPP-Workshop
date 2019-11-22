import groovy.json.JsonOutput
import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

plugins {
    id("org.asciidoctor.jvm.convert") version "2.3.0"
    kotlin("js") version "1.3.50"
    id("org.ajoberstar.git-publish") version "2.1.1"
}

version = "1.0"

val adocStyle = "golo"
val highlightStyle = "idea"

repositories {
    jcenter()
    maven( url = "https://kotlin.bintray.com/kotlin-js-wrappers")
}

asciidoctorj {
    setVersion("2.1.0")

    modules {
        diagram.setVersion("1.5.18")
    }
}

tasks {
    create<Sync>("syncHighlightjs") {
        group = "documentation"
        from(rootDir.resolve("highlightjs/highlight.pack.js")) {
            rename { "highlight.min.js" }
        }
        from(rootDir.resolve("highlightjs/styles/$highlightStyle.css")) {
            rename { "$highlightStyle.min.css" }
            into("styles")
        }
        into(buildDir.resolve("pages/highlightjs"))
    }

    create<Sync>("syncImages") {
        group = "documentation"
        from(rootDir.resolve("adoc/res"))
        into(buildDir.resolve("pages/images/res"))
    }

    create<Copy>("syncStylesheet") {
        group = "documentation"
        from(rootDir.resolve("stylesheets/$adocStyle.css"))
        into(buildDir.resolve("pages"))
    }

    getByName<AsciidoctorTask>("asciidoctor") {
        dependsOn("syncImages")
        dependsOn("syncStylesheet")
        dependsOn("syncHighlightjs")
        attributes = mapOf(
                "imagesdir" to "images",
                "stylesheet" to "$adocStyle.css",
                "source-highlighter" to "highlightjs",
                "highlightjsdir" to "highlightjs",
                "linkcss" to "true",
                "sectanchors" to "true",
                "nofooter" to "true",
                "highlightjs-theme" to highlightStyle
        )

        setSourceDir(rootDir.resolve("adoc"))
        setOutputDir(buildDir.resolve("pages"))
    }
    getByName("assemble").dependsOn("asciidoctor")

    create("pagesJson") {
        inputs.dir(rootDir.resolve("adoc"))
        outputs.file(buildDir.resolve("pages/pages.json"))
        doLast {
            val titles = rootDir.resolve("adoc").list()!!
                    .filter { it.endsWith(".adoc") }
                    .sorted()
                    .map { rootDir.resolve("adoc/$it").useLines { it.first() } }
                    .map { it.trimMargin("=").trim() }

            buildDir.resolve("pages/pages.json").writeText(JsonOutput.prettyPrint(JsonOutput.toJson(titles)))
        }
    }
    getByName("assemble").dependsOn("pagesJson")

    create("compileAll") {
        dependsOn("asciidoctor", "pagesJson", "compileKotlinJs")
    }
}

kotlin {
    target {
        browser()

        (tasks[compilations["main"].processResourcesTaskName] as ProcessResources).apply {
            dependsOn("asciidoctor")
            dependsOn("pagesJson")
            from(buildDir.resolve("pages"))
        }

        compilations["main"].kotlinOptions {
            moduleKind = "commonjs"
        }

        sourceSets["main"].dependencies {
            implementation(kotlin("stdlib-js"))

            val reactVersion = "16.9.0"
            val reactRouterVersion = "4.3.1"
            val kotlinWrapperVersion = "pre.87-kotlin-1.3.50"

            api("org.jetbrains:kotlin-react-dom:$reactVersion-$kotlinWrapperVersion")
            api("org.jetbrains:kotlin-react-router-dom:$reactRouterVersion-$kotlinWrapperVersion")
            implementation("org.jetbrains:kotlin-styled:1.0.0-$kotlinWrapperVersion")

            implementation(npm("react", "^$reactVersion"))
            implementation(npm("react-dom", "^$reactVersion"))
            implementation(npm("react-router", "^$reactRouterVersion"))
            implementation(npm("react-router-dom", "^$reactRouterVersion"))

            implementation(npm("css-in-js-utils", "^3.0.2"))
            implementation(npm("inline-style-prefixer", "^5.1.0"))
            implementation(npm("styled-components", "^4.3.2"))
            implementation(npm("core-js", "^3.0.0"))
        }
    }
}

gitPublish {
    repoUri.set("git@github.com:SalomonBrys/MPP-Workshop.git")
    branch.set("gh-pages")

    contents {
        val processResources = tasks["processResources"] as ProcessResources
        from(processResources.outputs.files)
        val browserWebpack = tasks["browserWebpack"] as KotlinWebpack
        from(browserWebpack.archiveFile)
    }

    preserve {
        include("CNAME")
    }
}

tasks["gitPublishCopy"].dependsOn("assemble")