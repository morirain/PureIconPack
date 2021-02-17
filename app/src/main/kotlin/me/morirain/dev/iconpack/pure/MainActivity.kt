package me.morirain.dev.iconpack.pure

import android.os.Build
import android.os.Bundle
import android.util.Log
import com.github.javiersantos.piracychecker.PiracyChecker
import com.google.gson.GsonBuilder
import dev.jahir.blueprint.data.requests.ArcticService
import dev.jahir.blueprint.data.requests.SendIconRequest
import dev.jahir.blueprint.ui.activities.BottomNavigationBlueprintActivity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.*
import kotlin.math.log
import kotlin.reflect.jvm.javaField

/**
 * You can choose between:
 * - DrawerBlueprintActivity
 * - BottomNavigationBlueprintActivity
 */
class MainActivity : BottomNavigationBlueprintActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (BuildConfig.DEBUG) {
            val request = lazy {
                Retrofit.Builder()
                    .baseUrl("https://morirain-3gv3co56b4babf1b-1256096275.ap-shanghai.app.tcloudbase.com/IconReceive/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                    .build().create(ArcticService::class.java)
            }
            val c = SendIconRequest::class.java.getDeclaredField("INSTANCE").get(null)
            c.javaClass.getDeclaredField("service\$delegate").let { field ->
                field.isAccessible = true
                field.set(SendIconRequest, request)
                return@let field.get(SendIconRequest)
            }
        }
        super.onCreate(savedInstanceState)
    }

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
    override fun getLicKey(): String? =
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
}