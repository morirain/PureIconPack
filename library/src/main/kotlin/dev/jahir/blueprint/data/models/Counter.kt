package dev.jahir.blueprint.data.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.jahir.blueprint.R

sealed class Counter(@StringRes val title: Int, @DrawableRes val icon: Int, open val count: Int = 0)

data class IconsCounter(override val count: Int = 0) :
    Counter(R.string.icons, R.drawable.ic_icons, count)

data class WallpapersCounter(override val count: Int = 0) :
    Counter(dev.jahir.frames.R.string.wallpapers, dev.jahir.frames.R.drawable.ic_wallpapers, count)

data class KustomCounter(override val count: Int = 0) :
    Counter(dev.jahir.kuper.R.string.templates, dev.jahir.kuper.R.drawable.ic_kustom, count)
