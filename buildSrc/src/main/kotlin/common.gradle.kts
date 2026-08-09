plugins {
    java
    idea
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.11.4")
}

tasks.test {
    useJUnitPlatform()
}

version = "${prop("mod.version")}+${dep("minecraft")}-$loaderName"
base.archivesName = prop("mod.id")

java {
    withSourcesJar()
    toolchain.languageVersion = JavaLanguageVersion.of(
        when {
            stonecutterBuild.eval(mc, ">26") -> 25
            stonecutterBuild.eval(mc, ">=1.21") -> 21
            else -> 17
        }
    )
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

stonecutterBuild.replacements.string {
    direction = stonecutterBuild.eval(mc, ">1.21.10")
    replace("ResourceLocation", "Identifier")
}
stonecutterBuild.replacements.string {
    direction = stonecutterBuild.eval(mc, ">26")
    replace("GuiGraphics;", "GuiGraphicsExtractor;")
}
stonecutterBuild.replacements.string {
    direction = stonecutterBuild.eval(mc, ">26")
    replace("GuiGraphics ", "GuiGraphicsExtractor ")
}
stonecutterBuild.replacements.string {
    direction = stonecutterBuild.eval(mc, ">26")
    replace("GuiGraphics.", "GuiGraphicsExtractor.")
}
stonecutterBuild.replacements.string {
    direction = stonecutterBuild.eval(mc, ">26")
    replace("GuiGraphics)", "GuiGraphicsExtractor)")
}
stonecutterBuild.replacements.string {
    direction = stonecutterBuild.eval(mc, ">1.21.10")
    replace("getKey().location()", "getKey().identifier()")
}
stonecutterBuild.replacements.string {
    direction = stonecutterBuild.eval(mc, ">26")
    replace("FabricDataOutput", "FabricPackOutput")
}
stonecutterBuild.replacements.string {
    direction = stonecutterBuild.eval(mc, ">1.21.10")
    replace("getRecipeIdentifier", "getRecipeIdentifier")
}
stonecutterBuild.replacements.string {
    direction = stonecutterBuild.eval(mc, ">26.1.2")
    replace("net.minecraft.advancements.Criterion", "net.minecraft.advancements.triggers.Criterion")
}
stonecutterBuild.replacements.string {
    direction = stonecutterBuild.eval(mc, ">26.1.2")
    replace("minecraft.screen", "minecraft.gui.screen()")
}
stonecutterBuild.replacements.string {
    direction = stonecutterBuild.eval(mc, ">26.1.2")
    replace("Minecraft.getInstance().screen", "Minecraft.getInstance().gui.screen()")
}
stonecutterBuild.replacements.string {
    direction = stonecutterBuild.eval(mc, ">26.1.2")
    replace("Minecraft.getInstance().setScreen", "Minecraft.getInstance().gui.setScreen")
}
stonecutterBuild.replacements.string {
    direction = stonecutterBuild.eval(mc, ">1.21.2")
    replace("DirectionProperty", "EnumProperty<Direction>")
}
