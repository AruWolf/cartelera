package com.litvy.carteleria.ui.menu.SubMenues

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicText
import com.litvy.carteleria.BuildConfig
import com.litvy.carteleria.config.ExternalLinks
import com.litvy.carteleria.util.qr.generateQrCode

@Composable
fun AboutSubMenu() {
    val qrBitmap = remember {
        generateQrCode(ExternalLinks.COMPANY_URL, size = 360).asImageBitmap()
    }

    Row(
        modifier = Modifier
            .width(620.dp)
            .fillMaxHeight()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Column(
            modifier = Modifier.widthIn(max = 360.dp)
        ) {
            AboutText(
                text = "Cartelera Digital TV",
                fontSize = 24,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            AboutText(text = "Versi\u00f3n ${BuildConfig.VERSION_NAME}")
            AboutText(
                text = "Powered by Litvy S.A.S.",
                color = Color.White.copy(alpha = 0.68f),
                fontSize = 18
            )

            AboutSectionSpacer()

            AboutHeading("EMPRESA")
            AboutText("Litvy S.A.S.")
            AboutText(
                text = "Sitio Web",
                color = Color.White.copy(alpha = 0.78f),
                fontSize = 18
            )
            AboutText(
                text = ExternalLinks.COMPANY_URL,
                color = Color.White.copy(alpha = 0.78f),
                fontSize = 18
            )

            AboutSectionSpacer()

            AboutHeading("DESARROLLADOR")
            AboutText("Leo")
            AboutText("LinkedIn", color = Color.White.copy(alpha = 0.78f), fontSize = 18)
            AboutText("GitHub", color = Color.White.copy(alpha = 0.78f), fontSize = 18)

            AboutSectionSpacer()

            AboutText(
                text = "\u00a9 Litvy S.A.S.",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 18
            )
            AboutText(
                text = "Todos los derechos reservados.",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 18
            )
        }

        Column(
            modifier = Modifier.padding(top = 104.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                bitmap = qrBitmap,
                contentDescription = null,
                modifier = Modifier.size(172.dp)
            )
        }
    }
}

@Composable
private fun AboutSectionSpacer() {
    Spacer(modifier = Modifier.height(28.dp))
}

@Composable
private fun AboutHeading(text: String) {
    AboutText(
        text = text,
        color = Color.White.copy(alpha = 0.62f),
        fontSize = 16,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun AboutText(
    text: String,
    color: Color = Color.White,
    fontSize: Int = 20,
    fontWeight: FontWeight = FontWeight.Normal
) {
    BasicText(
        text = text,
        style = TextStyle(
            color = color,
            fontSize = fontSize.sp,
            fontWeight = fontWeight
        )
    )
}
