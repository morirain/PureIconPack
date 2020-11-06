package me.morirain.dev.iconpack.pure

import android.content.Context
import dev.jahir.frames.ui.FramesApplication

// TODO: Remove comment marks to enable
// import com.onesignal.OneSignal

class MyApplication : FramesApplication() {
    override fun onCreate() {
        super.onCreate()
        MyApplication.appContext = applicationContext
        // TODO: Remove comment marks to enable
        /*
        OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init()
         */
    }
    companion object {

        lateinit  var appContext: Context

    }
}