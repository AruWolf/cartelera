package com.litvy.carteleria.ui.slideshow.exit

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.litvy.carteleria.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ExitHandler(

    private val context: Context,

    private val activity: Activity?,

    private val scope: CoroutineScope

) {

    private var backPressedOnce = false

    fun handleBack(): Boolean {

        if (!backPressedOnce) {

            backPressedOnce = true

            Toast.makeText(
                context,
                context.getString(R.string.press_again_to_exit),
                Toast.LENGTH_SHORT
            ).show()

            scope.launch {

                delay(2000)

                backPressedOnce = false

            }

            return true
        }

        activity?.finishAffinity()

        return true
    }

}