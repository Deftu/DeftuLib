package dev.deftu.lib.utils

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import dev.deftu.lib.DeftuLibConfig

class ModMenuIntegration : ModMenuApi {

    override fun getModConfigScreenFactory() = ConfigScreenFactory { _ ->
        DeftuLibConfig.gui()
    }

}
