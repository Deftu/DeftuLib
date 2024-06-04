package xyz.deftu.lib.utils

//#if MC <= 1.18.2
//$$ import net.minecraft.text.LiteralText
//$$ import net.minecraft.text.TranslatableText
//#endif

import net.minecraft.text.MutableText
import net.minecraft.text.Text

object TextHelper {

    @JvmStatic
    fun createLiteralText(text: String): MutableText {
        //#if MC >= 1.19.2
        return Text.literal(text)
        //#else
        //$$ return LiteralText(text)
        //#endif
    }

    @JvmStatic
    fun createTranslatableText(key: String, vararg args: Any): MutableText {
        //#if MC >= 1.19.2
        return Text.translatable(key, args)
        //#else
        //$$ return TranslatableText(key, *args)
        //#endif
    }

    @JvmStatic
    fun createEmptyText(): MutableText {
        //#if MC >= 1.19.2
        return Text.empty()
        //#else
        //$$ return LiteralText("")
        //#endif
    }

}
