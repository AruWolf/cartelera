package com.litvy.carteleria.ui.menu.preview

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.litvy.carteleria.domain.external.ExternalFile
import java.io.File

@Composable
fun FilePreviewPanel(
    file: ExternalFile?
) {

    if (file == null) return

    Box(
        modifier = Modifier
            .width(500.dp)
            .fillMaxHeight()
            .padding(24.dp)
            .background(Color.Black.copy(alpha = 0.95f))
    ) {

        val ext = file.name.substringAfterLast('.', "").lowercase()
        val fileObj = File(file.path)

        if (ext in listOf("png", "jpg", "jpeg", "webp")) {
            ImagePreview(fileObj)
        } else {
            VideoPreview(fileObj)
        }
    }
}

@Composable
private fun ImagePreview(file: File) {
    AsyncImage(
        model = file,
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun VideoPreview(file: File) {

    val context = LocalContext.current

    val player = remember(file.path) {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(Uri.fromFile(file))
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    DisposableEffect(player) {
        onDispose {
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