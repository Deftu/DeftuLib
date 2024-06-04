package dev.deftu.lib.client.utils

import java.awt.Color

/**
 * @author Deftu
 * @since 1.4.0
 * @param percentage The percentage between 0 and 1
 */
fun Color.withAlphaPercentage(percentage: Float): Color {
    val alpha = (255 * percentage).toInt()
    return Color(red, green, blue, alpha)
}
