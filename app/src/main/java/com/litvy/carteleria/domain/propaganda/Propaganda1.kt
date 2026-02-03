package com.litvy.carteleria.domain.propaganda

import androidx.compose.runtime.Composable
import com.litvy.carteleria.R
import com.litvy.carteleria.slides.ImageSlide
import com.litvy.carteleria.slides.Slide
import com.litvy.carteleria.slides.slideChargers


class Propaganda1 : Propaganda {
    override fun slides(): List<@Composable () -> Unit> {
        return listOf(
            {slideChargers()}
        )
    }

}