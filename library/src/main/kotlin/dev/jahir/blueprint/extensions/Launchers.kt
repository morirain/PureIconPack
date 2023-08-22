package dev.jahir.blueprint.extensions

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import dev.jahir.blueprint.R
import dev.jahir.blueprint.data.models.Launcher
import dev.jahir.frames.extensions.context.getAppName
import dev.jahir.frames.extensions.context.openLink
import dev.jahir.frames.extensions.context.string
import dev.jahir.frames.extensions.fragments.mdDialog
import dev.jahir.frames.extensions.fragments.message
import dev.jahir.frames.extensions.fragments.negativeButton
import dev.jahir.frames.extensions.fragments.positiveButton
import dev.jahir.frames.extensions.fragments.title
import dev.jahir.frames.ui.activities.base.BaseLicenseCheckerActivity.Companion.PLAY_STORE_LINK_PREFIX
import dev.jahir.kuper.extensions.isAppInstalled

@Suppress("REDUNDANT_ELSE_IN_WHEN")
fun Context.executeLauncherIntent(launcher: Launcher?) {
    launcher ?: return
    if (!launcher.isActuallySupported) {
        executeIconPacksNotSupportedIntent()
        return
    }
    when (launcher) {
        Launcher.ACTION -> executeActionLauncherIntent()
        Launcher.ADW -> executeAdwLauncherIntent()
        Launcher.ADW_EX -> executeAdwEXLauncherIntent()
        Launcher.APEX -> executeApexLauncherIntent()
        Launcher.GO -> executeGoLauncherIntent()
        Launcher.HOLO -> executeHoloLauncherIntent()
        Launcher.HOLO_ICS -> executeHoloLauncherICSIntent()
        Launcher.LG_HOME -> executeLgHomeLauncherIntent()
        Launcher.LAWNCHAIR -> executeLawnchairIntent()
        Launcher.LINEAGE_OS -> executeLineageOSThemeEngineIntent()
        Launcher.LUCID -> executeLucidLauncherIntent()
        Launcher.MOTO -> executeMotoLauncherIntent()
        Launcher.NIAGARA -> executeNiagaraLauncherIntent()
        Launcher.NOVA -> executeNovaLauncherIntent()
        Launcher.ONEPLUS -> executeOnePlusLauncherIntent()
        Launcher.POSIDON -> executePosidonLauncherIntent()
        Launcher.SMART -> executeSmartLauncherIntent()
        Launcher.SMART_PRO -> executeSmartLauncherProIntent()
        Launcher.SOLO -> executeSoloLauncherIntent()
        Launcher.SQUARE -> executeSquareHomeIntent()
        Launcher.TSF -> executeTsfLauncherIntent()
        else -> showLauncherApplyError()
    }
}

private fun Context.executeIconPacksNotSupportedIntent() {
    try {
        mdDialog {
            title(R.string.no_compatible_launcher_title)
            message(R.string.no_compatible_launcher_content)
            positiveButton(android.R.string.ok) {
                openLink(PLAY_STORE_LINK_PREFIX + "com.momocode.shortcuts")
            }
            negativeButton(android.R.string.cancel)
        }.show()
    } catch (_: Exception) {
    }
}

internal fun Context.showLauncherNotInstalledDialog(launcher: Launcher) {
    try {
        mdDialog {
            title(launcher.appName)
            message(getString(R.string.lni_content, launcher.appName))
            positiveButton(android.R.string.ok) {
                openLink(PLAY_STORE_LINK_PREFIX + launcher.packageNames[0])
            }
            negativeButton(android.R.string.cancel)
        }.show()
    } catch (_: Exception) {
    }
}

private fun Context.showLauncherApplyError(
    launcher: Launcher? = null,
    customContent: String? = null
) {
    try {
        mdDialog {
            title(dev.jahir.frames.R.string.error)
            message(
                customContent ?: launcher?.cleanAppName?.let {
                    string(R.string.direct_apply_not_supported, it, getAppName())
                } ?: getString(R.string.coming_soon))
            positiveButton(android.R.string.ok)
        }.show()
    } catch (_: Exception) {
    }
}

private fun Context.attemptApply(
    launcher: Launcher?,
    customContent: String? = null,
    intent: (() -> Intent?)? = null
) {
    try {
        intent?.invoke()?.let { startActivity(it) }
            ?: showLauncherApplyError(launcher, customContent)
    } catch (e: Exception) {
        showLauncherApplyError(launcher, customContent)
    }
}

private fun Context.executeActionLauncherIntent() {
    attemptApply(Launcher.ACTION) {
        packageManager
            .getLaunchIntentForPackage("com.actionlauncher.playstore")?.apply {
                putExtra("apply_icon_pack", packageName)
            }
    }
}

private fun Context.executeAdwLauncherIntent() {
    attemptApply(Launcher.ADW) {
        Intent("org.adw.launcher.SET_THEME").apply {
            putExtra("org.adw.launcher.theme.NAME", packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}

private fun Context.executeAdwEXLauncherIntent() {
    attemptApply(Launcher.ADW_EX) {
        Intent("org.adwfreak.launcher.SET_THEME").apply {
            putExtra("org.adwfreak.launcher.theme.NAME", packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}

private fun Context.executeApexLauncherIntent() {
    attemptApply(Launcher.APEX) {
        Intent("com.anddoes.launcher.SET_THEME").apply {
            putExtra("com.anddoes.launcher.THEME_PACKAGE_NAME", packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}

private fun Context.executeGoLauncherIntent() {
    attemptApply(Launcher.GO) {
        packageManager.getLaunchIntentForPackage("com.gau.go.launcherex").also {
            val go = Intent("com.gau.go.launcherex.MyThemes.mythemeaction")
            go.putExtra("type", 1)
            go.putExtra("pkgname", packageName)
            sendBroadcast(go)
        }
    }
}

private fun Context.executeHoloLauncherIntent() {
    attemptApply(Launcher.HOLO) {
        Intent(Intent.ACTION_MAIN).apply {
            component = ComponentName("com.mobint.hololauncher", "com.mobint.hololauncher.Settings")
        }
    }
}

private fun Context.executeHoloLauncherICSIntent() {
    attemptApply(Launcher.HOLO_ICS) {
        Intent(Intent.ACTION_MAIN).apply {
            component = ComponentName(
                "com.mobint.hololauncher.hd", "com.mobint.hololauncher.SettingsActivity"
            )
        }
    }
}

private fun Context.executeLgHomeLauncherIntent() {
    attemptApply(Launcher.LG_HOME) {
        Intent(Intent.ACTION_MAIN).apply {
            component = ComponentName(
                "com.lge.launcher2",
                "com.lge.launcher2.homesettings.HomeSettingsPrefActivity"
            )
        }
    }
}

private fun Context.executeLawnchairIntent() {
    attemptApply(Launcher.LAWNCHAIR) {
        Intent("ch.deletescape.lawnchair.APPLY_ICONS", null).apply {
            putExtra("packageName", packageName)
        }
    }
}

private fun Context.executeLineageOSThemeEngineIntent() {
    var themesAppInstalled = isAppInstalled("org.cyanogenmod.theme.chooser") ||
            isAppInstalled("org.cyanogenmod.theme.chooser2") ||
            isAppInstalled("com.cyngn.theme.chooser")

    attemptApply(
        Launcher.LINEAGE_OS,
        if (themesAppInstalled) getString(R.string.impossible_open_themes)
        else getString(R.string.themes_app_not_installed)
    ) {
        Intent("android.action.MAIN").apply {
            when {
                isAppInstalled("org.cyanogenmod.theme.chooser") -> {
                    component = ComponentName(
                        "org.cyanogenmod.theme.chooser",
                        "org.cyanogenmod.theme.chooser.ChooserActivity"
                    )
                }

                isAppInstalled("org.cyanogenmod.theme.chooser2") -> {
                    component = ComponentName(
                        "org.cyanogenmod.theme.chooser2",
                        "org.cyanogenmod.theme.chooser2.ChooserActivity"
                    )
                }

                isAppInstalled("com.cyngn.theme.chooser") -> {
                    component = ComponentName(
                        "com.cyngn.theme.chooser",
                        "com.cyngn.theme.chooser.ChooserActivity"
                    )
                }

                else -> themesAppInstalled = false
            }
            if (themesAppInstalled) putExtra("pkgName", packageName)
        }
    }
}

private fun Context.executeLucidLauncherIntent() {
    attemptApply(Launcher.LUCID) {
        Intent("com.powerpoint45.action.APPLY_THEME", null).apply {
            putExtra("icontheme", packageName)
        }
    }
}

private fun Context.executeNiagaraLauncherIntent() {
    attemptApply(Launcher.NIAGARA) {
        Intent("bitpit.launcher.APPLY_ICONS").apply {
            `package` = "bitpit.launcher"
            putExtra("packageName", packageName)
        }
    }
}

private fun Context.executeNovaLauncherIntent() {
    attemptApply(Launcher.NOVA) {
        Intent("com.teslacoilsw.launcher.APPLY_ICON_THEME").apply {
            `package` = "com.teslacoilsw.launcher"
            putExtra("com.teslacoilsw.launcher.extra.ICON_THEME_TYPE", "GO")
            putExtra("com.teslacoilsw.launcher.extra.ICON_THEME_PACKAGE", packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}

private fun Context.executeOnePlusLauncherIntent() {
    attemptApply(Launcher.ONEPLUS) {
        Intent().apply {
            component = ComponentName(
                "net.oneplus.launcher",
                "net.oneplus.launcher.IconPackSelectorActivity"
            )
        }
    }
}

private fun Context.executePosidonLauncherIntent() {
    attemptApply(Launcher.POSIDON) {
        Intent(Intent.ACTION_MAIN).apply {
            component = ComponentName("posidon.launcher", "posidon.launcher.external.ApplyIcons")
            putExtra("iconpack", packageName)
        }
    }
}

private fun Context.executeSmartLauncherIntent() {
    attemptApply(Launcher.SMART) {
        Intent("ginlemon.smartlauncher.setGSLTHEME").apply {
            putExtra("package", packageName)
        }
    }
}

private fun Context.executeSmartLauncherProIntent() {
    attemptApply(Launcher.SMART_PRO) {
        Intent("ginlemon.smartlauncher.setGSLTHEME").apply {
            putExtra("package", packageName)
        }
    }
}

private fun Context.executeSoloLauncherIntent() {
    attemptApply(Launcher.SOLO) {
        packageManager.getLaunchIntentForPackage("home.solo.launcher.free").also {
            val solo = Intent("home.solo.launcher.free.APPLY_THEME")
            solo.putExtra("EXTRA_PACKAGENAME", packageName)
            solo.putExtra("EXTRA_THEMENAME", getAppName())
            sendBroadcast(solo)
        }
    }
}

private fun Context.executeSquareHomeIntent() {
    attemptApply(Launcher.SQUARE) {
        Intent("com.ss.squarehome2.ACTION_APPLY_ICONPACK").apply {
            component =
                ComponentName.unflattenFromString("com.ss.squarehome2/.ApplyThemeActivity")
            putExtra("com.ss.squarehome2.EXTRA_ICONPACK", packageName)
        }
    }
}

private fun Context.executeTsfLauncherIntent() {
    attemptApply(Launcher.TSF) {
        packageManager.getLaunchIntentForPackage("com.tsf.shell").also {
            val tsf = Intent("android.action.MAIN")
            tsf.component = ComponentName("com.tsf.shell", "com.tsf.shelShellActivity")
            sendBroadcast(tsf)
        }
    }
}

private fun Context.executeMotoLauncherIntent() {
    attemptApply(Launcher.MOTO) {
        Intent().apply {
            component = ComponentName(
                "com.motorola.personalize",
                "com.motorola.personalize.app.IconPacksActivity"
            )
            putExtra("package", packageName)
        }
    }
}

internal val Context.defaultLauncher: Launcher?
    get() = try {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        val resolveInfo =
            packageManager.resolveActivityCompat(intent, PackageManager.MATCH_DEFAULT_ONLY)
        val launcherPackage = resolveInfo?.activityInfo?.packageName
        Launcher.getSupportedLaunchers(this)
            .filter { it.first.isActuallySupported }
            .firstOrNull { it.first.hasPackage(launcherPackage.orEmpty()) }?.first
    } catch (e: Exception) {
        null
    }
