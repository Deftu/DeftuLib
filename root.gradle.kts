plugins {
    id("dev.deftu.gradle.multiversion-root")
}

preprocess {
    val fabric_1_20_06 = createNode("1.20.6-fabric", 1_20_06, "yarn")
    val fabric_1_20_04 = createNode("1.20.4-fabric", 1_20_04, "yarn")
    val fabric_1_20_02 = createNode("1.20.2-fabric", 1_20_02, "yarn")
    val fabric_1_20_01 = createNode("1.20.1-fabric", 1_20_01, "yarn")
    val fabric_1_19_04 = createNode("1.19.4-fabric", 1_19_04, "yarn")
    val fabric_1_19_02 = createNode("1.19.2-fabric", 1_19_02, "yarn")
    val fabric_1_18_02 = createNode("1.18.2-fabric", 1_18_02, "yarn")

    fabric_1_20_06.link(fabric_1_20_04)
    fabric_1_20_04.link(fabric_1_20_02)
    fabric_1_20_02.link(fabric_1_20_01)
    fabric_1_20_01.link(fabric_1_19_04)
    fabric_1_19_04.link(fabric_1_19_02)
    fabric_1_19_02.link(fabric_1_18_02)
}

val versions = listOf(
    "1.18.2-fabric",
    "1.19.2-fabric",
    "1.19.4-fabric",
    "1.20.1-fabric",
    "1.20.2-fabric",
    "1.20.4-fabric",
    "1.20.6-fabric"
)

project.tasks.register("buildVersions") {
    group = "deftu"

    dependsOn(versions.map { ":$it:build" })
}

listOf(
    "DeftuReleasesRepository",
    "DeftuSnapshotsRepository"
).forEach { repository ->
    versions.forEach { version ->
        project(":$version").tasks.register("fullReleaseWith$repository") {
            group = "deftu"

            dependsOn(":$version:publishAllPublicationsTo$repository")
            dependsOn(":$version:publishMod")
        }
    }

    project.tasks.register("publishAllPublicationsTo$repository") {
        group = "deftu"

        dependsOn(versions.map { ":$it:publishAllPublicationsTo$repository" })
    }

    project.tasks.register("fullReleaseWith$repository") {
        group = "deftu"

        dependsOn(versions.map { ":$it:fullReleaseWith$repository" })
    }
}
