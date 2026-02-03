package com.litvy.carteleria

import androidx.compose.runtime.Composable
import com.litvy.carteleria.domain.propaganda.Propaganda
import com.litvy.carteleria.domain.propaganda.Propaganda1
import com.litvy.carteleria.slides.Slide
import com.litvy.carteleria.slides.slideChargers
import com.litvy.carteleria.slides.slideHeadphones
import com.litvy.carteleria.util.evokeSlide

class GenerateSlide(private val propaganda: Propaganda, /*Animation*/ private val TimeLength: Int){
    fun generate(): List<@Composable () -> Unit>{
        return propaganda.slides()
    }
}