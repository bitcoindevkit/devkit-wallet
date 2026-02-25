/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.bitcoindevkit.devkitwallet.presentation.navigation.HomeScreen
import org.bitcoindevkit.devkitwallet.presentation.theme.inter
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.SendViewModel
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.Recipient
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.SendScreenAction
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.TransactionType
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.mvi.TxDataBundle

private const val TAG = "SendScreen"

@Composable
internal fun SendScreen(navController: NavController, sendViewModel: SendViewModel) {
    val onAction = sendViewModel::onAction
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme

    val recipientList: MutableList<Recipient> = remember { mutableStateListOf(Recipient(address = "", amount = 0u)) }
    val feeRate: MutableState<String> = rememberSaveable { mutableStateOf("") }
    val (showDialog, setShowDialog) = rememberSaveable { mutableStateOf(false) }

    val sendAll: MutableState<Boolean> = remember { mutableStateOf(false) }
    val opReturnMsg: MutableState<String?> = remember { mutableStateOf(null) }
    val (showAdvanced, setShowAdvanced) = rememberSaveable { mutableStateOf(false) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = colorScheme.primary,
        unfocusedBorderColor = colorScheme.outline.copy(alpha = 0.30f),
        cursorColor = colorScheme.primary,
        focusedLabelColor = colorScheme.primary,
        unfocusedLabelColor = colorScheme.onSurface.copy(alpha = 0.5f),
    )

    Scaffold(
        topBar = {
            SecondaryScreensAppBar(
                title = "Send Bitcoin",
                navigation = { navController.navigate(HomeScreen) },
            )
        },
        containerColor = colorScheme.surface,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(16.dp))

            // Recipient address fields
            recipientList.forEachIndexed { index, _ ->
                val recipientAddress: MutableState<String> = rememberSaveable { mutableStateOf("") }

                FormLabel(text = "Recipient address${if (recipientList.size > 1) " ${index + 1}" else ""}")
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = recipientAddress.value,
                    onValueChange = {
                        recipientAddress.value = it
                        recipientList[index].address = it
                    },
                    placeholder = {
                        Text(
                            text = "bc1q...",
                            color = colorScheme.onSurface.copy(alpha = 0.3f),
                            fontFamily = inter,
                        )
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = colorScheme.onSurface,
                        fontFamily = inter,
                        fontSize = 15.sp,
                    ),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp),
                )
                Spacer(Modifier.height(16.dp))
            }

            // Amount fields
            recipientList.forEachIndexed { index, _ ->
                val amount: MutableState<String> = rememberSaveable { mutableStateOf("") }
                val transactionType = if (sendAll.value) TransactionType.SEND_ALL else TransactionType.STANDARD

                FormLabel(
                    text = when {
                        transactionType == TransactionType.SEND_ALL -> "Amount (Send All)"
                        recipientList.size > 1 -> "Amount ${index + 1}"
                        else -> "Amount"
                    }
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = amount.value,
                    onValueChange = {
                        amount.value = it
                        recipientList[index].amount = it.toULongOrNull() ?: 0u
                    },
                    trailingIcon = {
                        Text(
                            text = "sats",
                            color = colorScheme.onSurface.copy(alpha = 0.4f),
                            fontFamily = inter,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(end = 8.dp),
                        )
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = colorScheme.onSurface,
                        fontFamily = inter,
                        fontSize = 15.sp,
                    ),
                    colors = textFieldColors,
                    shape = RoundedCornerShape(12.dp),
                    enabled = transactionType != TransactionType.SEND_ALL,
                )
                Spacer(Modifier.height(16.dp))
            }

            // Fee rate
            FormLabel(text = "Fee rate")
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = feeRate.value,
                onValueChange = { newValue: String ->
                    feeRate.value = newValue.filter { it.isDigit() }
                },
                trailingIcon = {
                    Text(
                        text = "sat/vB",
                        color = colorScheme.onSurface.copy(alpha = 0.4f),
                        fontFamily = inter,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                },
                singleLine = true,
                textStyle = TextStyle(
                    color = colorScheme.onSurface,
                    fontFamily = inter,
                    fontSize = 15.sp,
                ),
                colors = textFieldColors,
                shape = RoundedCornerShape(12.dp),
            )
            Text(
                text = "Suggested: 1–10 for low priority",
                color = colorScheme.onSurface.copy(alpha = 0.35f),
                fontFamily = inter,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, top = 4.dp),
            )

            Spacer(Modifier.height(20.dp))

            // Advanced options toggle
            TextButton(
                onClick = { setShowAdvanced(!showAdvanced) },
            ) {
                Text(
                    text = if (showAdvanced) "Hide advanced options" else "Advanced options",
                    color = colorScheme.primary,
                    fontFamily = inter,
                    fontSize = 14.sp,
                )
            }

            AnimatedVisibility(visible = showAdvanced) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Spacer(Modifier.height(8.dp))

                    // Send all switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "Send All",
                            color = colorScheme.onSurface,
                            fontFamily = inter,
                            fontSize = 15.sp,
                        )
                        Switch(
                            checked = sendAll.value,
                            onCheckedChange = {
                                sendAll.value = !sendAll.value
                                while (recipientList.size > 1) {
                                    recipientList.removeAt(recipientList.lastIndex)
                                }
                            },
                            colors = SwitchDefaults.colors(
                                uncheckedBorderColor = colorScheme.outline.copy(alpha = 0.30f),
                                uncheckedThumbColor = colorScheme.outline,
                                uncheckedTrackColor = colorScheme.surface,
                                checkedThumbColor = colorScheme.surface,
                                checkedTrackColor = colorScheme.primary,
                            ),
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    // OP_RETURN message
                    FormLabel(text = "OP_RETURN message (optional)")
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = opReturnMsg.value ?: "",
                        onValueChange = { opReturnMsg.value = it },
                        singleLine = true,
                        textStyle = TextStyle(
                            color = colorScheme.onSurface,
                            fontFamily = inter,
                            fontSize = 15.sp,
                        ),
                        colors = textFieldColors,
                        shape = RoundedCornerShape(12.dp),
                    )

                    Spacer(Modifier.height(16.dp))

                    // Number of recipients
                    Text(
                        text = "Number of Recipients",
                        color = colorScheme.onSurface,
                        fontFamily = inter,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Button(
                            onClick = {
                                if (recipientList.size > 1) {
                                    recipientList.removeAt(recipientList.lastIndex)
                                }
                            },
                            enabled = !sendAll.value,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.secondary,
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.width(70.dp),
                        ) {
                            Text(text = "−", fontSize = 18.sp)
                        }

                        Text(
                            text = "${recipientList.size}",
                            color = colorScheme.onSurface,
                            fontFamily = inter,
                            fontSize = 18.sp,
                        )

                        Button(
                            onClick = { recipientList.add(Recipient("", 0u)) },
                            enabled = !sendAll.value,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.primary,
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.width(70.dp),
                        ) {
                            Text(text = "+", fontSize = 18.sp)
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }

            Spacer(Modifier.height(24.dp))

            // Broadcast button
            Button(
                onClick = { setShowDialog(true) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.secondary,
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
            ) {
                Text(
                    text = "Broadcast Transaction",
                    fontFamily = inter,
                    fontSize = 15.sp,
                )
            }

            Spacer(Modifier.height(32.dp))
        }

        // Confirmation dialog
        ConfirmDialog(
            recipientList = recipientList,
            feeRate = feeRate,
            showDialog = showDialog,
            setShowDialog = setShowDialog,
            transactionType = if (sendAll.value) TransactionType.SEND_ALL else TransactionType.STANDARD,
            opReturnMsg = opReturnMsg.value,
            context = context,
            onAction = onAction,
        )
    }
}

@Composable
private fun FormLabel(text: String) {
    val colorScheme = MaterialTheme.colorScheme
    Text(
        text = text,
        color = colorScheme.onSurface.copy(alpha = 0.6f),
        fontFamily = inter,
        fontSize = 13.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
    )
}

fun checkRecipientList(
    recipientList: MutableList<Recipient>,
    feeRate: MutableState<String>,
    context: Context,
): Boolean {
    if (recipientList.size > 4) {
        Toast.makeText(context, "Too many recipients", Toast.LENGTH_SHORT).show()
        return false
    }
    for (recipient in recipientList) {
        if (recipient.address == "") {
            Toast.makeText(context, "Address is empty", Toast.LENGTH_SHORT).show()
            return false
        }
    }
    if (feeRate.value.isBlank()) {
        Toast.makeText(context, "Fee rate is empty", Toast.LENGTH_SHORT).show()
        return false
    }
    return true
}

@Composable
private fun ConfirmDialog(
    recipientList: MutableList<Recipient>,
    feeRate: MutableState<String>,
    showDialog: Boolean,
    setShowDialog: (Boolean) -> Unit,
    transactionType: TransactionType,
    opReturnMsg: String?,
    context: Context,
    onAction: (SendScreenAction) -> Unit,
) {
    if (showDialog) {
        val colorScheme = MaterialTheme.colorScheme

        var confirmationText = "Confirm Transaction:\n"
        recipientList.forEach { confirmationText += "${it.address}, ${it.amount}\n" }
        if (feeRate.value.isNotEmpty()) {
            confirmationText += "Fee Rate: ${feeRate.value.toULong()}\n"
        }
        if (!opReturnMsg.isNullOrEmpty()) {
            confirmationText += "OP_RETURN Message: $opReturnMsg"
        }
        AlertDialog(
            containerColor = colorScheme.surface,
            onDismissRequest = {},
            title = {
                Text(
                    text = "Confirm transaction",
                    color = colorScheme.onSurface,
                    fontFamily = inter,
                )
            },
            text = {
                Text(
                    text = confirmationText,
                    color = colorScheme.onSurface.copy(alpha = 0.7f),
                    fontFamily = inter,
                    fontSize = 14.sp,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (checkRecipientList(recipientList = recipientList, feeRate = feeRate, context = context)) {
                            val txDataBundle =
                                TxDataBundle(
                                    recipients = recipientList.toList(),
                                    feeRate = feeRate.value.toULong(),
                                    transactionType = transactionType,
                                    opReturnMsg = opReturnMsg,
                                )
                            onAction(SendScreenAction.Broadcast(txDataBundle))
                            setShowDialog(false)
                        }
                    },
                ) {
                    Text(
                        text = "Confirm",
                        color = colorScheme.primary,
                        fontFamily = inter,
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { setShowDialog(false) },
                ) {
                    Text(
                        text = "Cancel",
                        color = colorScheme.onSurface.copy(alpha = 0.5f),
                        fontFamily = inter,
                    )
                }
            },
        )
    }
}
