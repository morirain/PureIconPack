package me.morirain.dev.iconpack.pure

import android.os.Build
import com.github.javiersantos.piracychecker.PiracyChecker
import dev.jahir.blueprint.ui.activities.BottomNavigationBlueprintActivity
import java.util.*

/**
 * You can choose between:
 * - DrawerBlueprintActivity
 * - BottomNavigationBlueprintActivity
 */
class MainActivity : BottomNavigationBlueprintActivity() {

    /**
     * These things here have the default values. You can delete the ones you don't want to change
     * and/or modify the ones you want to.
     */
    override val billingEnabled = true

    override fun amazonInstallsEnabled(): Boolean = true
    override fun checkLPF(): Boolean = false
    override fun checkStores(): Boolean = false
    override val isDebug: Boolean = BuildConfig.DEBUG

    /**
     * This is your app's license key. Get yours on Google Play Dev Console.
     * Default one isn't valid and could cause issues in your app.
     */
    override fun getLicKey(): String =
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAourVcAxhBy+MOy+zDZM01ntfDrJ5A3cHZqoEcJiwz2jRrmfByDBRPjKUCkJoEACknJU2WxyPFesI9ncLucL+nrcWoNZwK/132ltpOngE4Muqi9z4GVmFLqcj8I7ABCg08eyHf3pTrbOX8l+w1EynoLGZ1oPIRHc3mQq04bGyQL43C5R1/Holq4A1tYScvy/5E2HFf9bGFOc9YOVSdgmOJ27gfXptDGtfaznJLUqK91vCsx4TO8fvlLXwZtlT8w50hyK0XO04NyFEae+Z9qxuL4Zvp5FVkyfuB1uPggWvBtoiUxjtYNVfj8TxUu7k7zHqkR7Q9EHys7p/GqjQCr9FVwIDAQAB"

    /**
     * This is the license checker code. Feel free to create your own implementation or
     * leave it as it is.
     * Anyways, keep the 'destroyChecker()' as the very first line of this code block
     * Return null to disable license check
     */
    override fun getLicenseChecker(): PiracyChecker? {
        destroyChecker() // Important
        val l: Locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            MyApplication.appContext.resources.configuration.locales.get(0)
        else
            MyApplication.appContext.resources.configuration.locale
        if (l.country == "CN" || l.country == "TW" || l.country == "HK")
            return null
        return if (BuildConfig.DEBUG)
            return null
        else
            return null
        //super.getLicenseChecker()
    }

    override fun defaultTheme(): Int = R.style.MyApp_Default
    override fun amoledTheme(): Int = R.style.MyApp_Default_Amoled

    override fun defaultMaterialYouTheme(): Int = R.style.MyApp_Default_MaterialYou
    override fun amoledMaterialYouTheme(): Int = R.style.MyApp_Default_Amoled_MaterialYou
}