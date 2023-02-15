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

        val mods = gameVersionObj.map { it.asString }
        return !mods.contains(modId)
    }

    fun setPresent(
        modId: String,
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

        gameVersionObj.add(modId)
        file.writeText(obj.toString())
    }
}
