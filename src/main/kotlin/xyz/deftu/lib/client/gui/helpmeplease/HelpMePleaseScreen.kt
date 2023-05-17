package xyz.deftu.lib.client.gui.helpmeplease

import gg.essential.universal.ChatColor
import gg.essential.universal.GuiScale
import gg.essential.universal.UMatrixStack
import gg.essential.universal.UScreen
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.*
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.text.Text
import net.minecraft.util.Util
import xyz.deftu.lib.DeftuLibConfig

class HelpMePleaseScreen : UScreen(
    restoreCurrentGuiOnClose = true,
    newGuiScale = GuiScale.Medium.ordinal
) {
    //#if MC<11903
    //$$ private abstract class AbstractTextWidget(i: Int, j: Int, k: Int, l: Int, text: Text, textRenderer: TextRenderer) :
    //$$     ClickableWidget(i, j, k, l, text) {
    //$$     @JvmField
    //$$     val textRenderer: TextRenderer
    //$$     @JvmField
    //$$     var textColor = 16777215
    //$$
    //$$     init {
    //$$         this.textRenderer = textRenderer
    //$$     }
    //$$
    //$$     override fun appendNarrations(narrationMessageBuilder: NarrationMessageBuilder?) {}
    //$$     open fun setTextColor(i: Int): AbstractTextWidget? {
    //$$         textColor = i
    //$$         return this
    //$$     }
    //$$
    //$$     protected fun getTextRenderer(): TextRenderer {
    //$$         return textRenderer
    //$$     }
    //$$
    //$$     protected fun getTextColor(): Int {
    //$$         return textColor
    //$$     }
    //$$ }
    //$$
    //$$ private class TextWidget(i: Int, j: Int, k: Int, l: Int, text: Text, textRenderer: TextRenderer) :
    //$$     AbstractTextWidget(i, j, k, l, text, textRenderer) {
    //$$     private var horizontalAlignment = 0.5f
    //$$
    //$$     constructor(text: Text, textRenderer: TextRenderer) : this(
    //$$         0,
    //$$         0,
    //$$         textRenderer.getWidth(text.asOrderedText()),
    //$$         9,
    //$$         text,
    //$$         textRenderer
    //$$     )
    //$$
    //$$     constructor(i: Int, j: Int, text: Text, textRenderer: TextRenderer) : this(0, 0, i, j, text, textRenderer)
    //$$
    //$$     init {
    //$$         active = false
    //$$     }
    //$$
    //$$     override fun setTextColor(i: Int): TextWidget {
    //$$         super.setTextColor(i)
    //$$         return this
    //$$     }
    //$$
    //$$     private fun align(f: Float): TextWidget {
    //$$         horizontalAlignment = f
    //$$         return this
    //$$     }
    //$$
    //$$     fun alignLeft(): TextWidget {
    //$$         return align(0.0f)
    //$$     }
    //$$
    //$$     fun alignCenter(): TextWidget {
    //$$         return align(0.5f)
    //$$     }
    //$$
    //$$     fun alignRight(): TextWidget {
    //$$         return align(1.0f)
    //$$     }
    //$$
    //$$     override fun renderButton(matrixStack: MatrixStack?, i: Int, j: Int, f: Float) {
    //$$         val text = message
    //$$         val textRenderer = textRenderer
    //$$         val k = x + Math.round(horizontalAlignment * (getWidth() - textRenderer.getWidth(text)).toFloat())
    //$$         val l = y + (getHeight() - 9) / 2
    //$$         drawTextWithShadow(matrixStack, textRenderer, text, k, l, textColor)
    //$$     }
    //$$ }
    //#endif

    class TextListWidget(
        entries: List<TextListEntry>,
        client: MinecraftClient,
        width: Int,
        height: Int,
        top: Int,
        bottom: Int,
        itemHeight: Int
    ) : AlwaysSelectedEntryListWidget<TextListEntry>(
        client,
        width,
        height,
        top,
        bottom,
        itemHeight
    ) {
        init {
            addAllEntries(*entries.toTypedArray())
        }

        override fun addEntry(entry: TextListEntry) = super.addEntry(entry)
        fun addAllEntries(vararg entries: TextListEntry) = entries.forEach { addEntry(it) }

        fun setTopPosition(top: Int) {
            this.top = top
        }
    }

    class TextListEntry(
        val text: String
    ) : AlwaysSelectedEntryListWidget.Entry<TextListEntry>() {
        private val textRenderer: TextRenderer
            get() = MinecraftClient.getInstance().textRenderer

        val width: Int
            get() = textRenderer.getWidth(text)

        override fun render(
            stack: MatrixStack,
            index: Int,
            y: Int,
            x: Int,
            entryWidth: Int,
            entryHeight: Int,
            mouseX: Int,
            mouseY: Int,
            hovered: Boolean,
            tickDelta: Float
        ) {
            val textX = x + entryWidth / 2 - width / 2
            val textY = y + entryHeight / 2 - textRenderer.fontHeight / 2
            textRenderer.draw(stack, text, textX.toFloat(), textY.toFloat(), 0xFFFFFF)
        }

        override fun getNarration(): Text = Text.literal(text)
    }

    val text = """
            |I'm a 17-year-old developer who's been working on Minecraft mods for 4 years now. This has been a passion of mine for a long time.
            |Despite my push to do the best I can, I've been struggling to keep myself afloat financially.
            |I've been working hard to be able to support myself and move out of my problematic parents' house, but it hasn't been a very steady process.
            |My parents have caused severe damage against me my entire life.
            |Dealing with them has been a very big stressor. Living in a third-world, poverty-stricken and corrupt country has been a very big limiting factor in my ability to function and make good products.
            |I've been struggling to keep up with the needs of actively developing innovative and unique experiences without a stable internet connection or access to electricity.
            |Below are buttons you can click to support me by joining my Discord server, or donating to me through Ko-Fi or PayPal.
            |Thank you for reading this, and thank you for using my mods.
        """.trimMargin().replace('\n', ' ')
    lateinit var modListWidget: TextListWidget

    private var guiScale = -1

    override fun initScreen(width: Int, height: Int) {
        modListWidget = TextListWidget(
            gatherMods(),
            client!!,
            width,
            height,
            100,
            height - 80,
            textRenderer.fontHeight
        )
        addDrawable(modListWidget)

        // Discord
        addDrawableChild(createButton(
            // x
            width / 2 - 100,
            // y
            height - 70,
            // width
            200,
            // height
            20,
            // text
            Text.literal(ChatColor.BLUE + "Discord")
        ) {
            openLink("https://discord.gg/xmFBstnw9W")
        })

        // Ko-Fi
        addDrawableChild(createButton(
            // x
            width / 2 - 100,
            // y
            height - 50,
            // width
            100,
            // height
            20,
            // text
            Text.literal(ChatColor.GOLD + "Ko-Fi")
        ) {
            openLink("https://ko-fi.com/deftu")
        })

        // PayPal
        addDrawableChild(createButton(
            // x
            width / 2,
            // y
            height - 50,
            // width
            100,
            // height
            20,
            // text
            Text.literal(ChatColor.GREEN + "PayPal")
        ) {
            openLink("https://paypal.me/matthewtgm")
        })

        // Hide
        addDrawableChild(createButton(
            // x
            width / 2 - 100,
            // y
            height - 30,
            // width
            100,
            // height
            20,
            // text
            Text.literal(ChatColor.RED + ChatColor.BOLD.toString() + "Hide")
        ) {
            DeftuLibConfig.hideMainMenuButton = true
            DeftuLibConfig.markDirty()
            DeftuLibConfig.writeData()
            restorePreviousScreen()
        })

        // Close
        addDrawableChild(createButton(
            // x
            width / 2,
            // y
            height - 30,
            // width
            100,
            // height
            20,
            // text
            Text.literal("Close")
        ) {
            restorePreviousScreen()
        })
    }

    override fun onDrawScreen(universalStack: UMatrixStack, mouseX: Int, mouseY: Int, tickDelta: Float) {
        val stack = universalStack.toMC()
        super.onDrawScreen(universalStack, mouseX, mouseY, tickDelta)

        val titleText = "Sorry, I really need your help."
        val titleColor = ChatColor.RED
        val titleScale = 2.0f
        val titleX = width / 2f - textRenderer.getWidth(titleText)
        val titleY = 10f
        stack.push()
        stack.scale(titleScale, titleScale, 1f)
        textRenderer.draw(stack, "$titleColor$titleText", titleX / titleScale, titleY / titleScale, 0xFFFFFF)
        stack.pop()

        val subtitleY = titleY + textRenderer.fontHeight * titleScale + 10
        var currentSubtitleY = subtitleY
        textRenderer.wrapLines(Text.literal(text), width / 4 * 3).forEach { line ->
            val subtitleX = width / 2f - textRenderer.getWidth(line) / 2f
            textRenderer.draw(stack, line, subtitleX, currentSubtitleY, 0xFFFFFF)
            currentSubtitleY += textRenderer.fontHeight
        }

        val listTitleText = "Here's a list of my mods that you use:"
        val listTitleColor = ChatColor.GRAY
        val listTitleX = width / 2f - textRenderer.getWidth(listTitleText) / 2f
        val listTitleY = currentSubtitleY + textRenderer.fontHeight

        val topButtonBottom = height - 50
        if (listTitleY < topButtonBottom) {
            modListWidget.setTopPosition((listTitleY + textRenderer.fontHeight + 10).toInt())
            textRenderer.draw(stack, "$listTitleColor$listTitleText", listTitleX, listTitleY, 0xFFFFFF)
        } else remove(modListWidget)
    }

    private fun gatherMods(): List<TextListEntry> {
        val mods = mutableListOf<TextListEntry>()
        FabricLoader.getInstance().allMods.filter { container ->
            container.metadata.authors.any { person ->
                person.name.contains("Deftu", true)
            }
        }.forEach { container ->
            mods.add(TextListEntry(container.metadata.name))
        }

        if (mods.size == 0) mods.add(TextListEntry("No mods found! :("))
        return mods
    }

    private fun createButton(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        text: Text,
        onPress: (ButtonWidget) -> Unit
    ) =
        //#if MC<11903
        //$$ ButtonWidget(
        //$$     x,
        //$$     y,
        //$$     width,
        //$$     height,
        //$$     text,
        //$$     onPress
        //$$ )
        //#else
        ButtonWidget.builder(text, onPress)
            .dimensions(x, y, width, height)
            .build()
        //#endif

    private fun openLink(link: String) {
        Util.getOperatingSystem().open(link)
    }
}
