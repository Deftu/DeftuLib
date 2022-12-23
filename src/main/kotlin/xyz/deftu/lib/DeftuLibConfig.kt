package xyz.deftu.lib

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import dev.isxander.yacl.api.ConfigCategory
import dev.isxander.yacl.api.Option
import dev.isxander.yacl.api.YetAnotherConfigLib
import dev.isxander.yacl.gui.controllers.TickBoxController
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import xyz.deftu.lib.utils.TextHelper
import java.io.File
import java.nio.file.Files

object DeftuLibConfig {
    private val configFile by lazy {
        val configDir = File(FabricLoader.getInstance().configDir.toFile(), "Deftu")
        if (!configDir.exists() && !configDir.mkdirs())
            throw IllegalStateException("Could not create config directory!")

        val modDir = File(configDir, DeftuLib.ID)
        if (!modDir.exists() && !modDir.mkdirs())
            throw IllegalStateException("Could not create mod directory!")

        File(modDir, "config.json").toPath()
    }

    var updateChecking = true
    var darkMode = true

    @JvmStatic
    fun save() {
        Files.deleteIfExists(configFile)

        val json = JsonObject()
        json.addProperty("update_checking", updateChecking)
        json.addProperty("dark_mode", darkMode)

        Files.writeString(configFile, json.toString())
    }

    @JvmStatic
    fun load() {
        if (Files.notExists(configFile)) {
            save()
            return
        }

        val element = Files.readString(configFile).let(JsonParser::parseString)
        if (!element.isJsonObject) {
            save()
            return
        }

        val json = element.asJsonObject
        updateChecking = json.get("update_checking")?.asBoolean ?: save().let { return }
        darkMode = json.get("dark_mode")?.asBoolean ?: save().let { return }
    }

    @JvmStatic
    fun createMenu(parent: Screen? = MinecraftClient.getInstance().currentScreen) = YetAnotherConfigLib.createBuilder()
        .title(TextHelper.createTranslatableText("${DeftuLib.ID}.config.title"))
        .category(ConfigCategory.createBuilder()
            .name(TextHelper.createTranslatableText("${DeftuLib.ID}.config.category.general"))
            .option(Option.createBuilder(Boolean::class.java)
                .name(TextHelper.createTranslatableText("${DeftuLib.ID}.config.option.update_checking"))
                .binding(true, DeftuLibConfig::updateChecking) {
                        value -> updateChecking = value
                }.controller { option ->
                    TickBoxController(option)
                }.build())
            .build())
        .category(ConfigCategory.createBuilder()
            .name(TextHelper.createTranslatableText("${DeftuLib.ID}.config.category.appearance"))
            .option(Option.createBuilder(Boolean::class.java)
                .name(TextHelper.createTranslatableText("${DeftuLib.ID}.config.option.dark_mode"))
                .binding(true, DeftuLibConfig::darkMode) {
                        value -> darkMode = value
                }.controller { option ->
                    TickBoxController(option)
                }.build())
            .build())
        .save(DeftuLibConfig::save)
        .build()
        .generateScreen(parent)
}
