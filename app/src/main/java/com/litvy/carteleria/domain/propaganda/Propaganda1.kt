package com.litvy.carteleria.domain.propaganda

import com.litvy.carteleria.slides.ChargersSlide
import com.litvy.carteleria.slides.HeadphonesSlide
import com.litvy.carteleria.slides.Slide

class Propaganda1 : Propaganda {
    override fun slides(): List<Slide> {
        return listOf(
            ChargersSlide(durationMs = 2500, transitionKey = "fade"),
            HeadphonesSlide(durationMs = 2500, transitionKey = "scale")
        )
    }
}
