plugins {
    id("xyz.deftu.gradle.multiversion-root")
}

preprocess {
    val fabric11904 = createNode("1.19.4-fabric", 11904, "yarn")
    val fabric11903 = createNode("1.19.3-fabric", 11903, "yarn")
    val fabric11902 = createNode("1.19.2-fabric", 11902, "yarn")

    fabric11904.link(fabric11903)
    fabric11903.link(fabric11902)
}

listOf(
    "DeftuReleasesRepository",
    "DeftuSnapshotsRepository"
).forEach { repository ->
    listOf(
        "1.19.4-fabric",
        "1.19.3-fabric",
        "1.19.2-fabric"
    ).forEach { version ->
        project(":$version").tasks.register("fullReleaseWith$repository") {
            group = "deftu"

            dependsOn(":$version:publishAllPublicationsTo$repository")
            dependsOn(":$version:releaseProject")
        }
    }
}
