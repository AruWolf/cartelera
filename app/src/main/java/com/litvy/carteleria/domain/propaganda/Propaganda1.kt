package com.litvy.carteleria.domain.propaganda

import com.litvy.carteleria.slides.AssetSlideProvider
import com.litvy.carteleria.slides.Slide

class Propaganda1(
    private val slideProvider: AssetSlideProvider
) : Propaganda {
    override fun slides(): List<Slide> {
        return slideProvider.loadFrom("promos")
    }
}
