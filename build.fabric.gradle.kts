@file:Suppress("UnstableApiUsage")

plugins {
    id("common")
    id("net.fabricmc.fabric-loom-remap")
    id("dev.kikugie.postprocess.jsonlang")
    id("me.modmuss50.mod-publish-plugin")
}

repositories {
    mavenLocal()
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1") {
        name = "DevAuth"
        content { includeGroup("me.djtheredstoner") }
    }
    maven("https://cursemaven.com") {
        name = "Curse Maven"
        content { includeGroupAndSubgroups("curse.maven") }
    }
    maven("https://thedarkcolour.github.io/KotlinForForge/") {
        name = "Kotlin for Forge"
        content { includeGroupAndSubgroups("thedarkcolour") }
    }
    maven("https://maven.greenhouse.lgbt/releases/") {
        name = "Greenhouse Maven"
        content {
            includeGroup("house.greenhouse")
            includeGroup("umpaz.brewinandchewin")
        }
    }
    maven("https://maven.terraformersmc.com/releases/") {
        name = "Terraformers (Mod Menu)"
        content {
            includeGroupAndSubgroups("com.terraformersmc")
            includeGroupAndSubgroups("dev.emi")
        }
    }
    maven("https://api.modrinth.com/maven") {
        name = "Modrinth"
        content { includeGroupAndSubgroups("maven.modrinth") }
    }
    maven("https://repo.sleeping.town/") {
        name = "Sisby Maven"
        content { includeGroupAndSubgroups("folk.sisby") }
    }
    maven("https://maven.parchmentmc.org") {
        name = "Parchment Mappings"
        content { includeGroupAndSubgroups("org.parchmentmc") }
    }
    maven("https://maven.isxander.dev/releases") {
        name = "Xander Maven"
        content {
            includeGroupAndSubgroups("dev.isxander")
            includeGroupAndSubgroups("org.quiltmc.parsers")
        }
    }
    maven {
        name = "Quilt Maven"
        url = uri("https://maven.quiltmc.org/repository/release/")
        content {
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
    dependsOn("stonecutterGenerate")

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

fabricApi {
    configureDataGeneration {
        client = true
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${mc}")
    mappings(loom.layered {
        officialMojangMappings()
        if (hasProperty("deps.parchment"))
            parchment("org.parchmentmc.data:parchment-${dep("parchment")}@zip")
    })
    modImplementation("net.fabricmc:fabric-loader:${dep("fabric-loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${dep("fabric-api")}")

    modImplementation("com.terraformersmc:modmenu:${dep("modmenu")}")

    // YACL - required by McQoy
    if (hasProperty("deps.yacl")) {
        modLocalRuntime("dev.isxander:yet-another-config-lib:${dep("yacl")}-fabric")
    }
    modLocalRuntime("maven.modrinth:mcqoy:${dep("mcqoy")}")

    if (hasProperty("deps.emi")) {
        modCompileOnly("dev.emi:emi-fabric:${dep("emi")}")
        modLocalRuntime("dev.emi:emi-fabric:${dep("emi")}")
    }
    if (hasProperty("deps.pyrite")) {
        modLocalRuntime("maven.modrinth:pyrite:${dep("pyrite")}")
    }

    // JEI's API does not currently remap properly, so the full jar is used
    if (hasProperty("deps.jei")) {
        modCompileOnly("mezz.jei:jei-${mc}-fabric:${dep("jei")}")
        modLocalRuntime("mezz.jei:jei-${mc}-fabric:${dep("jei")}")
    } else {
        modCompileOnly("mezz.jei:jei-1.21.10-fabric:26.2.0.27")
    }

    modImplementation("dev.isxander:controlify:${property("deps.controlify")}") {
        exclude(group = "maven.modrinth")
        exclude(group = "io.github.llamalad7")
    }

    implementation("folk.sisby:kaleido-config:${dep("kaleido")}")
    include("folk.sisby:kaleido-config:${dep("kaleido")}")

    if (hasProperty("deps.mousetweaks")) {
        modImplementation("maven.modrinth:mouse-tweaks:${dep("mousetweaks")}")
    }
    modImplementation("maven.modrinth:fractal-lib:${dep("fractal")}")
}

tasks {
    processResources {
        exclude("**/neoforge.mods.toml", "**/mods.toml")
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        from(remapJar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${prop("mod.version")}"))
        dependsOn("build")
    }
}

publishMods {
    file = tasks.remapJar.map { it.archiveFile.get() }
    additionalFiles.from(tasks.remapSourcesJar.map { it.archiveFile.get() })

    type = STABLE
    displayName = "${prop("mod.name")} ${prop("mod.version")} for $mc Fabric"
    version = "${prop("mod.version")}+${mc}-fabric"
    changelog = provider { rootProject.file("CHANGELOG.md").readText() }
    modLoaders.add("fabric")

    modrinth {
        projectId = prop("publish.modrinth")
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.add(mc)
        minecraftVersions.addAll(additionalVersions)
        requires("fabric-api")
    }

    curseforge {
        projectId = prop("publish.curseforge")
        accessToken = providers.environmentVariable("CURSEFORGE_API_KEY")
        minecraftVersions.add(mc)
        minecraftVersions.addAll(additionalVersions)

        clientRequired = true
        serverRequired = true

        requires("fabric-api")
    }
}
