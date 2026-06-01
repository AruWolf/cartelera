package com.litvy.carteleria.ui.loading

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.LottieCompositionResult
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun AppLoadingSurface(
    backgroundName: String,
    loadingAnimationName: String = AppVisualAssets.LOADING_INDICATOR,
    message: String? = null,
    modifier: Modifier = Modifier,
    fallbackBackgroundColor: Color = Color.Black,
    showOverlayScrim: Boolean = false,
    showLoadingIndicator: Boolean = true,
    loadingAlignment: Alignment = Alignment.BottomEnd
) {
    val context = LocalContext.current
    val backgroundRes = remember(backgroundName) {
        AppVisualAssets.drawableId(context, backgroundName)
    }
    val animationRes = remember(loadingAnimationName) {
        AppVisualAssets.rawId(context, loadingAnimationName)
    }

    val compositionResult: LottieCompositionResult? =
        if (animationRes != 0) {
            rememberLottieComposition(LottieCompositionSpec.RawRes(animationRes))
        } else {
            null
        }
    val composition = compositionResult?.value
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(fallbackBackgroundColor)
    ) {
        if (backgroundRes != 0) {
            Image(
                painter = painterResource(id = backgroundRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (showOverlayScrim) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f))
            )
        }

        if (showLoadingIndicator) {
            Column(
                modifier = Modifier
                    .align(loadingAlignment)
                    .padding(LoadingUiDefaults.INDICATOR_EDGE_PADDING),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (animationRes != 0 && composition != null) {
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(LoadingUiDefaults.INDICATOR_SIZE)
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.size(LoadingUiDefaults.INDICATOR_SIZE),
                        color = Color.White
                    )
                }

                if (!message.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(LoadingUiDefaults.INDICATOR_MESSAGE_SPACING))
                    Text(
                        text = message,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else if (!message.isNullOrBlank()) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = message,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }

        Text(
            text = "Powered by Litvy S.A.S.",
            color = Color.White.copy(alpha = 0.58f),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 14.dp)
        )
    }
}
