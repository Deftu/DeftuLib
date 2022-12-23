package xyz.deftu.lib

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import gg.essential.universal.ChatColor
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import okhttp3.OkHttpClient
import org.apache.logging.log4j.LogManager
import xyz.deftu.deftils.Multithreader
import xyz.deftu.lib.updater.UpdateChecker
import xyz.deftu.lib.utils.ChatPrefixType
import xyz.deftu.lib.utils.prefix
import xyz.deftu.enhancedeventbus.bus
import xyz.deftu.enhancedeventbus.invokers.LMFInvoker

object DeftuLib : ModInitializer {
    const val NAME = "@MOD_NAME@"
    const val VERSION = "@MOD_VERSION@"
    const val ID = "@MOD_ID@"

    var ENVIRONMENT = EnvType.CLIENT
        private set

    val PREFIX = prefix {
        name = NAME
        color = ChatColor.GOLD
        brackets {
            type = ChatPrefixType.CARET
            bold = true
            color = ChatColor.GRAY
        }
    }

    val LOGGER = LogManager.getLogger("@MOD_NAME@")

    val GSON by lazy {
        GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .setLenient()
            .create()
    }

    val EVENT_BUS = bus {
        invoker = LMFInvoker()
        threadSafety = true
        setExceptionHandler { e ->
            LOGGER.error("Caught exception while handling event", e)
        }
    }

    val MULTITHREADER by lazy {
        Multithreader(35)
    }

    val HTTP_CLIENT by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder()
                    .addHeader("User-Agent", "${NAME}/${VERSION}")
                    .build())
            }.build()
    }

    val BROWSER_HTTP_CLIENT by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder()
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36")
                    .build())
            }.build()
    }

    val UPDATE_CHECKER by lazy {
        UpdateChecker()
    }

    override fun onInitialize() {
    }

    fun setEnvironment(environment: EnvType) {
        ENVIRONMENT = environment
    }
}
