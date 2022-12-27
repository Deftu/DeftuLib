package xyz.deftu.lib.prelaunch

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint
import net.fabricmc.loader.impl.FabricLoaderImpl
import net.minecraft.SharedConstants
import org.apache.logging.log4j.LogManager

class DeftuLibPreLaunch : PreLaunchEntrypoint {
    private val logger = LogManager.getLogger("@MOD_NAME@ Pre-Launch")

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

    override fun onPreLaunch() {
        logger.warn("Printing information about the current environment...")
        logger.info("Loading Minecraft ${SharedConstants.VERSION_NAME}")
        logger.info("Using ${if (isUsingQuilt()) "Quilt ${getQuiltVersion()}" else "Fabric ${FabricLoaderImpl.VERSION}"}")
        logger.info("Using Java ${System.getProperty("java.version")} (${System.getProperty("java.vendor")})")
        logger.info("Using OS: ${System.getProperty("os.name")} (${System.getProperty("os.version")})")
        logger.info("Using OS architecture: ${System.getProperty("os.arch")}")
    }
}
