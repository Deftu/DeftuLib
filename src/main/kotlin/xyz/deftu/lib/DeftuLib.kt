package xyz.deftu.lib

import com.google.gson.GsonBuilder
import gg.essential.universal.ChatColor
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import okhttp3.OkHttpClient
import org.apache.logging.log4j.LogManager
import xyz.deftu.deftils.Multithreader
import xyz.deftu.lib.events.EnvironmentSetupEvent
import xyz.deftu.lib.updater.UpdateChecker
import xyz.deftu.lib.utils.ChatPrefixType
import xyz.deftu.lib.utils.prefix
import xyz.enhancedpixel.enhancedeventbus.bus
import xyz.enhancedpixel.enhancedeventbus.invokers.LMFInvoker

object DeftuLib : ModInitializer {
    const val NAME = "@MOD_NAME@"
    const val VERSION = "@MOD_VERSION@"
    const val ID = "@MOD_ID@"

    @JvmStatic
    var ENVIRONMENT = EnvType.CLIENT
        private set

    @JvmStatic
    val PREFIX = prefix {
        name = NAME
        color = ChatColor.GOLD
        brackets {
            type = ChatPrefixType.CARET
            bold = true
            color = ChatColor.GRAY
        }
    }

    @JvmStatic
    val LOGGER = LogManager.getLogger("@MOD_NAME@")

    @JvmStatic
    val EVENT_BUS = bus {
        invoker = LMFInvoker()
        threadSafety = true
        setExceptionHandler { e ->
            LOGGER.error("Caught exception while handling event", e)
        }
    }

    @JvmStatic
    val MULTITHREADER by lazy {
        Multithreader(35)
    }

    @JvmStatic
    val HTTP_CLIENT by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                chain.proceed(chain.request().newBuilder()
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36")
                    .build())
            }.build()
    }

    @JvmStatic
    val GSON by lazy {
        GsonBuilder()
            .setPrettyPrinting()
            .setLenient()
            .create()
    }

    @JvmStatic
    val UPDATE_CHECKER by lazy {
        UpdateChecker()
    }

    override fun onInitialize() {
        DeftuLibConfig.load()
        EnvironmentSetupEvent.EVENT.register { type ->
            ENVIRONMENT = type
            UPDATE_CHECKER.start()
        }
    }
}
