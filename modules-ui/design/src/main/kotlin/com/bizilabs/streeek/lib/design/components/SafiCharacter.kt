package com.bizilabs.streeek.lib.design.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bizilabs.streeek.lib.design.theme.SafiTheme

@Composable
fun SafiCharacter(
    index: Int,
    text: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
) {

    val char = when {
        index == text.length -> ""
        index > text.length -> ""
        else -> if (isPassword) "*" else text[index].toString()
    }

    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.onBackground.copy(0.2f)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = char,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Justify,
        )
    }

}


@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SafiCharacterPreviewFocused() {
    SafiTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            SafiCharacter(index = 1, text = "123")
        }
    }
}
