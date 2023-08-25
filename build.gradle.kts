plugins {
    id("xyz.deftu.gradle.multiversion-root")
}

preprocess {
    val fabric_1_20_01 = createNode("1.20.1-fabric", 1_20_01, "yarn")
    val fabric_1_20_00 = createNode("1.20-fabric", 1_20_00, "yarn")
    val fabric_1_19_04 = createNode("1.19.4-fabric", 1_19_04, "yarn")
    val fabric_1_19_03 = createNode("1.19.3-fabric", 1_19_03, "yarn")
    val fabric_1_19_02 = createNode("1.19.2-fabric", 1_19_02, "yarn")

    fabric_1_20_01.link(fabric_1_20_00)
    fabric_1_20_00.link(fabric_1_19_04)
    fabric_1_19_04.link(fabric_1_19_03)
    fabric_1_19_03.link(fabric_1_19_02)
}

listOf(
    "DeftuReleasesRepository",
    "DeftuSnapshotsRepository"
).forEach { repository ->
    listOf(
        "1.20.1-fabric",
        "1.20-fabric",
        "1.19.4-fabric",
        "1.19.3-fabric",
        "1.19.2-fabric"
    ).forEach { version ->
        project(":$version").tasks.register("fullReleaseWith$repository") {
            group = "deftu"

            dependsOn(":$version:publishAllPublicationsTo$repository")
            dependsOn(":$version:publishMod")
        }
    }
}
