package com.litvy.carteleria.slides

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
//import androidx.media3.exoplayer.ExoPlayer
//import androidx.media3.ui.PlayerView

class VideoSlide(
    val videoRes: Int,
    val loop: Boolean = true,
) /*: Slide*/ {
    /*
    @Composable
    override fun Render() {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                PlayerView(context).apply {
                    player = ExoPlayer.Builder(context).build().apply {
                        val uri = uri.parse(
                            "android.resource://${context.packageName}/$videoRes"
                        )
                        setMediaItem(MediaItem.fromUri(uri))
                        repeatMode = if (loop) player.REPEAT_MODE_ALL
                        else player.REPEAT_MODE_OFF
                        prepare()
                        playWhenReady = true
                    }
                    useController = false
                }

        )
    }*/
}