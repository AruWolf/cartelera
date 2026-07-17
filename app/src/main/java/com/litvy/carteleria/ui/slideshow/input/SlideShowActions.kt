package com.litvy.carteleria.ui.slideshow.input

import com.litvy.carteleria.ui.slideshow.SlideShowViewModel

class SlideShowActions(
    private val viewModel: SlideShowViewModel,
    private val playFolderByShortcut: (Int) -> Boolean
) {
    fun nextSlide() {
        viewModel.nextSlide()
    }

    fun previousSlide() {
        viewModel.previousSlide()
    }

    fun pausePlayback() {
        viewModel.pausePlayback()
    }

    fun resumePlayback() {
        viewModel.resumePlayback()
    }

    fun togglePlayback() {
        viewModel.togglePause()
    }

    fun openMenu() {
        viewModel.openMenu()
    }

    fun closeMenu() {
        viewModel.closeMenu()
    }

    fun playShortcut(number: Int): Boolean = playFolderByShortcut(number)
}
