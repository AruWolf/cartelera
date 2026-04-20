package com.litvy.carteleria.data.external

import android.content.Context
import java.io.File

class HiddenFileManager(context: Context) {

    private val hiddenFile = File(context.filesDir, "hidden_files.txt")

    private var hiddenPaths: MutableSet<String> = load()

    fun isHidden(path: String): Boolean {
        return hiddenPaths.contains(path)
    }

    fun hide(path: String) {
        if (hiddenPaths.add(path)) save()
    }

    fun show(path: String) {
        if (hiddenPaths.remove(path)) save()
    }

    private fun save() {
        hiddenFile.writeText(hiddenPaths.joinToString("\n"))
    }

    private fun load(): MutableSet<String> {
        if (!hiddenFile.exists()) return mutableSetOf()
        return hiddenFile.readLines().toMutableSet()
    }
}