package dev.deftu.lib.client.gui

import dev.deftu.lib.DeftuLibConfig
import dev.deftu.lib.client.utils.withAlphaPercentage
import java.awt.Color

object DeftuPalette {
    // Primary colors
    private val primary = Color(0xC33F3F)
    private val primaryVariant = Color(0xA63A3A)

    // Background colors
    private val darkBackground1 = Color(0x28282B)
    private val darkBackground2 = Color(0x212125)
    private val lightBackground1 = Color(0xDDDDDD)
    private val lightBackground2 = Color(0xD1D1D1)

    // Text colors
    private val text = Color(0xFDFBF9)
    private val textFaded = Color(0xD2CDC6).withAlphaPercentage(0.65f)
    private val textDisabled = Color(0xD2CDC6).withAlphaPercentage(0.25f)
    private val textLight = Color(0x28282B)
    private val textLightFaded = Color(0x28282B).withAlphaPercentage(0.65f)
    private val textLightDisabled = Color(0x333333).withAlphaPercentage(0.25f)

    // State colors
    private val success = Color(0x05F140)
    private val error = Color(0xC8001F)
    private val warning = Color(0xDAAA3C)
    private val on = Color(0x038700)
    private val off = Color(0x8C0317)

    // Public access
    fun getPrimary() = primary
    fun getPrimaryVariant() = primaryVariant
    fun getBackground() = if (DeftuLibConfig.darkMode) darkBackground1 else lightBackground1
    fun getBackground2() = if (DeftuLibConfig.darkMode) darkBackground2 else lightBackground2
    fun getText() = if (DeftuLibConfig.darkMode) text else textLight
    fun getTextFaded() = if (DeftuLibConfig.darkMode) textFaded else textLightFaded
    fun getTextDisabled() = if (DeftuLibConfig.darkMode) textDisabled else textLightDisabled
    fun getSuccessState() = success
    fun getErrorState() = error
    fun getOffState() = off
}
