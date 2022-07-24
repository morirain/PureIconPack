@file:Suppress("unused", "RemoveExplicitTypeArguments")

object Libs {
    // Kuper
    const val kuper = "dev.jahir:Kuper:${Versions.kuper}@aar"

    // Adaptive Icons
    private const val adaptiveIcons =
        "com.github.sarsamurmu:AdaptiveIconBitmap:${Versions.adaptiveIcons}"

    val dependencies = arrayOf<String>(adaptiveIcons)

    // Kotlin
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"

    // Blueprint
    const val blueprint = "dev.jahir:Blueprint:${Versions.blueprint}@aar"

    // OneSignal
    const val oneSignal = "com.onesignal:OneSignal:${Versions.oneSignal}"
}