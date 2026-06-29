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
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.litvy.carteleria.BuildConfig
import com.litvy.carteleria.R
import com.litvy.carteleria.config.ExternalLinks
import com.litvy.carteleria.util.qr.generateQrCode
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import com.litvy.carteleria.util.DeviceUtils
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun AboutSubMenu() {

    val context = LocalContext.current
    val isMobile = !DeviceUtils.isTv(context)

    val websiteQrBitmap = remember {
        generateQrCode(ExternalLinks.WEBSITE_URL, size = 360).asImageBitmap()
    }
    val userManualQrBitmap = remember {
        generateQrCode(ExternalLinks.USER_MANUAL_URL, size = 360).asImageBitmap()
    }

    fun openUrl(url: String){
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
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
                text = stringResource(R.string.about_app_title),
                fontSize = 24,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            AboutText(text = stringResource(R.string.about_version, BuildConfig.VERSION_NAME))
            AboutText(
                text = stringResource(R.string.about_app_description),
                color = Color.White.copy(alpha = 0.68f),
                fontSize = 18
            )
            AboutText(
                text = stringResource(R.string.powered_by_litvy),
                color = Color.White.copy(alpha = 0.68f),
                fontSize = 18
            )

            AboutSectionSpacer()

            AboutHeading(stringResource(R.string.about_company_heading))
            AboutText(stringResource(R.string.about_company_name))
            AboutText(
                text = stringResource(R.string.about_website_label),
                color = Color.White.copy(alpha = 0.78f),
                fontSize = 18
            )
            AboutText(
                text = ExternalLinks.WEBSITE_URL,
                color = Color.White.copy(alpha = 0.78f),
                fontSize = 18,
                modifier = if (isMobile){
                    Modifier.clickable { openUrl(ExternalLinks.WEBSITE_URL) }
                } else {
                    Modifier
                },
                isLink = isMobile
            )

            AboutSectionSpacer()

            AboutHeading(stringResource(R.string.about_developer_heading))
            AboutText(stringResource(R.string.about_developer_name))
            AboutText(
                ExternalLinks.LINKEDIN_URL,
                color = Color.White.copy(alpha = 0.78f),
                fontSize = 18,
                modifier = if (isMobile){
                    Modifier.clickable { openUrl(ExternalLinks.LINKEDIN_URL) }
                } else {
                    Modifier
                },
                isLink = isMobile
            )
            AboutText(
                ExternalLinks.GITHUB_URL,
                color = Color.White.copy(alpha = 0.78f),
                fontSize = 18,
                modifier = if (isMobile){
                    Modifier.clickable { openUrl(ExternalLinks.GITHUB_URL) }
                } else {
                    Modifier
                },
                isLink = isMobile
            )

            AboutSectionSpacer()

            AboutText(
                text = stringResource(R.string.about_copyright),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 18
            )
            AboutText(
                text = stringResource(R.string.about_rights_reserved),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 18
            )
        }

        Column(
            modifier = Modifier.padding(top = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            AboutQrBlock(
                title = stringResource(R.string.about_litvy_page),
                bitmap = websiteQrBitmap,
                clickable = isMobile,
                onClick = { openUrl(ExternalLinks.WEBSITE_URL) }
            )
            AboutQrBlock(
                title = stringResource(R.string.manual_privacy_title),
                bitmap = userManualQrBitmap,
                clickable = isMobile,
                onClick = {openUrl(ExternalLinks.USER_MANUAL_URL)}
            )
        }
    }
}

@Composable
private fun AboutQrBlock(
    title: String,
    bitmap: ImageBitmap,
    clickable: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.width(180.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AboutText(
            text = title,
            modifier = Modifier.width(180.dp),
            color = Color.White.copy(alpha = 0.86f),
            fontSize = 16,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))
        Image(
            bitmap = bitmap,
            contentDescription = null,
            modifier = Modifier
                .size(172.dp)
                .then(
                    if (clickable) {
                        Modifier.clickable { onClick() }
                    } else {
                        Modifier
                    }
                )
        )
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
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    fontSize: Int = 20,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.Start,
    isLink: Boolean = false
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = TextStyle(
            color = color,
            fontSize = fontSize.sp,
            fontWeight = fontWeight,
            textAlign = textAlign,
            textDecoration = if (isLink) TextDecoration.Underline else TextDecoration.None
        )
    )
}