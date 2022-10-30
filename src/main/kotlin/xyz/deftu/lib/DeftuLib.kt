package xyz.deftu.lib

import net.fabricmc.api.ModInitializer
import okhttp3.OkHttpClient
import org.apache.logging.log4j.LogManager
import xyz.deftu.deftils.Multithreader
import xyz.enhancedpixel.enhancedeventbus.bus
import xyz.enhancedpixel.enhancedeventbus.invokers.LMFInvoker

object DeftuLib : ModInitializer {
    val LOGGER = LogManager.getLogger("@MOD_NAME@")

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
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36")
                    .build())
            }.build()
    }

    override fun onInitialize() {
    }
}
