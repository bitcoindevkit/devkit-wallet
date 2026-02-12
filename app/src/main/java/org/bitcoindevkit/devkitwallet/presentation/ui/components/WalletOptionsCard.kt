/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.bitcoindevkit.Network
import org.bitcoindevkit.devkitwallet.data.ActiveWalletScriptType
import org.bitcoindevkit.devkitwallet.domain.supportedNetworks
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.presentation.theme.monoRegular
import org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro.displayString

@Composable
fun WalletOptionsCard(
    scriptTypes: List<ActiveWalletScriptType>,
    selectedNetwork: MutableState<Network>,
    selectedScriptType: MutableState<ActiveWalletScriptType>,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(
                color = DevkitWalletColors.primaryLight,
                shape = RoundedCornerShape(16.dp),
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = "Network",
            fontFamily = monoRegular,
            fontSize = 14.sp,
            color = DevkitWalletColors.white,
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp, bottom = 8.dp),
        )

        HorizontalDivider(
            color = DevkitWalletColors.primaryDark,
            thickness = 4.dp,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        supportedNetworks.forEachIndexed { index, it ->
            RadioButtonWithLabel(
                label = it.displayString(),
                isSelected = selectedNetwork.value == it,
                onSelect = { selectedNetwork.value = it },
            )
            if (index == 2) Spacer(modifier = Modifier.padding(bottom = 8.dp))
        }

        Text(
            text = "Script Type",
            fontFamily = monoRegular,
            fontSize = 14.sp,
            color = DevkitWalletColors.white,
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp, bottom = 8.dp),
        )

        HorizontalDivider(
            color = DevkitWalletColors.primaryDark,
            thickness = 4.dp,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        scriptTypes.forEachIndexed { index, it ->
            RadioButtonWithLabel(
                label = it.displayString(),
                isSelected = selectedScriptType.value == it,
                onSelect = { selectedScriptType.value = it },
            )
            if (index == 1) Spacer(modifier = Modifier.padding(bottom = 8.dp))
        }
    }
}

@Composable
fun NetworkOptionsCard(selectedNetwork: MutableState<Network>) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(
                color = DevkitWalletColors.primaryLight,
                shape = RoundedCornerShape(16.dp),
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = "Network",
            fontFamily = monoRegular,
            fontSize = 14.sp,
            color = DevkitWalletColors.white,
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp, bottom = 8.dp),
        )

        HorizontalDivider(
            color = DevkitWalletColors.primaryDark,
            thickness = 4.dp,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        supportedNetworks.forEachIndexed { index, it ->
            RadioButtonWithLabel(
                label = it.displayString(),
                isSelected = selectedNetwork.value == it,
                onSelect = { selectedNetwork.value = it },
            )
            if (index == 2) Spacer(modifier = Modifier.padding(bottom = 8.dp))
        }
    }
}
