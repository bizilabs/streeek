package com.bizilabs.streeek.lib.resources.fonts

import com.bizilabs.streeek.lib.resources.R

object SafiFonts {
    val Sans = FontSans()
    val Mono = FontMono()
    val Noto = FontNoto()

    class FontSans internal constructor() {
        val bold = R.font.sans_black
        val boldItalic = R.font.sans_black_italic
        val semiBold = R.font.sans_bold
        val semiBoldItalic = R.font.sans_bold_italic
        val medium = R.font.sans_medium
        val mediumItalic = R.font.sans_medium_italic
        val regular = R.font.sans_regular
        val regularItalic = R.font.sans_regular_italic
        val light = R.font.sans_light
        val lightItalic = R.font.sans_light_italic
        val thin = R.font.sans_thin
        val thinItalic = R.font.sans_thin_italic
    }

    class FontMono internal constructor() {
        val bold = R.font.mono_bold
        val boldItalic = R.font.mono_bold_italic
        val semiBold = R.font.mono_semi_bold
        val semiBoldItalic = R.font.mono_semi_bold_italic
        val medium = R.font.mono_medium
        val mediumItalic = R.font.mono_medium_italic
        val regular = R.font.mono_regular
        val regularItalic = R.font.mono_regular_italic
        val light = R.font.mono_light
        val lightItalic = R.font.mono_light_italic
        val thin = R.font.mono_thin
        val thinItalic = R.font.mono_thin_italic
    }

    class FontNoto internal constructor() {
        val bold = R.font.noto_bold
        val boldItalic = R.font.noto_bold_italic
        val semiBold = R.font.noto_semi_bold
        val semiBoldItalic = R.font.noto_semi_bold_italic
        val medium = R.font.noto_medium
        val mediumItalic = R.font.noto_medium_italic
        val regular = R.font.noto_regular
        val regularItalic = R.font.noto_regular_italic
        val light = R.font.noto_light
        val lightItalic = R.font.noto_light_italic
        val thin = R.font.noto_thin
        val thinItalic = R.font.noto_thin_italic
    }
}
