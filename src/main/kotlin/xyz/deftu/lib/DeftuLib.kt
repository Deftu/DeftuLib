package xyz.deftu.lib

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import gg.essential.universal.ChatColor
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.metadata.ModMetadata
import okhttp3.OkHttpClient
import org.apache.logging.log4j.LogManager
import xyz.deftu.lib.events.EnvironmentSetupEvent
import xyz.deftu.lib.updater.UpdateChecker
import xyz.deftu.lib.utils.ChatPrefixType
import xyz.deftu.lib.utils.prefix
import xyz.deftu.enhancedeventbus.bus
import xyz.deftu.enhancedeventbus.invokers.LMFInvoker
import xyz.deftu.lib.utils.Multithreader
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

object DeftuLib : ModInitializer {
    const val NAME = "@MOD_NAME@"
    const val VERSION = "@MOD_VERSION@"
    const val ID = "@MOD_ID@"

    @JvmStatic
    var environment = EnvType.CLIENT
        private set

    @JvmStatic
    val prefix = prefix {
        name = NAME
        color = ChatColor.GOLD
        brackets {
            type = ChatPrefixType.CARET
            bold = true
            color = ChatColor.GRAY
        }
    }

    @JvmStatic
    val logger = LogManager.getLogger(NAME)

    @JvmStatic
    val gson by lazy {
        GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .setLenient()
            .create()
    }

    @JvmStatic
    val eventBus = bus {
        invoker = LMFInvoker()
        threadSafety = true
        setExceptionHandler { e ->
            logger.error("Caught exception while handling event", e)
        }
    }

    @JvmStatic
    val multithreader by lazy {
        Multithreader(35)
    }

    @JvmStatic
    val httpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder()
                    .addHeader("User-Agent", "${NAME}/${VERSION}")
                    .build())
            }.build()
    }

    @JvmStatic
    val browserHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder()
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36")
                    .build())
            }.build()
    }

    @JvmStatic
    val updateChecker by lazy {
        UpdateChecker()
    }

    @JvmStatic
    var firstLaunch = false
        private set

    override fun onInitialize() {
        DeftuLibConfig.preload()
        if (DeftuLibConfig.firstLaunch) {
            firstLaunch = true
            DeftuLibConfig.firstLaunch = false
            DeftuLibConfig.markDirty()
            DeftuLibConfig.writeData()
        }

        EnvironmentSetupEvent.EVENT.register { type ->
            environment = type
            updateChecker.start()
            setupTelemetry()
        }
    }

    private fun setupTelemetry() {
        logger.warn("Printing information about the current environment...")
        val date = OffsetDateTime.now()
        val gameEnv = FabricLoader.getInstance().environmentType.name.lowercase()
        val gameVersion = FabricLoader.getInstance().getModContainer("minecraft").get().metadata.version.friendlyString
        val loaderName = if (isUsingQuilt()) "Quilt" else "Fabric"
        val loaderVersion = (if (isUsingQuilt()) getQuiltVersion() else getFabricVersion()) ?: "Unknown"
        val javaVersion = System.getProperty("java.version")
        val javaVendor = System.getProperty("java.vendor")
        val osName = System.getProperty("os.name")
        val osVersion = System.getProperty("os.version")
        val osArch = System.getProperty("os.arch")

        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val lines = listOf(
            "Launching at ${date.format(dateFormatter)}",
            "Loading Minecraft $gameVersion as a ${gameEnv.lowercase()}",
            "Using $loaderName $loaderVersion",
            "Using Java $javaVersion ($javaVendor)",
            "Using $osName ($osVersion)",
            "Using architecture: $osArch"
        )
        val seperator = "-".repeat(lines.maxOf(String::length))

        logger.info(seperator)
        lines.forEach(logger::info)
        logger.info(seperator)
        logger.warn("Finished printing information about the current environment.")
    }

    private fun isUsingQuilt() = try {
        Class.forName("org.quiltmc.loader.impl.QuiltLoaderImpl")
        true
    } catch (e: ClassNotFoundException) {
        false
    }

    private fun getQuiltVersion() = try {
        Class.forName("org.quiltmc.loader.impl.QuiltLoaderImpl").getDeclaredField("VERSION").get(null) as String
    } catch (e: Exception) {
        null
    }

    private fun getFabricVersion() = try {
        Class.forName("net.fabricmc.loader.impl.FabricLoaderImpl").getDeclaredField("VERSION").get(null) as String
    } catch (e: Exception) {
        null
    }

    private fun ModMetadata.isDeftuMod() = authors.any { person ->
        person.name == "Deftu"
    }
}
