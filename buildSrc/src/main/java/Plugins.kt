@file:Suppress("unused")

object Plugins {
    // Android
    const val android = "com.android.tools.build:gradle:${Versions.gradle}"

    // Kotlin
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"

    // Sonatype
    const val sonatype =
        "io.github.gradle-nexus:publish-plugin:${Versions.sonatype}"

    // KSP
    const val ksp =
        "com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:${Versions.ksp}"
}
