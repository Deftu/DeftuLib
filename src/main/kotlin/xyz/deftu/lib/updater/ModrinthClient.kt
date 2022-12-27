package xyz.deftu.lib.updater

import com.google.gson.*
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import kotlinx.coroutines.delay
import net.minecraft.SharedConstants
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import xyz.deftu.lib.DeftuLib
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

internal class ModrinthClient {
    private val gson = DeftuLib.gson.newBuilder()
        .registerTypeAdapter(ModrinthVersionType::class.java, ModrinthVersionType.Adapter())
        .create()

    private var lastRequest = 0L

    suspend fun fetch(file: File): ModrinthVersion? {
        if (!file.exists() || !file.isFile) return null

        val currentTime = System.currentTimeMillis()
        if (currentTime - lastRequest < 2500) {
            // Wait 2.5 seconds between requests
            delay(2500 - (currentTime - lastRequest))
        }

        lastRequest = currentTime

        val response = DeftuLib.httpClient.newCall(
            Request.Builder()
                .post(constructBody().toRequestBody("application/json".toMediaType()))
                .url("https://api.modrinth.com/v2/version_file/${createSha1(file)}/update?algorithm=sha1")
                .build()
        ).execute()
        val body = response.body?.string() ?: return null

        val element = JsonParser.parseString(body)
        if (!element.isJsonObject) return null

        val json = element.asJsonObject
        return gson.fromJson(json, ModrinthVersion::class.java)
    }

    private fun constructBody(): String {
        val json = JsonObject()

        val loaders = JsonArray()
        loaders.add("fabric")
        json.add("loaders", loaders)

        val versions = JsonArray()
        val version = SharedConstants.getGameVersion().id

        val dotCount = version.count { it == '.' }
        if (dotCount > 0) {
            var patch = version.substringAfterLast('.')
            val major = version.substringBeforeLast('.')
            versions.add(major)
            while (patch.toIntOrNull() != null) {
                if (patch.toInt() <= 0) break
                versions.add("$major.$patch")
                patch = (patch.toInt() - 1).toString()
            }
        } else versions.add(version)

        json.add("game_versions", versions)

        return json.toString()
    }

    // the hash of the file, considering its byte content and encoded in hexadecimal
    private fun createSha1(file: File): String {
        val digest = MessageDigest.getInstance("SHA-1")
        val fis = FileInputStream(file)
        val buffer = ByteArray(1024)
        var read = fis.read(buffer)
        while (read != -1) {
            digest.update(buffer, 0, read)
            read = fis.read(buffer)
        }
        fis.close()

        val bytes = digest.digest()
        val sb = StringBuilder()
        for (b in bytes) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString()
    }
}

enum class ModrinthVersionType {
    RELEASE,
    ALPHA,
    BETA;

    class Adapter : TypeAdapter<ModrinthVersionType>() {
        override fun write(out: JsonWriter, value: ModrinthVersionType) {
            out.value(value.name.lowercase())
        }

        override fun read(input: JsonReader): ModrinthVersionType {
            return valueOf(input.nextString().uppercase())
        }
    }
}

data class ModrinthVersion(
    val projectId: String,
    val versionNumber: String,
    val versionType: ModrinthVersionType
) {
    val pageUrl: String
        get() = "https://modrinth.com/mod/$projectId"
    val versionUrl: String
        get() = "$pageUrl/version/$versionNumber"
}
