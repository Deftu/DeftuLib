package xyz.deftu.lib.utils

object TextHelper {
    fun createLiteralText(text: String) =
        //#if MC>=11900
        net.minecraft.text.Text.literal(text)
        //#else
        //$$ net.minecraft.text.LiteralText(text)
        //#endif
    fun createTranslatableText(key: String, vararg args: Any) =
        //#if MC>=11900
        net.minecraft.text.Text.translatable(key, *args)
        //#else
        //$$ net.minecraft.text.TranslatableText(key, *args)
        //#endif
    fun createEmptyText() =
        //#if MC>=11900
        net.minecraft.text.Text.empty()
        //#else
        //$$ net.minecraft.text.Text.EMPTY
        //#endif
}
