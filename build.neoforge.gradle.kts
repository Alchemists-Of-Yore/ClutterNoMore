plugins {
    id("common")
    id("net.neoforged.moddev")
    id("dev.kikugie.postprocess.jsonlang")
    id("me.modmuss50.mod-publish-plugin")
}

tasks.named<ProcessResources>("processResources") {
    val props = mapOf(
        "mod_version" to "${prop("mod.version")}+${mc}",
        "minecraft" to mc,
        "loader_version_range" to dep("loader_version_range"),
        "mod_license" to prop("mod.license"),
        "mod_description" to prop("mod.description"),
        "mod_id" to prop("mod.id"),
        "mod_name" to prop("mod.name"),
        "mod_authors" to prop("mod.authors"),
        "neo_version_range" to dep("neo_version_range"),
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
    maven("https://maven.su5ed.dev/releases") {
        name = "Sinytra Maven"
        content {
            includeGroupAndSubgroups("org.sinytra")
            includeGroupAndSubgroups("dev.su5ed")
        }
    }
    maven("https://maven.blamejared.com/") {
        name = "JEI"
    }
    exclusiveContent {
        forRepository {
            maven {
                name = "Registrate"
                url = uri("https://mvn.devos.one/snapshots")
            }
        }
        filter {
            includeGroup("com.tterrag.registrate")
        }
    }
    maven("https://maven.createmod.net")
}

neoForge {
    version = dep("neoforge")
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
    if (hasProperty("deps.emi")) {
        compileOnly("dev.emi:emi-neoforge:${dep("emi")}:api")
        runtimeOnly("dev.emi:emi-neoforge:${dep("emi")}")
    }

    // compile against the JEI API but do not include it at runtime
    compileOnly("mezz.jei:jei-${mc}-neoforge-api:${dep("jei")}")
    // at runtime, use the full JEI jar for NeoForge
    runtimeOnly("mezz.jei:jei-${mc}-neoforge:${dep("jei")}")

    implementation("folk.sisby:kaleido-config:${dep("kaleido")}")
    jarJar("folk.sisby:kaleido-config:${dep("kaleido")}")
    "additionalRuntimeClasspath"("folk.sisby:kaleido-config:${dep("kaleido")}")

    compileOnly("org.sinytra.forgified-fabric-api:fabric-item-group-api-v1:4.1.7+e324903319")

    runtimeOnly("me.djtheredstoner:DevAuth-neoforge:1.2.1")

    runtimeOnly("maven.modrinth:moonlight:${dep("moonlight")}")
    runtimeOnly("maven.modrinth:supplementaries:neoforge_${mcMajor}-3.4.14")
    runtimeOnly("maven.modrinth:the-block-box:0.1.1")
    runtimeOnly("maven.modrinth:no-mans-land:1.3.3")
    runtimeOnly("maven.modrinth:biolith:hd0IDIF5")
    runtimeOnly("maven.modrinth:mixed-litter:0.1.2")
    if (hasProperty("deps.would")) {
        runtimeOnly("maven.modrinth:would:${dep("would")}")
    }
    // YACL - required by McQoy
    if (hasProperty("deps.yacl")) {
        runtimeOnly("dev.isxander:yet-another-config-lib:${dep("yacl")}-neoforge")
    }
    if (hasProperty("deps.brewin_and_chewin")) {
        implementation("umpaz.brewinandchewin:BrewinAndChewin-neoforge:${dep("brewin_and_chewin")}+${mc}") { isTransitive = false }
        implementation("house.greenhouse:greenhouseconfig:${dep("greenhouse_config")}+${mc}-neoforge")
        implementation("house.greenhouse:greenhouseconfig_toml:${dep("greenhouse_config_toml")}")
    }
    if (hasProperty("deps.farmers_delight")) {
        implementation("maven.modrinth:farmers-delight:${dep("farmers_delight")}")
    }

    if (hasProperty("deps.create")) {
        implementation("com.simibubi.create:create-${mc}:${dep("create")}:slim") { isTransitive = false }
        implementation("dev.engine-room.flywheel:flywheel-neoforge-${mc}:${dep("flywheel")}")
        implementation("net.createmod.ponder:ponder-neoforge:${dep("ponder")}+mc${mc}")
        implementation("com.tterrag.registrate:Registrate:MC${mcMajor}-${dep("registrate")}")
    }
    if (hasProperty("deps.mousetweaks")) {
        implementation("maven.modrinth:mouse-tweaks:${dep("mousetweaks")}")
    }
}

tasks {
    processResources {
        exclude("**/fabric.mod.json", "**/*.accesswidener", "**/mods.toml")
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
    file = tasks.jar.map { it.archiveFile.get() }
    additionalFiles.from(tasks.named<org.gradle.jvm.tasks.Jar>("sourcesJar").map { it.archiveFile.get() })

    type = STABLE
    displayName = "${prop("mod.name")} ${prop("mod.version")} for $mc Neoforge"
    version = "${prop("mod.version")}+${mc}-neoforge"
    changelog = provider { rootProject.file("CHANGELOG.md").readText() }
    modLoaders.add("neoforge")

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
