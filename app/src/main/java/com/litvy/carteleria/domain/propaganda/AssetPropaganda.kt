package com.litvy.carteleria.domain.propaganda

import com.litvy.carteleria.slides.AssetSlideProvider
import com.litvy.carteleria.slides.Slide

class AssetPropaganda(
    private val slideProvider: AssetSlideProvider,
    private val folder: String
): Propaganda {
    override fun slides(): List<Slide> =
        slideProvider.loadFrom(folder)
}