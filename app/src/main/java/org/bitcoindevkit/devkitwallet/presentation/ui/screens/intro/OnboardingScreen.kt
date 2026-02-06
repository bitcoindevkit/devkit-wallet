/*
 * Copyright 2021-2025 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import kotlinx.coroutines.launch
import org.bitcoindevkit.devkitwallet.R
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.presentation.theme.devkitTypography

@Composable
fun OnboardingScreen(onFinishOnboarding: () -> Unit) {
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

    @Suppress("ktlint:standard:max-line-length")
    val messages = listOf(
        "Easter egg #1: \uD83E\uDD5A",
        "Welcome to the Devkit Wallet! This app is a playground for developers and bitcoin enthusiasts to experiment with bitcoin's test networks.",
        "It is developed with the Bitcoin Dev Kit, a powerful set of libraries produced and maintained by the Bitcoin Dev Kit Foundation.\n\n\"This version of the app is using Compact Block Filters to sync its wallets.",
        "The Foundation maintains this app as a way to showcase the capabilities of the Bitcoin Dev Kit and to provide a starting point for developers to build their own apps.\n\nIt is not a production application, and only works for testnet3, testnet4, signet, and regtest. Have fun!"
    )

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(DevkitWalletColors.primary)
    ) {
        val (logo, intro, progress, buttons) = createRefs()

        Image(
            painter = painterResource(id = R.drawable.bdk_logo),
            contentDescription = "Bitcoin Dev Kit logo",
            Modifier
                .size(180.dp)
                .constrainAs(logo) {
                    top.linkTo(parent.top, margin = 140.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.constrainAs(intro) {
                top.linkTo(logo.bottom, margin = 90.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ) { page ->
            IntroTextPart(messages[page])
        }

        Row(
            modifier = Modifier.constrainAs(progress) {
                bottom.linkTo(buttons.top, margin = 32.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(3) { index ->
                val isSelected = pagerState.currentPage == index + 1
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(size = 16.dp)
                        .clip(shape = CircleShape)
                        .background(
                            if (isSelected) DevkitWalletColors.accent1 else DevkitWalletColors.accent1.copy(alpha = 0.3f)
                        )
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .constrainAs(buttons) {
                    bottom.linkTo(parent.bottom, margin = 32.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Previous",
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage((pagerState.currentPage - 1).coerceIn(0, 3))
                        }
                    },
                color = DevkitWalletColors.white,
                style = devkitTypography.labelLarge
            )
            Text(
                text = if (pagerState.currentPage < 3) "Next" else "Awesome!",
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        if (pagerState.currentPage < 3) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage((pagerState.currentPage + 1).coerceIn(0, 3))
                            }
                        } else {
                            onFinishOnboarding()
                        }
                    },
                color = DevkitWalletColors.white,
                style = devkitTypography.labelLarge
            )
        }
    }
}

@Composable
fun IntroTextPart(message: String) {
    Text(
        text = message,
        modifier = Modifier.padding(horizontal = 32.dp),
        color = DevkitWalletColors.white,
        style = devkitTypography.labelLarge
    )
}
