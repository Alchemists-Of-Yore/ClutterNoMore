plugins {
    java
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

