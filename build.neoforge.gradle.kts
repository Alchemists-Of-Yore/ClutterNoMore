plugins {
    id("net.neoforged.moddev")
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
    maven {
        name = "Curse Maven"
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
    compileOnly("mezz.jei:jei-${property("deps.minecraft")}-common-api:19.21.0.247")
    compileOnly("mezz.jei:jei-${property("deps.minecraft")}-neoforge-api:19.21.0.247")
    runtimeOnly("mezz.jei:jei-${property("deps.minecraft")}-neoforge:19.21.0.247")

    if (hasProperty("deps.emi")) {
        compileOnly("dev.emi:emi-neoforge:${property("deps.emi")}:api")
        runtimeOnly("dev.emi:emi-neoforge:${property("deps.emi")}")
    }

    // compile against the JEI API but do not include it at runtime
    compileOnly("mezz.jei:jei-${property("deps.minecraft")}-neoforge-api:${property("deps.jei")}")
    // at runtime, use the full JEI jar for NeoForge
    runtimeOnly("mezz.jei:jei-${property("deps.minecraft")}-neoforge:${property("deps.jei")}")


    implementation("folk.sisby:kaleido-config:${property("deps.kaleido")}")
    jarJar("folk.sisby:kaleido-config:${property("deps.kaleido")}")
    "additionalRuntimeClasspath"("folk.sisby:kaleido-config:${property("deps.kaleido")}")

    compileOnly("org.sinytra.forgified-fabric-api:fabric-item-group-api-v1:4.1.7+e324903319")

    runtimeOnly("me.djtheredstoner:DevAuth-neoforge:1.2.1")

    runtimeOnly("maven.modrinth:moonlight:${property("deps.moonlight")}")
    runtimeOnly("maven.modrinth:supplementaries:neoforge_1.21-3.4.14")
    runtimeOnly("maven.modrinth:the-block-box:0.1.1")
    runtimeOnly("maven.modrinth:no-mans-land:1.3.3")
    runtimeOnly("maven.modrinth:biolith:hd0IDIF5")
    runtimeOnly("maven.modrinth:mixed-litter:0.1.2")
    if (hasProperty("deps.would")) {
        runtimeOnly("maven.modrinth:would:${property("deps.would")}")
    }
    // YACL - required by McQoy
    if (hasProperty("deps.yacl")) {
        runtimeOnly("dev.isxander:yet-another-config-lib:${property("deps.yacl")}-neoforge")
    }
    if (hasProperty("deps.brewin_and_chewin")) {
        implementation("umpaz.brewinandchewin:BrewinAndChewin-neoforge:${property("deps.brewin_and_chewin")}+${property("deps.minecraft")}") { isTransitive = false }
        implementation("house.greenhouse:greenhouseconfig:${property("deps.greenhouse_config")}+${property("deps.minecraft")}-neoforge")
        implementation("house.greenhouse:greenhouseconfig_toml:${property("deps.greenhouse_config_toml")}")
    }
    if (hasProperty("deps.farmers_delight")) {
        implementation("maven.modrinth:farmers-delight:${property("deps.farmers_delight")}")
    }
//    runtimeOnly("maven.modrinth:mcqoy:${property("deps.mcqoy")}")


}

stonecutter {
    replacements.string {
        direction = eval(current.version, ">1.21.10")
        replace("ResourceLocation", "Identifier")
    }
    replacements.string {
        direction = eval(current.version, ">1.21.10")
        replace("getKey().location()", "getKey().identifier()")
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

    type = STABLE
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