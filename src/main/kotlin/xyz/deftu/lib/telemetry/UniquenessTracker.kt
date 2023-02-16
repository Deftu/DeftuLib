package xyz.deftu.lib.telemetry

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File

internal object UniquenessTracker {
    private val file: File
        get() {
            val file = File("deftu/telemetry.json")
            if (!file.exists()) {
                file.parentFile.mkdirs()
                file.createNewFile()
                file.writeText("{}")
            }

            return file
        }

    fun isUnique(
        modId: String,
        modVersion: String,
        gameVersion: String,
        loaderName: String,
        loaderVersion: String
    ): Boolean {
        val json = file.readText()
        val obj = JsonParser.parseString(json).asJsonObject
        val loader = obj.get(loaderName)?.asJsonObject ?: run {
            obj.add(loaderName, JsonObject())
            return true
        }

        val loaderVersionObj = loader.get(loaderVersion)?.asJsonObject ?: run {
            loader.add(loaderVersion, JsonObject())
            return true
        }

        val gameVersionObj = loaderVersionObj.get(gameVersion)?.asJsonArray ?: run {
            loaderVersionObj.add(gameVersion, JsonArray())
            return true
        }

        val mods = gameVersionObj.map {
            if (it.isJsonPrimitive) return true

            val id = it.asJsonObject.get("id")?.asString ?: return true
            val version = it.asJsonObject.get("version")?.asString ?: return true
            id to version
        }

        return mods.none { (id, version) -> id == modId && version == modVersion }
    }

    fun setPresent(
        modId: String,
        modVersion: String,
        gameVersion: String,
        loaderName: String,
        loaderVersion: String
    ) {
        val json = file.readText()
        val obj = JsonParser.parseString(json).asJsonObject
        val loader = obj.get(loaderName)?.asJsonObject ?: run {
            val loader = JsonObject()
            obj.add(loaderName, loader)
            loader
        }

        val loaderVersionObj = loader.get(loaderVersion)?.asJsonObject ?: run {
            val loaderVersionObj = JsonObject()
            loader.add(loaderVersion, loaderVersionObj)
            loaderVersionObj
        }

        val gameVersionObj = loaderVersionObj.get(gameVersion)?.asJsonArray ?: run {
            val gameVersionObj = JsonArray()
            loaderVersionObj.add(gameVersion, gameVersionObj)
            gameVersionObj
        }

        if (gameVersionObj.map { it.asJsonObject.get("id")?.asString }.contains(modId)) return

        val mod = JsonObject()
        mod.addProperty("id", modId)
        mod.addProperty("version", modVersion)
        gameVersionObj.add(mod)

        // remove all primitives - these are leftovers from versions 1.5.0 and 1.5.1
        val newGameVersionObj = JsonArray()
        gameVersionObj.forEach {
            if (it.isJsonObject) newGameVersionObj.add(it)
        }
        loaderVersionObj.add(gameVersion, newGameVersionObj)

        file.writeText(obj.toString())
    }
}
