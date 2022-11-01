package xyz.deftu.lib.updater

import kotlinx.coroutines.runBlocking
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.ModContainer
import net.fabricmc.loader.api.entrypoint.EntrypointContainer
import net.fabricmc.loader.api.metadata.ModMetadata
import net.fabricmc.loader.impl.util.version.VersionParser
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
        ClientPlayConnectionEvents.JOIN.register { _, _, _ ->
            for (update in updates) {
                ChatHelper.sendClientMessage(Text.translatable("deftulib.update_checker.text", update.version.versionType.toText(), TextHelper.createLiteralText(update.mod.name).formatted(Formatting.AQUA)))
            }
        }

        ServerLifecycleEvents.SERVER_STARTED.register {
            for (update in updates) {
                logger.info("Update available for ${update.mod.name} in the ${update.version.versionType.name.lowercase()} channel! (${update.version.versionNumber})")
            }
        }

        val mods = FabricLoader.getInstance().allMods.filter { container ->
            container.shouldCheckForUpdates() && container.metadata.isDeftuMod()
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

                    if (version > currentVersion || container.metadata.id == "noteable") {
                        updates.add(Update(container.metadata, modrinthVersion))
                    }
                }
            }
        }, 0, 30, TimeUnit.MINUTES)
    }

    private fun ModContainer.shouldCheckForUpdates() = (entrypoints.filter { entrypoint ->
        entrypoint.provider.metadata.id == metadata.id
    }.map(EntrypointContainer<UpdaterEntrypoint>::getEntrypoint).firstOrNull()?.shouldCheck() ?: true)

    private fun ModMetadata.isDeftuMod() = authors.any { person ->
        person.name == "Deftu"
    }

    private fun ModrinthVersionType.toText() = when (this) {
        ModrinthVersionType.release -> TextHelper.createTranslatableText("deftulib.update_checker.release").formatted(Formatting.GREEN)
        ModrinthVersionType.beta -> TextHelper.createTranslatableText("deftulib.update_checker.beta").formatted(Formatting.YELLOW)
        ModrinthVersionType.alpha -> TextHelper.createTranslatableText("deftulib.update_checker.alpha").formatted(Formatting.RED)
    }

    private data class Update(
        val mod: ModMetadata,
        val version: ModrinthVersion
    )
}
