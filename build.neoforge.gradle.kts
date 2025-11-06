plugins {
    id("net.neoforged.moddev")
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
        this["minecraft_version_range"] = prop("deps.minecraft_version_range")

    }

    filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml", "META-INF/mods.toml")) {
        expand(props)
    }
}

version = "${property("mod.version")}+${property("deps.minecraft")}-neoforge"
base.archivesName = property("mod.id") as String

jsonlang {
    languageDirectories = listOf("assets/${property("mod.id")}/lang")
    prettyPrint = true
}

neoForge {
    version = property("deps.neoforge") as String
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
    compileOnly("mezz.jei:jei-${property("deps.minecraft")}-common-api:19.21.0.247")
    compileOnly("mezz.jei:jei-${property("deps.minecraft")}-neoforge-api:19.21.0.247")
    runtimeOnly("mezz.jei:jei-${property("deps.minecraft")}-neoforge:19.21.0.247")

    implementation("folk.sisby:kaleido-config:${property("deps.kaleido")}")
    jarJar("folk.sisby:kaleido-config:${property("deps.kaleido")}")
    "additionalRuntimeClasspath"("folk.sisby:kaleido-config:${property("deps.kaleido")}")

    runtimeOnly("me.djtheredstoner:DevAuth-neoforge:1.2.1")

    runtimeOnly("maven.modrinth:supplementaries:neoforge_1.21-3.4.14")
    runtimeOnly("maven.modrinth:the-block-box:0.1.1")
    runtimeOnly("maven.modrinth:no-mans-land:1.3.3")
    runtimeOnly("maven.modrinth:biolith:hd0IDIF5")
    runtimeOnly("maven.modrinth:mixed-litter:0.1.2")

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
    displayName = "${property("mod.name")} ${property("mod.version")} for ${stonecutter.current.version} Neoforge"
    version = "${property("mod.version")}+${property("deps.minecraft")}-neoforge"
    changelog = provider { rootProject.file("CHANGELOG.md").readText() }
    modLoaders.add("neoforge")

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