/*
 * Copyright 2021-2025 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.wallet

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.UserRoundMinus
import com.composables.icons.lucide.UserRoundPlus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bitcoindevkit.devkitwallet.presentation.navigation.HomeScreen
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.presentation.theme.standardText
import org.bitcoindevkit.devkitwallet.presentation.ui.components.NeutralButton
import org.bitcoindevkit.devkitwallet.presentation.ui.components.SecondaryScreensAppBar
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.Recipient
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.SendScreenAction
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.TransactionType
import org.bitcoindevkit.devkitwallet.presentation.viewmodels.TxDataBundle

private const val TAG = "SendScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SendScreen(
    onAction: (SendScreenAction) -> Unit,
    navController: NavController,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val recipientList: MutableList<Recipient> = remember { mutableStateListOf(Recipient(address = "", amount = 0u)) }
    val feeRate: MutableState<String> = rememberSaveable { mutableStateOf("") }
    val (showDialog, setShowDialog) = rememberSaveable { mutableStateOf(false) }
    val sendAll: MutableState<Boolean> = remember { mutableStateOf(false) }

    val bottomSheetScaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        topBar = {
            SecondaryScreensAppBar(
                title = "Send Bitcoin",
                navigation = { navController.navigate(HomeScreen) }
            )
        },
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetContent = { AdvancedOptions(sendAll, recipientList) },
        sheetContainerColor = DevkitWalletColors.primaryDark,
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 0.dp,
    ) { paddingValues ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(DevkitWalletColors.primary)
        ) {
            val (transactionInputs, bottomButtons) = createRefs()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .constrainAs(transactionInputs) {
                        top.linkTo(parent.top)
                        bottom.linkTo(bottomButtons.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        height = Dimension.fillToConstraints
                    }
            ) {
                TransactionRecipientInput(recipientList = recipientList)
                TransactionAmountInput(
                    recipientList = recipientList,
                    transactionType = if (sendAll.value) TransactionType.SEND_ALL else TransactionType.STANDARD
                )
                TransactionFeeInput(feeRate = feeRate)
                Dialog(
                    recipientList = recipientList,
                    feeRate = feeRate,
                    showDialog = showDialog,
                    setShowDialog = setShowDialog,
                    transactionType = if (sendAll.value) TransactionType.SEND_ALL else TransactionType.STANDARD,
                    context = context,
                    onAction = onAction
                )
            }
            Column(
                Modifier
                    .constrainAs(bottomButtons) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }.padding(bottom = 32.dp)
            ) {
                MoreOptions(coroutineScope = coroutineScope, bottomSheetScaffoldState = bottomSheetScaffoldState)
                Button(
                    onClick = { setShowDialog(true) },
                    colors = ButtonDefaults.buttonColors(DevkitWalletColors.accent2),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .height(80.dp)
                        .fillMaxWidth(0.9f)
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
                ) {
                    Text(
                        text = "Broadcast transaction",
                    )
                }
            }
        }
    }
}

@Composable
internal fun AdvancedOptions(
    sendAll: MutableState<Boolean>,
    recipientList: MutableList<Recipient>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Send All",
                style = standardText,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = sendAll.value,
                onCheckedChange = {
                    sendAll.value = !sendAll.value
                    while (recipientList.size > 1) {
                        recipientList.removeLast()
                    }
                },
                colors = SwitchDefaults.colors(
                    uncheckedBorderColor = DevkitWalletColors.primaryDark,
                    uncheckedThumbColor = DevkitWalletColors.primaryDark,
                    uncheckedTrackColor = DevkitWalletColors.white,
                    checkedThumbColor = DevkitWalletColors.white,
                    checkedTrackColor = DevkitWalletColors.accent1
                )
            )
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Number of Recipients",
                style = standardText
            )
            Text(
                text = "${recipientList.size}",
                style = standardText
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy((-1).dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                val startShape = RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp)
                val endShape = RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp)
                Box(
                    Modifier
                        .clip(startShape)
                        .border(2.dp, Color.White, startShape)
                        .clickable(role = Role.Button) {
                            recipientList.add(Recipient("", 0u))
                        }.padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Icon(Lucide.UserRoundPlus, tint = DevkitWalletColors.white, contentDescription = null)
                }
                Box(
                    Modifier
                        .clip(endShape)
                        .border(2.dp, Color.White, endShape)
                        .clickable(role = Role.Button) {
                            if (recipientList.size > 1) {
                                recipientList.removeLast()
                            }
                        }.padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Icon(Lucide.UserRoundMinus, tint = DevkitWalletColors.white, contentDescription = null)
                }
            }
            // Button(
            //     onClick = {
            //         if (recipientList.size > 1) {
            //             recipientList.removeLast()
            //         }
            //     },
            //     enabled = !sendAll.value,
            //     colors = ButtonDefaults.buttonColors(DevkitWalletColors.accent2),
            //     shape = RoundedCornerShape(16.dp),
            //     modifier = Modifier.width(70.dp)
            // ) {
            //     Text(text = "-")
            // }
            // Button(
            //     onClick = { recipientList.add(Recipient("", 0u)) },
            //     enabled = !sendAll.value,
            //     colors = ButtonDefaults.buttonColors(DevkitWalletColors.accent1),
            //     shape = RoundedCornerShape(16.dp),
            //     modifier = Modifier.width(70.dp)
            // ) {
            //     Text(text = "+")
            // }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun TransactionRecipientInput(recipientList: MutableList<Recipient>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .heightIn(max = 100.dp)
    ) {
        itemsIndexed(recipientList) { index, _ ->
            val recipientAddress: MutableState<String> = rememberSaveable { mutableStateOf("") }

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .weight(0.5f),
                    value = recipientAddress.value,
                    onValueChange = {
                        recipientAddress.value = it
                        recipientList[index].address = it
                    },
                    label = {
                        Text(
                            text = "Recipient address ${index + 1}",
                            color = DevkitWalletColors.white,
                        )
                    },
                    singleLine = true,
                    textStyle = TextStyle(color = DevkitWalletColors.white),
                    colors = OutlinedTextFieldDefaults.colors(
                        cursorColor = DevkitWalletColors.accent1,
                        focusedBorderColor = DevkitWalletColors.accent1,
                        unfocusedBorderColor = DevkitWalletColors.white
                    )
                )
            }
        }
    }
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
private fun TransactionAmountInput(recipientList: MutableList<Recipient>, transactionType: TransactionType) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .heightIn(max = 100.dp)
    ) {
        itemsIndexed(recipientList) { index, _ ->
            val amount: MutableState<String> = rememberSaveable { mutableStateOf("") }

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .weight(0.5f),
                    value = amount.value,
                    onValueChange = {
                        amount.value = it
                        recipientList[index].amount = it.toULong()
                    },
                    label = {
                        when (transactionType) {
                            TransactionType.SEND_ALL -> {
                                Text(
                                    text = "Amount (Send All)",
                                    color = DevkitWalletColors.white,
                                )
                            }
                            else -> {
                                Text(
                                    text = "Amount ${index + 1}",
                                    color = DevkitWalletColors.white,
                                )
                            }
                        }
                    },
                    singleLine = true,
                    textStyle = TextStyle(color = DevkitWalletColors.white),
                    colors = OutlinedTextFieldDefaults.colors(
                        cursorColor = DevkitWalletColors.accent1,
                        focusedBorderColor = DevkitWalletColors.accent1,
                        unfocusedBorderColor = DevkitWalletColors.white
                    ),
                    enabled = (
                        when (transactionType) {
                            TransactionType.SEND_ALL -> false
                            else -> true
                        }
                    )
                )
            }
        }
    }
}

@Composable
private fun TransactionFeeInput(feeRate: MutableState<String>) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(0.9f),
            value = feeRate.value,
            onValueChange = { newValue: String ->
                feeRate.value = newValue.filter { it.isDigit() }
            },
            singleLine = true,
            textStyle = TextStyle(color = DevkitWalletColors.white),
            label = {
                Text(
                    text = "Fee rate",
                    color = DevkitWalletColors.white,
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = DevkitWalletColors.accent1,
                focusedBorderColor = DevkitWalletColors.accent1,
                unfocusedBorderColor = DevkitWalletColors.white
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreOptions(coroutineScope: CoroutineScope, bottomSheetScaffoldState: BottomSheetScaffoldState) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NeutralButton(
            text = "Advanced options",
            onClick = {
                coroutineScope.launch {
                    bottomSheetScaffoldState.bottomSheetState.expand()
                }
            },
        )
    }
}

@Composable
private fun Dialog(
    recipientList: MutableList<Recipient>,
    feeRate: MutableState<String>,
    showDialog: Boolean,
    setShowDialog: (Boolean) -> Unit,
    transactionType: TransactionType,
    context: Context,
    onAction: (SendScreenAction) -> Unit,
) {
    if (showDialog) {
        var confirmationText = "Confirm Transaction : \n"
        recipientList.forEach { confirmationText += "${it.address}, ${it.amount}\n" }
        if (feeRate.value.isNotEmpty()) {
            confirmationText += "Fee Rate : ${feeRate.value.toULong()}"
        }
        AlertDialog(
            containerColor = DevkitWalletColors.primaryLight,
            onDismissRequest = {},
            title = {
                Text(
                    text = "Confirm transaction",
                    color = DevkitWalletColors.white
                )
            },
            text = {
                Text(
                    text = confirmationText,
                    color = DevkitWalletColors.white
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (checkRecipientList(recipientList = recipientList, feeRate = feeRate, context = context)) {
                            val txDataBundle =
                                TxDataBundle(
                                    recipients = recipientList,
                                    feeRate = feeRate.value.toULong(),
                                    transactionType = transactionType,
                                )
                            onAction(SendScreenAction.Broadcast(txDataBundle))
                            setShowDialog(false)
                        }
                    },
                ) {
                    Text(
                        text = "Confirm",
                        color = DevkitWalletColors.white
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        setShowDialog(false)
                    },
                ) {
                    Text(
                        text = "Cancel",
                        color = DevkitWalletColors.white
                    )
                }
            },
        )
    }
}

// @Preview(device = Devices.PIXEL_4, showBackground = true)
// @Composable
// internal fun PreviewSendScreen() {
//     SendScreen(rememberNavController())
// }
