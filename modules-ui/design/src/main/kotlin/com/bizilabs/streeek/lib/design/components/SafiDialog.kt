package com.bizilabs.streeek.lib.design.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

sealed class DialogState(open val title: String, open val message: String) {

    data class Loading(override val title: String = "", override val message: String = "") :
        DialogState(title = title, message = message)

    data class Success(override val title: String, override val message: String) :
        DialogState(title = title, message = message)

    data class Error(override val title: String, override val message: String) :
        DialogState(title = title, message = message)

    data class Info(override val title: String, override val message: String) :
        DialogState(title = title, message = message)

    data class Caution(override val title: String, override val message: String) :
        DialogState(title = title, message = message)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafiBottomDialog(
    state: DialogState?,
    onClickDismiss: () -> Unit,
) {

    val bottomSheetState = rememberModalBottomSheetState()

    AnimatedVisibility(visible = state != null) {
        LaunchedEffect(state) {
            if (state != null) bottomSheetState.show() else bottomSheetState.hide()
        }
        ModalBottomSheet(
            onDismissRequest = {},
            sheetState = bottomSheetState
        ) {
            AnimatedContent(targetState = state, label = "dialog state") { dialog ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    if (dialog != null)
                        when (dialog) {
                            is DialogState.Caution -> {
                                SafiInfoSection(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 32.dp),
                                    icon = Icons.Rounded.WarningAmber,
                                    title = dialog.title,
                                    description = dialog.message
                                ) {
                                    TextButton(onClick = onClickDismiss) {
                                        Text(text = "Dismiss")
                                    }
                                }
                            }

                            is DialogState.Error -> {
                                SafiInfoSection(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 32.dp),
                                    icon = Icons.Rounded.Error,
                                    title = dialog.title,
                                    description = dialog.message
                                ) {
                                    TextButton(onClick = onClickDismiss) {
                                        Text(text = "Dismiss")
                                    }
                                }
                            }

                            is DialogState.Info -> {
                                SafiInfoSection(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 32.dp),
                                    icon = Icons.Rounded.Info,
                                    title = dialog.title,
                                    description = dialog.message
                                ) {
                                    TextButton(onClick = onClickDismiss) {
                                        Text(text = "Dismiss")
                                    }
                                }
                            }

                            is DialogState.Loading -> {
                                SafiCenteredColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(64.dp)
                                ) {
                                    CircularProgressIndicator()
                                }
                            }

                            is DialogState.Success -> {
                                SafiInfoSection(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 32.dp),
                                    icon = Icons.Rounded.CheckCircle,
                                    title = dialog.title,
                                    description = dialog.message
                                ) {
                                    TextButton(onClick = onClickDismiss) {
                                        Text(text = "Dismiss")
                                    }
                                }
                            }

                        }
                }

            }
        }
    }
}