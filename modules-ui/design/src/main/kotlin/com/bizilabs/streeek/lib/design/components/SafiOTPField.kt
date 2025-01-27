package com.bizilabs.streeek.lib.design.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bizilabs.streeek.lib.design.theme.SafiTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SafiOTPField(
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions =
        KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done,
        ),
    text: String,
    count: Int = 6,
    isPassword: Boolean = false,
    isEnabled: Boolean = true,
    isReadOnly: Boolean = false,
    onClickDone: () -> Unit = {},
    onValueChange: (String, Boolean) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        if (text.length > count) {
            throw IllegalArgumentException("Otp text value must not have more than otpCount: $count characters")
        }
    }

    BasicTextField(
        modifier = modifier.padding(8.dp),
        value = TextFieldValue(text, selection = TextRange(text.length)),
        onValueChange = {
            if (it.text.length <= count) {
                onValueChange.invoke(it.text, it.text.length == count)
            }
        },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true,
        enabled = isEnabled,
        readOnly = isReadOnly,
        keyboardOptions = keyboardOptions,
        keyboardActions =
            KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    onClickDone()
                },
            ),
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                repeat(count) { index ->
                    SafiCharacter(
                        modifier = Modifier.weight(1f).padding(horizontal = 2.dp),
                        index = index,
                        text = text,
                        isPassword = isPassword,
                    )
                }
            }
        },
    )
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SafiOTPFieldPreview() {
    SafiTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            SafiOTPField(text = "", onValueChange = { _, _ -> })
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SafiOTPFieldFullPreview() {
    SafiTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            SafiOTPField(text = "123456", onValueChange = { _, _ -> })
        }
    }
}
