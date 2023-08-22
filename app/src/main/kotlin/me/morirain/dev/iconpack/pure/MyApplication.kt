package me.morirain.dev.iconpack.pure

import android.content.Context
import dev.jahir.frames.ui.FramesApplication

// TODO: Remove comment marks to enable
// import com.onesignal.OneSignal
// import com.onesignal.OSNotificationReceivedEvent
// import dev.jahir.frames.extensions.context.preferences

class MyApplication : FramesApplication(BuildConfig.ONESIGNAL_APP_ID) {
    override fun onCreate() {
        super.onCreate()
        MyApplication.appContext = applicationContext
        // TODO: Remove comment marks to enable
        /*
        OneSignal.initWithContext(this);
        OneSignal.setAppId(BuildConfig.ONESIGNAL_APP_ID);
        OneSignal.setNotificationWillShowInForegroundHandler { notificationReceivedEvent: OSNotificationReceivedEvent ->
            notificationReceivedEvent.complete(
                if (preferences.notificationsEnabled)
                    notificationReceivedEvent.notification
                else null
            )
        }
        OneSignal.unsubscribeWhenNotificationsAreDisabled(true)
        OneSignal.pauseInAppMessages(true)
        OneSignal.setLocationShared(false)
         */
    }
    companion object {

        lateinit  var appContext: Context

    }
}
