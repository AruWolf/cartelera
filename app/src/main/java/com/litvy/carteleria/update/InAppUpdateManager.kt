package com.litvy.carteleria.update

import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.InstallStatus

class InAppUpdateManager(
    private val activity: Activity,
    private val launcher: ActivityResultLauncher<IntentSenderRequest>
) {

    private val appUpdateManager =
        AppUpdateManagerFactory.create(activity)

    private val installStateListener = InstallStateUpdatedListener { state ->

        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            completeUpdate()
        }
    }

    fun registerListener() {
        appUpdateManager.registerListener(installStateListener)
    }

    fun unregisterListener() {
        appUpdateManager.unregisterListener(installStateListener)
    }

    fun checkForUpdates() {

        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { info ->
                val availability = info.updateAvailability()

                if (availability == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    startImmediateUpdate(info)
                    return@addOnSuccessListener
                }

                if (
                    availability == UpdateAvailability.UPDATE_AVAILABLE &&
                    info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                ) {
                    startImmediateUpdate(info)
                    return@addOnSuccessListener
                }

                if (
                    availability == UpdateAvailability.UPDATE_AVAILABLE &&
                    info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                ) {
                    startFlexibleUpdate(info)
                }
            }
    }

    private fun startImmediateUpdate(info: AppUpdateInfo) {

        appUpdateManager.startUpdateFlowForResult(
            info,
            launcher,
            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
        )
    }

    private fun startFlexibleUpdate(info: AppUpdateInfo) {

        appUpdateManager.startUpdateFlowForResult(
            info,
            launcher,
            AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
        )
    }

    private fun completeUpdate() {
        appUpdateManager.completeUpdate()
    }
}