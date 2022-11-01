import com.modrinth.minotaur.dependencies.DependencyType
import com.modrinth.minotaur.dependencies.ModDependency
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import xyz.enhancedpixel.gradle.tools.CurseRelation
import xyz.enhancedpixel.gradle.tools.CurseRelationType

plugins {
    java
    kotlin("jvm")
    kotlin("plugin.serialization")
    `maven-publish`
    id("xyz.enhancedpixel.gradle.multiversion")
    id("xyz.enhancedpixel.gradle.tools")
    id("xyz.enhancedpixel.gradle.tools.loom")
    id("xyz.enhancedpixel.gradle.tools.blossom")
    id("xyz.enhancedpixel.gradle.tools.releases")
}

repositories {
    maven("https://maven.terraformersmc.com/")
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

    modImplementation("net.fabricmc.fabric-api:fabric-api:${when (mcData.version) {
        11902 -> "0.64.0+1.19.2"
        11802 -> "0.59.1+1.18.2"
        else -> throw IllegalStateException("Invalid MC version: ${mcData.version}")
    }}")
    modImplementation("com.terraformersmc:modmenu:${when (mcData.version) {
        11902 -> "4.0.6"
        11802 -> "3.2.3"
        else -> throw IllegalStateException("Invalid MC version: ${mcData.version}")
    }}")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.8.2+kotlin.1.7.10")

    api(include("com.squareup.okhttp3:okhttp:${libs.versions.okhttp.get()}")!!)
    api(include("com.squareup.okio:okio:${libs.versions.okio.get()}")!!)
    modApi(include(libs.versions.universalcraft.map {
        "gg.essential:universalcraft-${when (mcData.version) {
            11902 -> "1.19.1-fabric"
            11802 -> "1.18.1-fabric"
            else -> "${mcData.versionStr}-${mcData.loader.name}"
        }}:$it"
    }.get()).excludeVitals())
    modApi(include(libs.versions.elementa.map {
        "gg.essential:elementa-${when (mcData.version) {
            11902 -> "1.18.1-fabric"
            11802 -> "1.18.1-fabric"
            else -> "${mcData.versionStr}-${mcData.loader.name}"
        }}:$it"
    }.get()).excludeVitals())

    implementation(include("xyz.enhancedpixel:enhancedeventbus:${libs.versions.enhancedeventbus.get()}")!!)
    api(include("xyz.deftu.deftils:Deftils:${libs.versions.deftils.get()}")!!)
    implementation(include("com.github.ben-manes.caffeine:caffeine:${libs.versions.caffeine.get()}")!!)
}

releases {
    modrinth {
        projectId.set("WfhjX9sQ")
        dependencies.set(listOf(
            ModDependency("P7dR8mSH", DependencyType.REQUIRED),
            ModDependency("Ha28R6CL", DependencyType.REQUIRED),
            ModDependency("mOgUt4GM", DependencyType.REQUIRED)
        ))
    }

    curseforge {
        projectId.set("695205")
        relations.set(listOf(
            CurseRelation("fabric-api", CurseRelationType.REQUIRED),
            CurseRelation("fabric-language-kotlin", CurseRelationType.REQUIRED),
            CurseRelation("modmenu", CurseRelationType.REQUIRED)
        ))
    }
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjvm-default=enable"
        }
    }

    remapJar {
        archiveBaseName.set("${modData.name}-${mcData.versionStr}")
    }
}

publishing {
    repositories {
        if (project.hasProperty("deftu.publishing.username") && project.hasProperty("deftu.publishing.password")) {
            fun MavenArtifactRepository.applyCredentials() {
                authentication.create<BasicAuthentication>("basic")
                credentials {
                    username = property("deftu.publishing.username")?.toString()
                    password = property("deftu.publishing.password")?.toString()
                }
            }

            maven {
                name = "DeftuReleases"
                url = uri("https://maven.deftu.xyz/releases")
                applyCredentials()
            }

            maven {
                name = "DeftuSnapshots"
                url = uri("https://maven.deftu.xyz/snapshots")
                applyCredentials()
            }
        }
    }
}
