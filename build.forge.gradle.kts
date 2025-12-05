plugins {
    id("net.neoforged.moddev.legacyforge")
    id ("dev.kikugie.postprocess.jsonlang")
    id("me.modmuss50.mod-publish-plugin")
}

tasks.named<ProcessResources>("processResources") {
    fun prop(name: String) = project.property(name) as String

    val props = HashMap<String, String>().apply {
        this["mod_version"] = prop("mod.version") + "+" + prop("deps.minecraft")
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
    maven {
        name = "DevAuth"
        url = uri("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
        content {
            includeGroup("me.djtheredstoner")
        }
    }
    maven {
        name = "Curse Maven"
        url = uri("https://cursemaven.com")
        content {
            includeGroupAndSubgroups("curse.maven")
        }
    }
    maven {
        name = "Kotlin for Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
        content {
            includeGroupAndSubgroups("thedarkcolour")
        }
    }
    maven {
        name = "Greenhouse Maven"
        url = uri("https://maven.greenhouse.lgbt/releases/")
        content {
            includeGroup("house.greenhouse")
            includeGroup("umpaz.brewinandchewin")
        }
    }
    maven {
        name = "Terraformers (Mod Menu)"
        url = uri("https://maven.terraformersmc.com/releases/")
        content {
            includeGroupAndSubgroups("com.terraformersmc")
            includeGroupAndSubgroups("dev.emi")
        }
    }
    maven {
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
        content {
            includeGroupAndSubgroups("maven.modrinth")
        }
    }
    maven {
        name = "Sisby Maven"
        url = uri("https://repo.sleeping.town/")
        content {
            includeGroupAndSubgroups("folk.sisby")
        }
    }
    maven {
        name = "Parchment Mappings"
        url = uri("https://maven.parchmentmc.org")
        content {
            includeGroupAndSubgroups("org.parchmentmc")
        }
    }
    maven {
        name = "Xander Maven"
        url = uri("https://maven.isxander.dev/releases")
        content {
            includeGroupAndSubgroups("dev.isxander")
            includeGroupAndSubgroups("org.quiltmc.parsers")
        }
    }
    maven {
        name = "Sinytra Maven"
        url = uri("https://maven.su5ed.dev/releases")
        content {
            includeGroupAndSubgroups("org.sinytra")
            includeGroupAndSubgroups("dev.su5ed")
        }
    }
    maven {
        name = "JEI"
        url = uri("https://maven.blamejared.com/")
        content {
            includeGroup("mezz.jei")
        }
    }
}

dependencies {

    implementation("folk.sisby:kaleido-config:${property("deps.kaleido")}")
    jarJar("folk.sisby:kaleido-config:${property("deps.kaleido")}")
    "additionalRuntimeClasspath"("folk.sisby:kaleido-config:${property("deps.kaleido")}")

    modCompileOnly("dev.emi:emi-forge:${property("deps.emi")}:api")
//    modRuntimeOnly("dev.emi:emi-forge:${property("deps.emi")}")

    modRuntimeOnly("maven.modrinth:moonlight:${property("deps.moonlight")}")
    modRuntimeOnly("maven.modrinth:supplementaries:LAQ22yJj")
    modRuntimeOnly("maven.modrinth:would:2FZ421Oh")

    // compile against the JEI API but do not include it at runtime
    compileOnly("mezz.jei:jei-${property("deps.minecraft")}-forge-api:${property("deps.jei")}")
    // at runtime, use the full JEI jar for Forge
    modRuntimeOnly("mezz.jei:jei-${property("deps.minecraft")}-forge:${property("deps.jei")}")

    modImplementation("dev.su5ed.sinytra:fabric-loader:2.7.11+0.16.5+1.20.1")
    modImplementation("dev.su5ed.sinytra.fabric-api:fabric-api-base:0.4.32+ef105b4977")
    modImplementation("dev.su5ed.sinytra.fabric-api:fabric-item-group-api-v1:4.0.14+c9161c2d77")

    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    annotationProcessor("io.github.llamalad7:mixinextras-common:0.5.0")
    compileOnly("io.github.llamalad7:mixinextras-common:0.5.0")
    implementation("io.github.llamalad7:mixinextras-forge:0.5.0")
    jarJar("io.github.llamalad7:mixinextras-forge:0.5.0")
}

mixin {
    add(sourceSets["main"], "clutternomore.refmap.json")
    config("clutternomore-forge.mixins.json")
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "MixinConfigs" to "clutternomore-forge.mixins.json"
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