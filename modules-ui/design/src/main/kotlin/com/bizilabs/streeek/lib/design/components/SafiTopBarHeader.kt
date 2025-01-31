package com.bizilabs.streeek.lib.design.components

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bizilabs.streeek.lib.design.theme.SafiTheme

/**
 * A component to render titles for a screen
 * @sample SafiTopBarHeaderSample
 * @sample SafiTopBarHeaderSampleGuide
 */
@Composable
fun SafiTopBarHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    align: TextAlign = TextAlign.Start,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = title.uppercase(),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            textAlign = align,
        )
        AnimatedVisibility(
            modifier = Modifier.fillMaxWidth(),
            visible = subtitle != null,
        ) {
            Text(
                modifier = Modifier.padding(),
                text = subtitle ?: "",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Normal,
                textAlign = align,
            )
        }
    }
}

@Composable
private fun SafiTopBarHeaderSample() {
    SafiTopBarHeader(
        modifier = Modifier.fillMaxWidth(),
        title = "Title",
        subtitle = "A sample description",
    )
}

@Composable
private fun SafiTopBarHeaderSampleGuide() {
    Column {
        // don't make the title uppercase
        SafiTopBarHeader(
            modifier = Modifier.fillMaxWidth(),
            title = "Title".uppercase(),
            subtitle = "A sample description",
        )
        // don't make the description uppercase
        SafiTopBarHeader(
            modifier = Modifier.fillMaxWidth(),
            title = "Title",
            subtitle = "A sample description".uppercase(),
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun SafiTopBarHeaderPreview() {
    SafiTheme {
        Scaffold(topBar = {
            TopAppBar(title = {
                SafiTopBarHeader(
                    title = "Leaderboard",
                )
            })
        }) { _ -> }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun SafiTopBarHeaderCenteredPreview() {
    SafiTheme {
        Scaffold(topBar = {
            TopAppBar(title = {
                SafiTopBarHeader(
                    title = "Leaderboard",
                    align = TextAlign.Center,
                )
            })
        }) { _ -> }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun SafiTopBarHeaderWithDescriptionPreview() {
    SafiTheme {
        Scaffold(topBar = {
            TopAppBar(title = {
                SafiTopBarHeader(
                    title = "Leaderboard",
                    subtitle = "This is a leaderboard",
                )
            })
        }) { _ -> }
    }
}
