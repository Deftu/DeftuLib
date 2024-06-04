package xyz.deftu.lib.utils

object TextHelper {

    @JvmStatic
    fun createLiteralText(text: String) =
        net.minecraft.text.Text.literal(text)

    @JvmStatic
    fun createTranslatableText(key: String, vararg args: Any) =
        net.minecraft.text.Text.translatable(key, *args)

    @JvmStatic
    fun createEmptyText() =
        net.minecraft.text.Text.empty()

}
