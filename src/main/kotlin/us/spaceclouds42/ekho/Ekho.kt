package us.spaceclouds42.ekho

import net.minecraft.text.*
import net.minecraft.util.Formatting

class EkhoBuilder(base: LiteralText, method: EkhoBuilder.() -> Unit) {
    private var root: MutableText = base
    private val siblings = mutableListOf<Text>() 
    private var inherit = true

    /**
     * Inserts a new line in the resulting [Text] object
     */
    val newLine
        get() = run { siblings.add(LiteralText("\n")); "\n" }

    init {
        method()
    }

    /**
     * Creates the final [Text] object from all the inputs
     *
     * @return finalized [Text] object
     */
    fun create(): Text {
        siblings.forEach { root.append(it) }
        return root
    }

    operator fun String.invoke(inheritStyle: Boolean = true, method: EkhoBuilder.() -> Unit = { }) {
        inherit = inheritStyle
        if (method == { }) {
            LiteralText(this).let { it.style = root.style; siblings.add(it) }
        } else {
            siblings.add(EkhoBuilder(
                LiteralText(this).let { if (inheritStyle) { it.style = root.style }; it },
                method
            ).create())
        }
    }

    fun style(method: StyleBuilder.() -> Unit) {
        root.style = StyleBuilder(root.style.let { if (inherit) { it } else { Style.EMPTY } }).apply(method).create()
    }
}

class StyleBuilder(private val parentStyle: Style) {
    private var color: TextColor? = null
    private var isBold: Boolean? = null
    private var isItalic: Boolean? = null
    private var isUnderlined: Boolean? = null
    private var isStrikethrough: Boolean? = null
    private var isObfuscated: Boolean? = null
    private var clickEvent: ClickEvent? = null
    private var hoverEvent: HoverEvent? = null

    val black
        get() = colorByCode(Formatting.BLACK)
    val darkBlue
        get() = colorByCode(Formatting.DARK_BLUE)
    val darkGreen
        get() = colorByCode(Formatting.DARK_GREEN)
    val darkAqua
        get() = colorByCode(Formatting.DARK_AQUA)
    val darkRed
        get() = colorByCode(Formatting.DARK_RED)
    val darkPurple
        get() = colorByCode(Formatting.DARK_PURPLE)
    val gold
        get() = colorByCode(Formatting.GOLD)
    val gray
        get() = colorByCode(Formatting.GRAY)
    val darkGray
        get() = colorByCode(Formatting.DARK_GRAY)
    val blue
        get() = colorByCode(Formatting.DARK_BLUE)
    val green
        get() = colorByCode(Formatting.GREEN)
    val aqua
        get() = colorByCode(Formatting.AQUA)
    val red
        get() = colorByCode(Formatting.RED)
    val lightPurple
        get() = colorByCode(Formatting.LIGHT_PURPLE)
    val yellow
        get() = colorByCode(Formatting.YELLOW)
    val white
        get() = colorByCode(Formatting.WHITE)

    private fun colorByCode(formatting: Formatting) {
        this.color = TextColor.fromFormatting(formatting)
    }

    /**
     * Allows for rgb color codes. For default Minecraft colors, just type the Minecraft color name (red, aqua, etc.)
     *
     * @param method be sure that this method returns an int like `0xFF00FF` for proper parsing
     */
    fun color(method: StyleBuilder.() -> Int) {
        this.color = TextColor.fromRgb(method()) ?: run {
            println("[Ekho] Error parsing color '${method()}' from rgb, defaulting to white")
            TextColor.fromFormatting(Formatting.WHITE)
        }
    }

    val bold
        get() = run { this.isBold = true }
    val noBold
        get() = run { this.isBold = false }

    val italics
        get() = run { this.isItalic = true }
    val noItalics
        get() = run { this.isItalic = false }

    val underline
        get() = run { this.isUnderlined = true }
    val noUnderline
        get() = run { this.isUnderlined = false }

    val strikethrough
        get() = run { this.isStrikethrough = true }
    val noStrikethrough
        get() = run { this.isStrikethrough = false }

    val obfuscated
        get() = run { this.isObfuscated = true }
    val noObfuscation
        get() = run { this.isObfuscated = false }

    fun clickEvent(method: StyleBuilder.() -> ClickEvent) {
        this.clickEvent = method()
    }

    fun hoverEvent(method: StyleBuilder.() -> HoverEvent) {
        this.hoverEvent = method()
    }

    fun create(): Style = Style(
        color ?: parentStyle.color,
        isBold ?: parentStyle.isBold,
        isItalic ?: parentStyle.isItalic,
        isUnderlined ?: parentStyle.isUnderlined,
        isStrikethrough ?: parentStyle.isStrikethrough,
        isObfuscated ?: parentStyle.isObfuscated,
        clickEvent ?: parentStyle.clickEvent,
        hoverEvent ?: parentStyle.hoverEvent,
        null,
        Style.DEFAULT_FONT_ID,
    )
}

enum class EkhoColors(private val formatting: Formatting) {

}

/**
 * Create a [Text] object using [EkhoBuilder]
 *
 * @param base the first part of the text, optional, defaults to empty string
 * @return a [Text] object
 */
fun ekho(base: String = "", method: EkhoBuilder.() -> Unit = { }): Text {
    return EkhoBuilder(LiteralText(base), method).create()
}
