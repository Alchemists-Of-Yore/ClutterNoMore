import dev.kikugie.stonecutter.build.StonecutterBuildExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

val Project.stonecutterBuild: StonecutterBuildExtension
    get() = extensions.getByType()

val Project.mc: String
    get() = stonecutterBuild.current.version

val Project.loaderName: String
    get() = stonecutterBuild.current.project.substringAfterLast('-')

fun Project.prop(key: String): String =
    requireNotNull(findProperty(key)) { "Missing property: $key" }.toString()

fun Project.dep(key: String): String = prop("deps.$key")

val Project.additionalVersions: List<String>
    get() = (findProperty("publish.additionalVersions") as String?)
        ?.split(",")
        ?.map { it.trim() }
        ?.filter { it.isNotEmpty() }
        ?: emptyList()
