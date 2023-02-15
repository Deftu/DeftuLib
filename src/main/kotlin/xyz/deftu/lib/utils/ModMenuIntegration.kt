package xyz.deftu.lib.utils

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import xyz.deftu.lib.DeftuLibConfig

class ModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory() = ConfigScreenFactory { parent ->
        DeftuLibConfig.gui()
    }
}
