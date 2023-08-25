package xyz.deftu.lib

import com.google.gson.JsonParser
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import net.fabricmc.loader.api.FabricLoader
import java.io.File
import java.nio.file.Files

private val configDir by lazy {
    val configDir = File(FabricLoader.getInstance().configDir.toFile(), "Deftu")
    if (!configDir.exists() && !configDir.mkdirs())
        throw IllegalStateException("Could not create config directory!")

    val modDir = File(configDir, DeftuLib.ID)
    if (!modDir.exists() && !modDir.mkdirs())
        throw IllegalStateException("Could not create mod directory!")

    modDir
}

private val deftuDir by lazy {
    val deftuDir = File(FabricLoader.getInstance().gameDir.toFile(), "Deftu")
    if (!deftuDir.exists() && !deftuDir.mkdirs())
        throw IllegalStateException("Could not create Deftu directory!")

    deftuDir
}

private val legacyConfigFile by lazy {
    File(configDir, "config.json")
}

private val configFile by lazy {
    File(deftuDir, "@MOD_ID@.toml")
}

object DeftuLibConfig : Vigilant(
    file = configFile,
    guiTitle = "${DeftuLib.NAME} Config",
) {
    @JvmStatic
    @Property(
        type = PropertyType.SWITCH,
        name = "First Launch",
        category = "General",
        hidden = true
    ) var firstLaunch = true

    @JvmStatic
    @Property(
        type = PropertyType.SWITCH,
        name = "Update Checking",
        description = "Enables update checking. This will toggle these checks in all mods made by Deftu.",
        category = "General"
    ) var updateChecking = true
    @JvmStatic
    @Property(
        type = PropertyType.SWITCH,
        name = "Dark Mode",
        description = "Enables dark mode.",
        category = "General"
    ) var darkMode = true

    init {
        loadLegacyConfig()
        initialize()
    }

    private fun loadLegacyConfig() {
        if (!legacyConfigFile.exists()) return

        val element = Files.readString(legacyConfigFile.toPath()).let(JsonParser::parseString)
        if (!element.isJsonObject) return

        val json = element.asJsonObject
        json.get("update_checking")?.asBoolean?.let { updateChecking = it }
        json.get("dark_mode")?.asBoolean?.let { darkMode = it }
        legacyConfigFile.delete()
    }
}
