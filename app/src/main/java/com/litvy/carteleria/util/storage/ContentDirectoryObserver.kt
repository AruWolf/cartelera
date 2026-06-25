package com.litvy.carteleria.util.storage

import android.os.FileObserver
import java.io.File

class ContentDirectoryObserver(
    private val root: File,
    private val onChanged: () -> Unit
) {

    private val observers = mutableMapOf<String, FileObserver>()

    fun start() {
        root.mkdirs()
        refreshObservers()
    }

    fun stop() {
        observers.values.forEach { it.stopWatching() }
        observers.clear()
    }

    private fun refreshObservers() {
        val directories = root.walkTopDown()
            .filter { it.isDirectory }
            .map { it.absolutePath }
            .toSet()

        observers.keys.minus(directories).forEach { path ->
            observers.remove(path)?.stopWatching()
        }

        directories.minus(observers.keys).forEach { path ->
            val observer = object : FileObserver(path, EVENTS) {
                override fun onEvent(event: Int, changedPath: String?) {
                    val cleanEvent = event and FileObserver.ALL_EVENTS
                    if (cleanEvent in RELEVANT_EVENTS) {
                        refreshObservers()
                        onChanged()
                    }
                }
            }
            observer.startWatching()
            observers[path] = observer
        }
    }

    private companion object {
        const val EVENTS = FileObserver.CREATE or FileObserver.DELETE or FileObserver.MOVED_FROM or FileObserver.MOVED_TO or FileObserver.CLOSE_WRITE or FileObserver.DELETE_SELF or FileObserver.MOVE_SELF
        val RELEVANT_EVENTS = setOf(FileObserver.CREATE, FileObserver.DELETE, FileObserver.MOVED_FROM, FileObserver.MOVED_TO, FileObserver.CLOSE_WRITE, FileObserver.DELETE_SELF, FileObserver.MOVE_SELF)
    }
}
