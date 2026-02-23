package com.litvy.carteleria.slides

import android.net.Uri
import android.telecom.VideoProfile.isPaused
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import java.io.File

class ExternalVideoSlide(
    override val id: String,
    private val file: File,
    override val transitionKey: String?
) : Slide {

    override val durationMs: Long? = null

    @Composable
    override fun Render(
        isPaused: Boolean,
        onFinished: (() -> Unit)?
    ) {

        val context = LocalContext.current

        val player = remember {
            ExoPlayer.Builder(context).build().apply {
                val mediaItem = MediaItem.fromUri(Uri.fromFile(file))
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
            }
        }

        // Manejo de pausa real
        LaunchedEffect(isPaused) {
            if (isPaused) player.pause()
            else player.play()
        }

        DisposableEffect(player) {

            val listener = object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED) {
                        onFinished?.invoke()
                    }
                }
            }

            player.addListener(listener)

            onDispose {
                player.removeListener(listener)
                player.release()
            }
        }

        AndroidView(
            factory = {
                PlayerView(context).apply {
                    this.player = player
                    useController = false
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}