package dev.deftu.lib.client.gui

import gg.essential.elementa.utils.withAlpha
import dev.deftu.lib.DeftuLibConfig
import dev.deftu.lib.client.utils.withAlphaPercentage
import java.awt.Color

object DeftuPalette {
    // Primary colors
    private val primary = Color(0xC91212)
    private val primaryVariant = Color(0xA90F0F)

    // Background colors
    private val darkBackground1 = Color(0x28282B)
    private val darkBackground2 = Color(0x151316)
    private val darkBackground3 = Color(0x1D1D20)
    private val darkBackground4 = Color(0x131316)
    private val lightBackground1 = Color(0xFDFBF9)
    private val lightBackground2 = Color(0xDBD7D2)
    private val lightBackground3 = Color(0xFAF5F0)
    private val lightBackground4 = Color(0xD2CDC6)

    // Button outline colors
    private val button = Color(0x960D0D)
    private val buttonFaded = Color(0x960D0D).withAlphaPercentage(0.75f)

    // Text colors
    private val text = Color(0xFDFBF9)
    private val textFaded = Color(0xFDFBF9).withAlphaPercentage(0.75f)
    private val textDisabled = Color(0xFDFBF9).withAlpha(0.25f)
    private val textLight = Color(0x28282B)
    private val textLightFaded = Color(0x28282B).withAlphaPercentage(0.75f)
    private val textLightDisabled = Color(0x28282B).withAlpha(0.25f)

    // State colors
    private val success = Color(0x05F140)
    private val error = Color(0xDD0426)
    private val off = Color(0x8C0317)

    // Public access
    fun getPrimary() = primary
    fun getPrimaryVariant() = primaryVariant
    fun getBackground() = if (DeftuLibConfig.darkMode) darkBackground1 else lightBackground1
    fun getBackground2() = if (DeftuLibConfig.darkMode) darkBackground2 else lightBackground2
    fun getBackground3() = if (DeftuLibConfig.darkMode) darkBackground3 else lightBackground3
    fun getBackground4() = if (DeftuLibConfig.darkMode) darkBackground4 else lightBackground4
    fun getButton() = button
    fun getButtonFaded() = buttonFaded
    fun getText() = if (DeftuLibConfig.darkMode) text else textLight
    fun getTextFaded() = if (DeftuLibConfig.darkMode) textFaded else textLightFaded
    fun getTextDisabled() = if (DeftuLibConfig.darkMode) textDisabled else textLightDisabled
    fun getSuccessState() = success
    fun getErrorState() = error
    fun getOffState() = off
}
