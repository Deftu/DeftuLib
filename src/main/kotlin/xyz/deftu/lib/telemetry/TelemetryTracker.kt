package xyz.deftu.lib.telemetry

import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime

/**
 * Cry me a river, telemetry haters.
 *
 * This is completely GDPR-compliant and legal. In no way is tracking completely anonymized data ethically or morally wrong.
 * I could not care less about how you feel about it. If you really dislike it that much, simply don't use the mods. No one is forcing you to use what I make.
 * I am not going to remove this. I am not going to make it optional. I am not going to make it configurable. I am not going to make it opt-in.
 *
 * If you don't like it, don't use my mods. Simple as that.
 */
internal object TelemetryTracker {
    private val logger = LoggerFactory.getLogger("@MOD_NAME@ Telemetry Tracker")
    private val telemetryUrl: String
        get() = if (System.getProperty("deftu.debug") == "true") "http://localhost:3001/v1/telemetry/mod/install" else "https://api.deftu.xyz/v1/telemetry/mod/install"

    private lateinit var client: OkHttpClient
    private lateinit var environment: String
    private lateinit var gameVersion: String
    private lateinit var loaderName: String
    private lateinit var loaderVersion: String
    private lateinit var javaVersion: String
    private lateinit var javaVendor: String
    private lateinit var osName: String
    private lateinit var osVersion: String
    private lateinit var osArch: String

    fun recordPreLaunch(
        date: OffsetDateTime,
        environment: String,
        gameVersion: String,
        loaderName: String,
        loaderVersion: String,
        javaVersion: String,
        javaVendor: String,
        osName: String,
        osVersion: String,
        osArch: String
    ) {
        client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("User-Agent", "@MOD_NAME@/@MOD_VERSION@")
                    .build()
                chain.proceed(request)
            }.cache(null).build()

        this.environment = environment
        this.gameVersion = gameVersion
        this.loaderName = loaderName
        this.loaderVersion = loaderVersion
        this.javaVersion = javaVersion
        this.javaVendor = javaVendor
        this.osName = osName
        this.osVersion = osVersion
        this.osArch = osArch

        if (!UniquenessTracker.isUnique("@MOD_ID@", "@MOD_VERSION@", gameVersion, loaderName, loaderVersion)) {
            logger.info("Telemetry data already sent for @MOD_NAME@ on this version of Minecraft and this version of $loaderName.")
            return
        }

        val obj = JsonObject()
        obj.addProperty("date", date.toString())
        obj.addProperty("environment", environment)
        obj.addProperty("gameVersion", gameVersion)
        obj.addProperty("version", "@MOD_VERSION@")
        obj.add("loader", buildLoaderInfo(loaderName, loaderVersion))
        obj.add("java", buildJavaInfo(javaVersion, javaVendor))
        obj.add("os", buildOsInfo(osName, osVersion, osArch))
        val response = client.newCall(
            Request.Builder()
                .url("$telemetryUrl/@MOD_ID@")
                .post(obj.toString().toRequestBody("application/json".toMediaTypeOrNull()))
                .build()
        ).execute()
        if (!response.isSuccessful) {
            logger.error("Failed to send telemetry data: ${response.code} ${response.message}")
        } else {
            UniquenessTracker.setPresent("@MOD_ID@", "@MOD_VERSION@", gameVersion, loaderName, loaderVersion)
            logger.info("Successfully sent telemetry data for @MOD_NAME@.")
        }
    }

    fun record(
        id: String,
        version: String
    ) {
        if (!UniquenessTracker.isUnique(id, version, gameVersion, loaderName, loaderVersion)) {
            logger.info("Telemetry data already sent for $id on this version of Minecraft and this version of $loaderName.")
            return
        }

        val obj = JsonObject()
        obj.addProperty("date", OffsetDateTime.now().toString())
        obj.addProperty("environment", environment)
        obj.addProperty("gameVersion", gameVersion)
        obj.addProperty("version", version)
        obj.add("loader", buildLoaderInfo(loaderName, loaderVersion))
        obj.add("java", buildJavaInfo(javaVersion, javaVendor))
        obj.add("os", buildOsInfo(osName, osVersion, osArch))
        val response = client.newCall(
            Request.Builder()
                .url("$telemetryUrl/$id")
                .post(obj.toString().toRequestBody("application/json".toMediaTypeOrNull()))
                .build()
        ).execute()
        if (!response.isSuccessful) {
            logger.error("Failed to send telemetry data: ${response.code} ${response.message}")
        } else {
            UniquenessTracker.setPresent(id, version, gameVersion, loaderName, loaderVersion)
            logger.info("Successfully sent telemetry data for $id.")
        }
    }

    private fun buildLoaderInfo(loaderName: String, loaderVersion: String): JsonObject {
        val obj = JsonObject()
        obj.addProperty("type", loaderName)
        obj.addProperty("version", loaderVersion)
        return obj
    }

    private fun buildJavaInfo(javaVersion: String, javaVendor: String): JsonObject {
        val obj = JsonObject()
        obj.addProperty("version", javaVersion)
        obj.addProperty("vendor", javaVendor)
        return obj
    }

    private fun buildOsInfo(osName: String, osVersion: String, osArch: String): JsonObject {
        val obj = JsonObject()
        obj.addProperty("name", osName)
        obj.addProperty("version", osVersion)
        obj.addProperty("arch", osArch)
        return obj
    }
}
