package com.litvy.carteleria

import android.app.Application
import com.litvy.carteleria.bootstrap.BootstrapContentInitializer

class CarteleriaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        BootstrapContentInitializer().initializeIfNeeded(this)
    }
}
