import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    `java-library`
    `maven-publish`
    idea
    id("net.neoforged.moddev") version "2.0.141"
    kotlin("jvm") version "2.3.0"
}

version = property("mod_version") as String
group = property("mod_group_id") as String

repositories {
    mavenLocal()
    maven {
        name = "Kotlin for Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
        content {
            includeGroup("thedarkcolour")
        }
    }
}

base {
    archivesName.set(property("mod_id") as String)
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))
kotlin.jvmToolchain(21)

neoForge {
    version = property("neo_version") as String

    runs {
        create("client") {
            client()
            systemProperty("neoforge.enabledGameTestNamespaces", property("mod_id") as String)
        }

        create("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", property("mod_id") as String)
        }

        create("gameTestServer") {
            type = "gameTestServer"
            systemProperty("neoforge.enabledGameTestNamespaces", property("mod_id") as String)
        }

        create("data") {
            data()
            programArguments.addAll(
                "--mod", property("mod_id") as String,
                "--all",
                "--output", file("src/generated/resources/").absolutePath,
                "--existing", file("src/main/resources/").absolutePath
            )
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods {
        register(property("mod_id") as String) {
            sourceSet(sourceSets["main"])
        }
    }
}

sourceSets["main"].resources {
    srcDir("src/generated/resources")
}

dependencies {
    implementation("thedarkcolour:kotlinforforge-neoforge:5.3.0")
}

val generateModMetadata = tasks.register<Copy>("generateModMetadata") {
    val replaceProperties = mapOf(
        "minecraft_version" to providers.gradleProperty("minecraft_version"),
        "minecraft_version_range" to providers.gradleProperty("minecraft_version_range"),
        "neo_version" to providers.gradleProperty("neo_version"),
        "neo_version_range" to providers.gradleProperty("neo_version_range"),
        "loader_version_range" to providers.gradleProperty("loader_version_range"),
        "mod_id" to providers.gradleProperty("mod_id"),
        "mod_name" to providers.gradleProperty("mod_name"),
        "mod_license" to providers.gradleProperty("mod_license"),
        "mod_version" to providers.gradleProperty("mod_version"),
        "mod_authors" to providers.gradleProperty("mod_authors"),
        "mod_description" to providers.gradleProperty("mod_description")
    )
    inputs.properties(replaceProperties)
    from("src/main/templates") {
        expand(replaceProperties.mapValues { (_, v) -> v.get() })
    }
    into("build/generated/sources/modMetadata")
}

sourceSets["main"].resources.srcDir(generateModMetadata)
tasks.named("classes") {
    dependsOn(generateModMetadata)
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("file://${project.projectDir}/repo")
        }
    }
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

