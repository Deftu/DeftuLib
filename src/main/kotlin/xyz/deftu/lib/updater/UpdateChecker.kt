package xyz.deftu.lib.updater

import kotlinx.coroutines.runBlocking
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import net.fabricmc.loader.api.entrypoint.EntrypointContainer
import net.fabricmc.loader.api.metadata.ModMetadata
import net.fabricmc.loader.impl.util.version.VersionParser
import net.minecraft.text.ClickEvent
import net.minecraft.util.Formatting
import org.apache.logging.log4j.LogManager
import xyz.deftu.lib.DeftuLib
import xyz.deftu.lib.utils.ChatHelper
import xyz.deftu.lib.utils.TextHelper
import java.util.concurrent.TimeUnit

class UpdateChecker {
    private var started = false
    private val logger = LogManager.getLogger("${DeftuLib.NAME} Update Checker")
    private val entrypoints: List<EntrypointContainer<UpdaterEntrypoint>>
        get() = FabricLoader.getInstance().getEntrypointContainers("update_checker", UpdaterEntrypoint::class.java)

    private val modrinth = ModrinthClient()
    private val updates = mutableListOf<Update>()

    fun start() {
        if (started) return
        if (DeftuLib.ENVIRONMENT == EnvType.CLIENT) {
            ClientPlayConnectionEvents.JOIN.register { _, _, _ ->
                for (update in updates) {
                    ChatHelper.sendClientMessage(
                        TextHelper.createTranslatableText(
                            "deftulib.update_checker.text",
                            update.version.versionNumber,
                            TextHelper.createLiteralText(update.mod.name)
                                .formatted(Formatting.UNDERLINE)
                                .formatted(Formatting.AQUA)
                                .styled {
                                    it.withClickEvent(
                                        ClickEvent(
                                            ClickEvent.Action.OPEN_URL,
                                            "https://modrinth.com/mod/${update.version.projectId}"
                                        )
                                    )
                                },
                            update.mod.version.friendlyString,
                            update.version.versionType.toText()
                        )
                    )
                }
            }
        } else {
            ServerLifecycleEvents.SERVER_STARTED.register {
                for (update in updates) {
                    logger.info("Update available for ${update.mod.name} in the ${update.version.versionType.name.lowercase()} channel! (${update.version.versionNumber})")
                }
            }
        }

        val mods = FabricLoader.getInstance().allMods.filter { container ->
            container.metadata.isDeftuMod() || container.metadata.shouldCheck()
        }

        // check for updates in another thread (async checking)
        DeftuLib.MULTITHREADER.schedule({
            logger.info("Checking for updates...")
            updates.clear()
            runBlocking {
                mods.forEach { container ->
                    if (!container.shouldCheckEntrypointForUpdates()) return@forEach

                    val file = container.origin.paths[0]?.toFile() ?: return@forEach
                    if (!file.exists()) return@forEach

                    val modrinthVersion = modrinth.fetch(file) ?: return@forEach
                    val version = VersionParser.parseSemantic(modrinthVersion.versionNumber) ?: return@forEach
                    val currentVersion = VersionParser.parseSemantic(container.metadata.version.friendlyString) ?: return@forEach

                    if (version > currentVersion) {
                        updates.add(Update(container.metadata, modrinthVersion))
                    }
                }
            }
        }, 0, 30, TimeUnit.MINUTES)
        started = true
    }

    private fun ModContainer.shouldCheckEntrypointForUpdates() = (entrypoints.filter { entrypoint ->
        entrypoint.provider.metadata.id == metadata.id
    }.map(EntrypointContainer<UpdaterEntrypoint>::getEntrypoint).firstOrNull()?.shouldCheck() ?: true)

    private fun ModMetadata.isDeftuMod() = authors.any { person ->
        person.name == "Deftu"
    }

    private fun ModMetadata.shouldCheck() = containsCustomValue("deftulib_upate_checking") && getCustomValue("deftulib_upate_checking").asBoolean

    private fun ModrinthVersionType.toText() = when (this) {
        ModrinthVersionType.RELEASE -> TextHelper.createTranslatableText("deftulib.update_checker.release").formatted(Formatting.GREEN)
        ModrinthVersionType.BETA -> TextHelper.createTranslatableText("deftulib.update_checker.beta").formatted(Formatting.YELLOW)
        ModrinthVersionType.ALPHA -> TextHelper.createTranslatableText("deftulib.update_checker.alpha").formatted(Formatting.RED)
    }

    private data class Update(
        val mod: ModMetadata,
        val version: ModrinthVersion
    )
}
