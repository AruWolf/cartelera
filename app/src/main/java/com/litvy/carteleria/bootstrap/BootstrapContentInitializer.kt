package com.litvy.carteleria.bootstrap

import android.content.Context
import com.litvy.carteleria.content.ContentStorage
import java.io.File
/*
class BootstrapContentInitializer {

    companion object {
        private const val PREFS_NAME = "bootstrap_content"
        private const val BOOTSTRAP_COMPLETED_KEY = "bootstrap_completed"
    }

    private data class DemoAsset(
        val assetPath: String,
        val targetName: String
    )

    private val demoAssets = listOf(
        DemoAsset(
            assetPath = "content/productos/",
            targetName = "demo_slide_1.png"
        ),
        DemoAsset(
            assetPath = "content/productos/",
            targetName = "demo_slide_2.png"
        ),
        DemoAsset(
            assetPath = "content/productos/",
            targetName = "demo_slide_3.mp4"
        )
    )

    fun initializeIfNeeded(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        if (prefs.getBoolean(BOOTSTRAP_COMPLETED_KEY, false)) {
            return
        }

        val targetFolder = ContentStorage.defaultFolder(context).apply {
            if (!exists()) mkdirs()
        }

        demoAssets.forEach { demoAsset ->
            copyAssetToFile(
                context = context,
                assetPath = demoAsset.assetPath,
                targetFile = File(targetFolder, demoAsset.targetName)
            )
        }

        prefs.edit()
            .putBoolean(BOOTSTRAP_COMPLETED_KEY, true)
            .apply()
    }

    private fun copyAssetToFile(
        context: Context,
        assetPath: String,
        targetFile: File
    ) {
        context.assets.open(assetPath).use { input ->
            targetFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }
}
*/