plugins {
    id("common")
    id("net.neoforged.moddev.legacyforge")
    id("dev.kikugie.postprocess.jsonlang")
    id("me.modmuss50.mod-publish-plugin")
}

tasks.named<ProcessResources>("processResources") {
    val props = mapOf(
        "mod_version" to "${prop("mod.version")}+${dep("minecraft")}",
        "minecraft" to dep("minecraft"),
        "loader_version_range" to dep("loader_version_range"),
        "mod_license" to prop("mod.license"),
        "mod_description" to prop("mod.description"),
        "mod_id" to prop("mod.id"),
        "mod_name" to prop("mod.name"),
        "mod_authors" to prop("mod.authors"),
        "neo_version_range" to dep("neo_version_range"),
        "forge_version_range" to dep("forge_version_range"),
        "minecraft_version_range" to dep("minecraft_version_range"),
    )

    filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml", "META-INF/mods.toml")) {
        expand(props)
    }
    inputs.properties(props)
}

jsonlang {
    languageDirectories = listOf("assets/${prop("mod.id")}/lang")
    prettyPrint = true
}

repositories {
    mavenCentral()
    maven("https://maven.blamejared.com/") {
        name = "JEI"
        content { includeGroup("mezz.jei") }
    }
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1") {
        name = "DevAuth"
        content { includeGroup("me.djtheredstoner") }
    }
    maven("https://cursemaven.com") {
        name = "Curse Maven"
        content { includeGroup("curse.maven") }
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
        content { includeGroup("dev.emi") }
    }
    maven("https://api.modrinth.com/maven") {
        name = "Modrinth"
        content { includeGroup("maven.modrinth") }
    }
    maven("https://repo.sleeping.town/") {
        name = "Sisby Maven"
        content { includeGroup("folk.sisby") }
    }
    maven("https://maven.parchmentmc.org") {
        name = "Parchment Mappings"
        content { includeGroup("org.parchmentmc.data") }
    }
    maven("https://maven.isxander.dev/releases") {
        name = "Xander Maven"
        content {
            includeGroupAndSubgroups("dev.isxander")
            includeGroupAndSubgroups("org.quiltmc.parsers")
        }
    }
    maven("https://maven.su5ed.dev/releases") {
        name = "Sinytra Maven"
        content { includeGroupAndSubgroups("dev.su5ed.sinytra") }
    }
}

legacyForge {
    version = dep("forge")
    validateAccessTransformers = true

    if (hasProperty("deps.parchment")) parchment {
        val (mc, ver) = dep("parchment").split(':')
        mappingsVersion = ver
        minecraftVersion = mc
    }

    runs {
        register("client") {
            gameDirectory = file("run/")
            client()
        }
        register("server") {
            gameDirectory = file("run/")
            server()
        }
    }

    mods {
        register(prop("mod.id")) {
            sourceSet(sourceSets["main"])
        }
    }
    sourceSets["main"].resources.srcDir("src/main/generated")
}

dependencies {
    implementation("folk.sisby:kaleido-config:${dep("kaleido")}")
    jarJar("folk.sisby:kaleido-config:${dep("kaleido")}")
    "additionalRuntimeClasspath"("folk.sisby:kaleido-config:${dep("kaleido")}")

    modCompileOnly("dev.emi:emi-forge:${dep("emi")}:api")
//    modRuntimeOnly("dev.emi:emi-forge:${dep("emi")}")

    modRuntimeOnly("maven.modrinth:moonlight:${dep("moonlight")}")
    modRuntimeOnly("maven.modrinth:supplementaries:LAQ22yJj")
    modRuntimeOnly("maven.modrinth:would:2FZ421Oh")

    // compile against the JEI API but do not include it at runtime
    compileOnly("mezz.jei:jei-${dep("minecraft")}-forge-api:${dep("jei")}")
    // at runtime, use the full JEI jar for Forge
    modRuntimeOnly("mezz.jei:jei-${dep("minecraft")}-forge:${dep("jei")}")

    modImplementation("dev.su5ed.sinytra:fabric-loader:2.7.11+0.16.5+1.20.1")
    modImplementation("dev.su5ed.sinytra.fabric-api:fabric-api-base:0.4.32+ef105b4977")
    modImplementation("dev.su5ed.sinytra.fabric-api:fabric-item-group-api-v1:4.0.14+c9161c2d77")

    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    annotationProcessor("io.github.llamalad7:mixinextras-common:0.5.4")
    compileOnly("io.github.llamalad7:mixinextras-common:0.5.4")
    implementation("io.github.llamalad7:mixinextras-forge:0.5.4")
    jarJar("io.github.llamalad7:mixinextras-forge:0.5.4")
}

mixin {
    add(sourceSets["main"], "clutternomore.refmap.json")
    config("clutternomore.mixins.json")
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "MixinConfigs" to "clutternomore.mixins.json"
        )
    }
}

tasks {
    processResources {
        exclude("**/fabric.mod.json", "**/*.accesswidener", "**/neoforge.mods.toml")
    }

    named("createMinecraftArtifacts") {
        dependsOn("stonecutterGenerate")
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        from(jar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${prop("mod.version")}"))
        dependsOn("build")
    }
}

publishMods {
    file = (tasks.named<org.gradle.jvm.tasks.Jar>("reobfJar").map { it.archiveFile.get() })
    additionalFiles.from(tasks.named<org.gradle.jvm.tasks.Jar>("sourcesJar").map { it.archiveFile.get() })

    type = BETA
    displayName = "${prop("mod.name")} ${prop("mod.version")} for $mc Forge"
    version = "${prop("mod.version")}+${dep("minecraft")}-forge"
    changelog = provider { rootProject.file("CHANGELOG.md").readText() }
    modLoaders.add("forge")

    modrinth {
        projectId = prop("publish.modrinth")
        accessToken = env.MODRINTH_API_KEY.orNull()
        minecraftVersions.add(mc)
        minecraftVersions.addAll(additionalVersions)
    }

    curseforge {
        projectId = prop("publish.curseforge")
        accessToken = env.CURSEFORGE_API_KEY.orNull()
        minecraftVersions.add(mc)
        minecraftVersions.addAll(additionalVersions)
    }
}
