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
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.apache.logging.log4j.LogManager
import xyz.deftu.lib.DeftuLib
import xyz.deftu.lib.utils.ChatHelper
import xyz.deftu.lib.utils.TextHelper
import java.util.concurrent.TimeUnit

class UpdateChecker {
    private val logger = LogManager.getLogger("${DeftuLib.NAME} Update Checker")
    private val entrypoints: List<EntrypointContainer<UpdaterEntrypoint>>
        get() = FabricLoader.getInstance().getEntrypointContainers("update_checker", UpdaterEntrypoint::class.java)

    private val modrinth = ModrinthClient()
    private val updates = mutableListOf<Update>()

    fun start() {
        sendUpdateNotifications()
        val mods = FabricLoader.getInstance().allMods.filter { container ->
            container.metadata.isDeftuMod() && container.shouldCheckForUpdates()
        }

        // check for updates in another thread (async checking)
        DeftuLib.MULTITHREADER.schedule({
            logger.info("Checking for updates...")
            updates.clear()
            runBlocking {
                mods.forEach { container ->
                    if (!container.shouldCheckForUpdates()) return@forEach

                    val file = container.origin.paths[0]?.toFile() ?: return@forEach
                    if (!file.exists()) return@forEach

                    val modrinthVersion = modrinth.fetch(file) ?: return@forEach
                    val version = VersionParser.parseSemantic(modrinthVersion.versionNumber) ?: return@forEach
                    val currentVersion = VersionParser.parseSemantic(container.metadata.version.friendlyString) ?: return@forEach

                    if (version > currentVersion) {
                        updates.add(Update(container.metadata, modrinthVersion))
                    }
                }

                logger.info("Finished checking for updates!")
                sendUpdateNotifications()
            }
        }, 0, 30, TimeUnit.MINUTES)
    }

    private fun constructUpdateMessage(update: Update): Text =
        TextHelper.createTranslatableText(
            "deftulib.update_checker.text",
            update.version.versionType.toText().formatted(Formatting.UNDERLINE).styled {
                it.withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, update.version.versionUrl))
            },
            TextHelper.createLiteralText(update.mod.name).formatted(Formatting.AQUA, Formatting.UNDERLINE).styled {
                it.withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, update.version.pageUrl))
            }
        )

    private fun ModContainer.shouldCheckForUpdates() = (entrypoints.filter { entrypoint ->
        entrypoint.provider.metadata.id == metadata.id
    }.map(EntrypointContainer<UpdaterEntrypoint>::getEntrypoint).firstOrNull()?.shouldCheck() ?: true)

    private fun ModMetadata.isDeftuMod() = authors.any { person ->
        person.name == "Deftu"
    }

    private fun ModrinthVersionType.toText() = when (this) {
        ModrinthVersionType.RELEASE -> TextHelper.createTranslatableText("deftulib.update_checker.release").formatted(Formatting.GREEN)
        ModrinthVersionType.BETA -> TextHelper.createTranslatableText("deftulib.update_checker.beta").formatted(Formatting.YELLOW)
        ModrinthVersionType.ALPHA -> TextHelper.createTranslatableText("deftulib.update_checker.alpha").formatted(Formatting.RED)
    }

    private fun sendUpdateNotifications() {
        if (updates.isEmpty()) return

        if (DeftuLib.ENVIRONMENT == EnvType.CLIENT) {
            ClientPlayConnectionEvents.JOIN.register { _, _, _ ->
                for (update in updates) {
                    ChatHelper.sendClientMessage(constructUpdateMessage(update))
                }
            }
        } else {
            ServerLifecycleEvents.SERVER_STARTED.register {
                for (update in updates) {
                    logger.info("Update available for ${update.mod.name} in the ${update.version.versionType.name.lowercase()} channel! (${update.version.versionNumber}, ${update.version.versionUrl})")
                }
            }
        }
    }

    private data class Update(
        val mod: ModMetadata,
        val version: ModrinthVersion
    )
}
