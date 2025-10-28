plugins {
    id("net.neoforged.moddev.legacyforge")
    id ("dev.kikugie.postprocess.jsonlang")
    id("me.modmuss50.mod-publish-plugin")
}

tasks.named<ProcessResources>("processResources") {
    fun prop(name: String) = project.property(name) as String

    val props = HashMap<String, String>().apply {
        this["mod_version"] = prop("mod.version")
        this["minecraft"] = prop("deps.minecraft")
        this["loader_version_range"] = prop("deps.loader_version_range")
        this["mod_license"] = prop("mod.license")
        this["mod_description"] = prop("mod.description")
        this["mod_id"] = prop("mod.id")
        this["mod_name"] = prop("mod.name")
        this["mod_authors"] = prop("mod.authors")
        this["neo_version_range"] = prop("deps.neo_version_range")
        this["forge_version_range"] = prop("deps.forge_version_range")
        this["minecraft_version_range"] = prop("deps.minecraft_version_range")

    }

    filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml", "META-INF/mods.toml")) {
        expand(props)
    }
}

version = "${property("mod.version")}+${property("deps.minecraft")}-forge"
base.archivesName = property("mod.id") as String

jsonlang {
    languageDirectories = listOf("assets/${property("mod.id")}/lang")
    prettyPrint = true
}

legacyForge {
    version = property("deps.forge") as String
    validateAccessTransformers = true

    if (hasProperty("deps.parchment")) parchment {
        val (mc, ver) = (property("deps.parchment") as String).split(':')
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
        register(property("mod.id") as String) {
            sourceSet(sourceSets["main"])
        }
    }
    sourceSets["main"].resources.srcDir("src/main/generated")
}


repositories {
    mavenCentral()
    maven ( url = "https://maven.blamejared.com/" )
    maven ( url = "https://repo1.maven.org/maven2" )
    maven ( url = "https://api.modrinth.com/maven" )
    maven ( url = "https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1" )
    maven ( url = "https://maven.terraformersmc.com/" )
    maven ( url = "https://cursemaven.com" )
    maven ( url = "https://api.modrinth.com/maven")
    maven ( "https://repo.sleeping.town/" ) {
        name = "Sisby Maven"
    }
}

dependencies {
    modImplementation("mezz.jei:jei-${property("deps.minecraft")}-forge-api:${property("deps.jei")}")
    modImplementation("mezz.jei:jei-${property("deps.minecraft")}-forge:${property("deps.jei")}")

    implementation("folk.sisby:kaleido-config:${property("deps.kaleido")}")
    jarJar("folk.sisby:kaleido-config:${property("deps.kaleido")}")
    "additionalRuntimeClasspath"("folk.sisby:kaleido-config:${property("deps.kaleido")}")

    modCompileOnly("io.github.llamalad7:mixinextras-common:0.5.0")
    modImplementation("io.github.llamalad7:mixinextras-forge:0.5.0")
    jarJar("io.github.llamalad7:mixinextras-forge:0.5.0")
}

mixin {
    add(sourceSets["main"], "clutternomore.mixin-refmap.json")
    config("clutternomoreforge.mixins.json")
}

tasks {
    processResources {
        exclude("**/fabric.mod.json", "**/*.accesswidener")
    }

    named("createMinecraftArtifacts") {
        dependsOn("stonecutterGenerate")
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        from(jar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }
}

java {
    withSourcesJar()
    val javaCompat = if (stonecutter.eval(stonecutter.current.version, ">=1.20.5")) {
        JavaVersion.VERSION_21
    } else {
        JavaVersion.VERSION_17
    }
    sourceCompatibility = javaCompat
    targetCompatibility = javaCompat
}

val additionalVersionsStr = findProperty("publish.additionalVersions") as String?
val additionalVersions: List<String> = additionalVersionsStr
    ?.split(",")
    ?.map { it.trim() }
    ?.filter { it.isNotEmpty() }
    ?: emptyList()

publishMods {
    file = tasks.jar.map { it.archiveFile.get() }
    additionalFiles.from(tasks.named<org.gradle.jvm.tasks.Jar>("sourcesJar").map { it.archiveFile.get() })

    type = BETA
    displayName = "${property("mod.name")} ${property("mod.version")} for ${stonecutter.current.version} Forge"
    version = "${property("mod.version")}+${property("deps.minecraft")}-forge"
    changelog = provider { rootProject.file("CHANGELOG.md").readText() }
    modLoaders.add("forge")

    modrinth {
        projectId = property("publish.modrinth") as String
        accessToken = env.MODRINTH_API_KEY.orNull()
        minecraftVersions.add(stonecutter.current.version)
        minecraftVersions.addAll(additionalVersions)
    }

    curseforge {
        projectId = property("publish.curseforge") as String
        accessToken = env.CURSEFORGE_API_KEY.orNull()
        minecraftVersions.add(stonecutter.current.version)
        minecraftVersions.addAll(additionalVersions)
    }
}