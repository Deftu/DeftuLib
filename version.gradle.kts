import com.modrinth.minotaur.dependencies.DependencyType
import com.modrinth.minotaur.dependencies.ModDependency
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import xyz.deftu.gradle.tools.minecraft.CurseRelation
import xyz.deftu.gradle.tools.minecraft.CurseRelationType

plugins {
    java
    kotlin("jvm")
    kotlin("plugin.serialization")
    `maven-publish`
    id("xyz.deftu.gradle.multiversion")
    id("xyz.deftu.gradle.tools")
    id("xyz.deftu.gradle.tools.blossom")
    id("xyz.deftu.gradle.tools.maven-publishing")
    id("xyz.deftu.gradle.tools.minecraft.loom")
    id("xyz.deftu.gradle.tools.minecraft.releases")
}

repositories {
    maven("https://maven.terraformersmc.com/")
    maven("https://maven.isxander.dev/releases")
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
    mavenCentral()
}

fun Dependency?.excludeVitals(): Dependency = apply {
    check(this is ExternalModuleDependency)
    exclude(module = "kotlin-stdlib")
    exclude(module = "kotlin-stdlib-common")
    exclude(module = "kotlin-stdlib-jdk8")
    exclude(module = "kotlin-stdlib-jdk7")
    exclude(module = "kotlin-reflect")
    exclude(module = "annotations")
    exclude(module = "fabric-loader")
}!!

dependencies {
    implementation(kotlin("stdlib"))

    modImplementation("net.fabricmc.fabric-api:fabric-api:${mcData.fabricApiVersion}")
    modImplementation(mcData.modMenuDependency)
    modImplementation("net.fabricmc:fabric-language-kotlin:1.7.4+kotlin.1.6.21")

    api(include("com.squareup.okio:okio:${libs.versions.okio.get()}")!!)
    api(include("com.squareup.okio:okio-jvm:${libs.versions.okio.get()}")!!)
    api(include("com.squareup.okhttp3:okhttp:${libs.versions.okhttp.get()}")!!)

    include(modApi(libs.versions.universalcraft.map {
        "gg.essential:universalcraft-${when (mcData.version) {
            11902 -> "1.19.1-fabric"
            else -> "${mcData.versionStr}-${mcData.loader.name}"
        }}:$it"
    }.get()).excludeVitals())
    include(modApi(libs.versions.elementa.map {
        "gg.essential:elementa-${when (mcData.version) {
            12001 -> "1.18.1-fabric"
            12000 -> "1.18.1-fabric"
            11904 -> "1.18.1-fabric"
            11903 -> "1.18.1-fabric"
            11902 -> "1.18.1-fabric"
            else -> "${mcData.versionStr}-${mcData.loader.name}"
        }}:$it"
    }.get()).excludeVitals())
    include(modApi(libs.versions.vigilance.map {
        "gg.essential:vigilance-${when (mcData.version) {
            12001 -> "1.18.1-fabric"
            12000 -> "1.18.1-fabric"
            11904 -> "1.18.1-fabric"
            11903 -> "1.18.1-fabric"
            11902 -> "1.18.1-fabric"
            else -> "${mcData.versionStr}-${mcData.loader.name}"
        }}:$it"
    }.get()).excludeVitals())

    implementation(include("xyz.deftu:enhancedeventbus:${libs.versions.enhancedeventbus.get()}")!!)
    api(include("xyz.deftu.deftils:Deftils:${libs.versions.deftils.get()}")!!)
    implementation(include("com.github.ben-manes.caffeine:caffeine:${libs.versions.caffeine.get()}")!!)
}

toolkitReleases {
    val log = rootProject.file("changelogs/${modData.version}.md")
    if (log.exists()) changelogFile.set(log)

    modrinth {
        projectId.set("WfhjX9sQ")
        dependencies.set(listOf(
            ModDependency("P7dR8mSH", DependencyType.REQUIRED),                     // Fabric API
            ModDependency("Ha28R6CL", DependencyType.REQUIRED),                     // Fabric Language Kotlin
            ModDependency("mOgUt4GM", DependencyType.OPTIONAL)                      // Mod Menu
        ))
    }

//    curseforge {
//        projectId.set("695205")
//        relations.set(listOf(
//            CurseRelation("fabric-api", CurseRelationType.REQUIRED),                // Fabric API
//            CurseRelation("fabric-language-kotlin", CurseRelationType.REQUIRED),    // Fabric Language Kotlin
//            CurseRelation("modmenu", CurseRelationType.OPTIONAL)                    // Mod Menu
//        ))
//    }
}
