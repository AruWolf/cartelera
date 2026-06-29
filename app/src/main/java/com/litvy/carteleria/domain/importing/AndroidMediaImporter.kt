package com.litvy.carteleria.domain.importing

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import java.io.File

class AndroidMediaImporter(
    private val context: Context
) {

    private val supportedExtensions = setOf("jpg", "jpeg", "png", "webp", "mp4", "webm", "mkv")

    fun importUris(uris: List<Uri>, targetFolder: File): Int {
        val destination = targetFolder.apply { mkdirs() }
        return uris.count { uri -> copyUri(uri, destination) }
    }

    private fun copyUri(uri: Uri, destination: File): Boolean {
        val displayName = displayName(uri) ?: fallbackName(uri) ?: return false
        val extension = displayName.substringAfterLast('.', missingDelimiterValue = "")
            .lowercase()
            .ifBlank { extensionFromMime(uri).orEmpty() }

        if (extension !in supportedExtensions) return false

        val sanitizedName = sanitizeFileName(
            if (displayName.contains('.')) displayName else "$displayName.$extension"
        )
        val target = uniqueFile(destination, sanitizedName)

        return runCatching {
            context.contentResolver.openInputStream(uri)?.use { input ->
                target.outputStream().use { output -> input.copyTo(output) }
            } ?: return false
            true
        }.getOrDefault(false)
    }

    private fun displayName(uri: Uri): String? {
        return context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor ->
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0 && cursor.moveToFirst()) cursor.getString(index) else null
            }
    }

    private fun fallbackName(uri: Uri): String? {
        val extension = extensionFromMime(uri) ?: return null
        return "media-${System.currentTimeMillis()}.$extension"
    }

    private fun extensionFromMime(uri: Uri): String? {
        val mime = context.contentResolver.getType(uri) ?: return null
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mime)?.lowercase()
    }

    private fun sanitizeFileName(name: String): String {
        return name.replace(Regex("[\\\\/:*?\"<>|]"), "_").trim().ifBlank { "media" }
    }

    private fun uniqueFile(folder: File, fileName: String): File {
        val base = fileName.substringBeforeLast('.', fileName)
        val extension = fileName.substringAfterLast('.', missingDelimiterValue = "")
        var candidate = File(folder, fileName)
        var counter = 1

        while (candidate.exists()) {
            val suffix = if (extension.isBlank()) "" else ".$extension"
            candidate = File(folder, "$base($counter)$suffix")
            counter++
        }

        return candidate
    }
}
