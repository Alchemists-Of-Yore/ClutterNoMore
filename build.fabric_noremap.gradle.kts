@file:Suppress("UnstableApiUsage")

plugins {
    id("common")
    id("net.fabricmc.fabric-loom")
    id("dev.kikugie.postprocess.jsonlang")
    id("me.modmuss50.mod-publish-plugin")
    id("maven-publish")
}

repositories {
    mavenLocal()
    maven("https://maven.shedaniel.me/") {
        name = "shedaniel (Cloth Config)"
        content { includeGroupAndSubgroups("me.shedaniel") }
    }
    maven("https://maven.terraformersmc.com/releases/") {
        name = "Terraformers (Mod Menu)"
        content { includeGroupAndSubgroups("com.terraformersmc") }
    }
    maven("https://api.modrinth.com/maven") {
        name = "Modrinth"
        content { includeGroupAndSubgroups("maven.modrinth") }
    }
    maven("https://repo.sleeping.town/") {
        name = "Sisby Maven"
        content { includeGroupAndSubgroups("folk.sisby") }
    }
    maven("https://maven.isxander.dev/releases") {
        name = "Xander Maven"
        content {
            includeGroupAndSubgroups("dev.isxander")
            includeGroupAndSubgroups("org.quiltmc.parsers")
        }
    }
    maven("https://maven.blamejared.com/") {
        name = "JEI"
        content { includeGroup("mezz.jei") }
    }
    exclusiveContent {
        forRepository {
            maven("https://maven.cassian.cc") { name = "Cassian's Maven" }
        }
        filter { includeGroupAndSubgroups("cc.cassian") }
    }
}

val accesswidener = "${mc}.accesswidener"

tasks.named<ProcessResources>("processResources") {
    dependsOn(":${stonecutter.current.project}:stonecutterGenerate")

    val props = mapOf(
        "mod_version" to "${prop("mod.version")}+${mc}",
        "minecraft" to dep("minecraft_version_range"),
        "loader_version_range" to dep("loader_version_range"),
        "mod_license" to prop("mod.license"),
        "mod_description" to prop("mod.description"),
        "mod_id" to prop("mod.id"),
        "mod_name" to prop("mod.name"),
        "mod_authors" to prop("mod.authors"),
        "minecraft_version_range" to dep("minecraft_version_range"),
        "aw_file" to accesswidener,
    )

    filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml", "META-INF/mods.toml")) {
        expand(props)
    }
    inputs.properties(props)
}

loom {
    accessWidenerPath = rootProject.file("src/main/resources/accesswideners/$accesswidener")
}

jsonlang {
    languageDirectories = listOf("assets/${prop("mod.id")}/lang")
    prettyPrint = true
}

dependencies {
    minecraft("com.mojang:minecraft:${mc}")
    implementation("net.fabricmc:fabric-loader:${dep("fabric-loader")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${dep("fabric-api")}")

    implementation("folk.sisby:kaleido-config:${dep("kaleido")}")
    include("folk.sisby:kaleido-config:${dep("kaleido")}")
    compileOnly("mezz.jei:jei-1.21.11-neoforge-api:${dep("jei")}")
    implementation("cc.cassian.rrv:reliable-recipe-viewer-fabric:${dep("rrv")}")

    implementation("com.terraformersmc:modmenu:${dep("modmenu")}")


    if (hasProperty("deps.mousetweaks")) {
        implementation("maven.modrinth:mouse-tweaks:${dep("mousetweaks")}")
    }
}

configurations.all {
    resolutionStrategy {
        force("net.fabricmc:fabric-loader:${dep("fabric-loader")}")
    }
}

tasks {
    processResources {
        exclude("**/neoforge.mods.toml", "**/mods.toml")
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        from(jar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${prop("mod.version")}"))
        dependsOn("build")
    }
}

loom.runs.named("server") {
    isIdeConfigGenerated = false
}

publishMods {
    file = tasks.jar.map { it.archiveFile.get() }

    type = STABLE
    displayName = "${prop("mod.name")} ${prop("mod.version")} for $mc Fabric"
    version = "${prop("mod.version")}+${mc}-fabric"
    changelog = provider { rootProject.file("CHANGELOG.md").readText() }
    modLoaders.add("fabric")

    modrinth {
        projectId = prop("publish.modrinth")
        accessToken = env.MODRINTH_API_KEY.orNull()
        minecraftVersions.add(mc)
        minecraftVersions.addAll(additionalVersions)
        requires("fabric-api")
    }

    curseforge {
        projectId = prop("publish.curseforge")
        accessToken = env.CURSEFORGE_API_KEY.orNull()
        minecraftVersions.add(prop("publish.curseforge_minecraft_version"))
        minecraftVersions.addAll(additionalVersions)
        requires("fabric-api")
    }
}
