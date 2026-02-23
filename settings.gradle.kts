pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
        maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.kikugie.stonecutter") version "0.7.11"
}

stonecutter {
    create(rootProject) {
        fun match(version: String, vararg loaders: String) = loaders
            .forEach {
                if (it == "fabric" && stonecutter.eval(version, ">1.21.11"))
                    version("$version-$it", version).buildscript = "build.fabric_noremap.gradle.kts"
                else
                    version("$version-$it", version).buildscript = "build.$it.gradle.kts"
            }
        match("1.20.1", "forge")
        match("1.21.1", "fabric", "neoforge")
        match("1.21.10", "fabric")
        match("26.1", "fabric")

        vcsVersion = "1.21.1-neoforge"
    }
}
