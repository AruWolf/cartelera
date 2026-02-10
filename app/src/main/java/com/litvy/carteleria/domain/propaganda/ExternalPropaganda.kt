package com.litvy.carteleria.domain.propaganda

import com.litvy.carteleria.slides.ExternalSlideProvider
import com.litvy.carteleria.slides.Slide
import java.io.File

class ExternalPropaganda(
    private val provider: ExternalSlideProvider,
    private val folder: File
): Propaganda{
    override fun slides(): List<Slide> =
        provider.loadFrom(folder)
}