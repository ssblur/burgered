import net.fabricmc.loom.api.LoomGradleExtensionAPI
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
    id("dev.architectury.loom") version "1.7-SNAPSHOT" apply false
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    `maven-publish`
}

architectury {
    minecraft = rootProject.property("minecraft_version").toString()
}

subprojects {
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "maven-publish")

    val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")

    dependencies {
        "minecraft"("com.mojang:minecraft:${project.property("minecraft_version")}")
        "mappings"(loom.officialMojangMappings())
    }

    val projectVersion = rootProject.property("mod_version").toString()
    publishing {
        publications {
            create<MavenPublication>("${project.property("mod_id")}") {
                groupId = project.property("maven_group").toString()
                artifactId = rootProject.property("archives_base_name").toString() + "-${project.name}"
                version = if (projectVersion.toString().contains("beta")) "$projectVersion-SNAPSHOT" else projectVersion

                from(components["java"])
            }
        }

        repositories {
            maven(if (projectVersion.contains("beta")) "https://maven.wiredtomato.net/snapshots" else "https://maven.wiredtomato.net/releases") {
                name = "wtRepo"
                credentials {
                    username = System.getenv("MAVEN_USERNAME")
                    password = System.getenv("MAVEN_PASSWORD")
                }
            }
        }
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "architectury-plugin")
    apply(plugin = "maven-publish")

    base.archivesName = rootProject.property("archives_base_name").toString() + "-${project.name}"
    version = rootProject.property("mod_version").toString()
    group = rootProject.property("maven_group").toString()

    repositories {
        maven("https://maven.shedaniel.me/")
        maven("https://maven.terraformersmc.com/releases/")
    }

    dependencies {
        compileOnly(kotlin("stdlib"))
        compileOnly(kotlin("reflect"))
        compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = 21
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_21
        }
    }

    java {
        withSourcesJar()
    }
}
