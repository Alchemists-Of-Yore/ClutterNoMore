plugins {
    java
}

version = "${prop("mod.version")}+${dep("minecraft")}-$loaderName"
base.archivesName = prop("mod.id")

java {
    withSourcesJar()
    val compat = when {
        stonecutterBuild.eval(mc, ">26") -> JavaVersion.VERSION_25
        stonecutterBuild.eval(mc, ">=1.21") -> JavaVersion.VERSION_21
        else -> JavaVersion.VERSION_17
    }
    sourceCompatibility = compat
    targetCompatibility = compat
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
