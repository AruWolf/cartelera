package com.litvy.carteleria.data.external

import android.content.Context
import com.litvy.carteleria.domain.external.ExternalFolder

class FolderShortcutManager(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun applyPersistedShortcuts(folders: List<ExternalFolder>): List<ExternalFolder> {
        val folderPaths = folders.map { it.path }.toSet()
        val editor = prefs.edit()
        var changed = false

        prefs.all.keys
            .filter { it !in folderPaths }
            .forEach {
                editor.remove(it)
                changed = true
            }

        val used = prefs.all
            .filterKeys { it in folderPaths }
            .values
            .mapNotNull { it as? Int }
            .filter { it in VALID_SHORTCUTS }
            .toMutableSet()

        val foldersWithShortcuts = folders.map { folder ->
            val hasPersistedShortcut = prefs.contains(folder.path)
            val persisted = prefs.getInt(folder.path, NO_SHORTCUT)

            if (hasPersistedShortcut && persisted in VALID_SHORTCUTS) {
                folder.copy(shortcutNumber = persisted)
            } else if (hasPersistedShortcut) {
                folder.copy(shortcutNumber = null)
            } else {
                val next = VALID_SHORTCUTS.firstOrNull { it !in used }
                if (next != null) {
                    used += next
                    editor.putInt(folder.path, next)
                    changed = true
                }
                folder.copy(shortcutNumber = next)
            }
        }

        if (changed) editor.apply()
        return foldersWithShortcuts
    }

    fun setShortcut(folderPath: String, shortcutNumber: Int?) {
        val editor = prefs.edit()

        prefs.all.forEach { (path, value) ->
            if (path != folderPath && value == shortcutNumber) {
                editor.putInt(path, NO_SHORTCUT)
            }
        }

        if (shortcutNumber == null) {
            editor.putInt(folderPath, NO_SHORTCUT)
        } else {
            editor.putInt(folderPath, shortcutNumber)
        }

        editor.apply()
    }

    fun clearShortcut(folderPath: String) {
        prefs.edit().remove(folderPath).apply()
    }

    companion object {
        private const val PREFS_NAME = "folder_numeric_shortcuts"
        private const val NO_SHORTCUT = -1
        val VALID_SHORTCUTS = 0..9
    }
}
