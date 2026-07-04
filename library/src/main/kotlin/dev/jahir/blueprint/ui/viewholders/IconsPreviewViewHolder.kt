package dev.jahir.blueprint.ui.viewholders

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.afollestad.sectionedrecyclerview.SectionedViewHolder
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.listeners.HomeItemsListener
import dev.jahir.blueprint.data.models.Icon
import dev.jahir.blueprint.ui.widgets.IconsPreviewRecyclerView
import dev.jahir.frames.extensions.context.boolean
import dev.jahir.frames.extensions.context.currentVersionName
import dev.jahir.frames.extensions.views.findView
import dev.jahir.frames.extensions.views.visibleIf

class IconsPreviewViewHolder(itemView: View) : SectionedViewHolder(itemView) {
    private val wallpaperView: AppCompatImageView? by itemView.findView(R.id.wallpaper)
    private val iconsGrid: IconsPreviewRecyclerView? by itemView.findView(R.id.icons_preview_grid)
    private val versionView: TextView? by itemView.findView(R.id.icons_preview_version)

    fun bind(icons: List<Icon>, wallpaper: Drawable? = null, listener: HomeItemsListener? = null) {
        wallpaperView?.setImageDrawable(wallpaper)
        iconsGrid?.setIcons(icons)
        versionView?.text = itemView.context.currentVersionName
        versionView?.visibleIf(itemView.context.boolean(R.bool.show_home_preview_version, true))
        itemView.setOnClickListener { listener?.onIconsPreviewClicked() }
    }
}
