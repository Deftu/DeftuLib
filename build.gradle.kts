import com.modrinth.minotaur.dependencies.DependencyType
import com.modrinth.minotaur.dependencies.ModDependency
import dev.deftu.gradle.tools.minecraft.CurseRelation
import dev.deftu.gradle.tools.minecraft.CurseRelationType
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    java
    kotlin("jvm")
    kotlin("plugin.serialization")
    `maven-publish`
    id("dev.deftu.gradle.multiversion")
    id("dev.deftu.gradle.tools")
    id("dev.deftu.gradle.tools.resources")
    id("dev.deftu.gradle.tools.bloom")
    id("dev.deftu.gradle.tools.maven-publishing")
    id("dev.deftu.gradle.tools.minecraft.loom")
    id("dev.deftu.gradle.tools.minecraft.releases")
}

toolkit.useDevAuth()

repositories {
    maven("https://maven.terraformersmc.com/")
    maven("https://maven.isxander.dev/releases")
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
    api(include("dev.deftu:enhancedeventbus:${libs.versions.enhancedeventbus.get()}")!!)
    api(include("com.github.ben-manes.caffeine:caffeine:${libs.versions.caffeine.get()}")!!)

    modApi(include(libs.versions.universalcraft.map {
        "gg.essential:universalcraft-${when (mcData.version) {
            1_18_02 -> "1.18.1-fabric"
            else -> "${mcData.versionStr}-${mcData.loader.name}"
        }}:$it"
    }.get()).excludeVitals())
    modApi(include(libs.versions.elementa.map {
        "gg.essential:elementa-${when (mcData.version) {
            1_21_00, 1_20_06, 1_20_04,
            1_20_02, 1_20_01, 1_19_04,
            1_19_02, 1_18_02 -> "1.18.1-fabric"
            else -> "${mcData.versionStr}-${mcData.loader.name}"
        }}:$it"
    }.get()).excludeVitals())
    modApi(include(libs.versions.vigilance.map {
        "gg.essential:vigilance-${when (mcData.version) {
            1_21_00, 1_20_06, 1_20_04,
            1_20_02, 1_20_01, 1_19_04,
            1_19_02, 1_18_02 -> "1.18.1-fabric"
            else -> "${mcData.versionStr}-${mcData.loader.name}"
        }}:$it"
    }.get()).excludeVitals())
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

    curseforge {
        projectId.set("695205")
        relations.set(listOf(
            CurseRelation("fabric-api", CurseRelationType.REQUIRED),                // Fabric API
            CurseRelation("fabric-language-kotlin", CurseRelationType.REQUIRED),    // Fabric Language Kotlin
            CurseRelation("modmenu", CurseRelationType.OPTIONAL)                    // Mod Menu
        ))
    }
}
