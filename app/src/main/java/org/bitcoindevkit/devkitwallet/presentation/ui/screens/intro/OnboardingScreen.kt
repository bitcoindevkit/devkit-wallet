/*
 * Copyright 2021-2026 thunderbiscuit and contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the ./LICENSE file.
 */

package org.bitcoindevkit.devkitwallet.presentation.ui.screens.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.bitcoindevkit.devkitwallet.R
import org.bitcoindevkit.devkitwallet.presentation.theme.DevkitWalletColors
import org.bitcoindevkit.devkitwallet.presentation.theme.inter

private val surface = Color(0xFF1C1B1F)
private val onSurface = Color(0xFFE6E1E5)
private val subtle = Color(0xFF79747E)
private val accent = Color(0xFFF2D2B6)

@Composable
fun OnboardingScreen(onFinishOnboarding: () -> Unit) {
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

    @Suppress("ktlint:standard:max-line-length")
    val messages = listOf(
        "Easter egg #1: \uD83E\uDD5A",
        "Welcome to the Devkit Wallet! This app is a playground for developers and bitcoin enthusiasts to experiment with bitcoin's test networks.",
        "It is developed with the Bitcoin Dev Kit, a powerful set of libraries produced and maintained by the Bitcoin Dev Kit Foundation.\n\nThis version of the app is using Compact Block Filters to sync its wallets.",
        "The Foundation maintains this app as a way to showcase the capabilities of the Bitcoin Dev Kit and to provide a starting point for developers to build their own apps.\n\nIt is not a production application, and only works for testnet3, testnet4, signet, and regtest. Have fun!"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(surface)
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(120.dp))

        // Logo
        Box(
            modifier = Modifier
                .size(100.dp)
                .border(
                    width = 2.dp,
                    color = accent.copy(alpha = 0.20f),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.bdk_logo),
                contentDescription = "Bitcoin Dev Kit logo",
                modifier = Modifier.size(56.dp),
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Devkit Wallet",
            fontFamily = inter,
            fontSize = 24.sp,
            fontWeight = FontWeight.Light,
            color = onSurface,
        )
        Text(
            text = "BITCOIN DEVELOPMENT KIT",
            fontFamily = inter,
            fontSize = 11.sp,
            color = subtle,
            letterSpacing = 1.5.sp,
        )

        Spacer(Modifier.height(48.dp))

        // Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) { page ->
            Text(
                text = messages[page],
                fontFamily = inter,
                fontSize = 15.sp,
                lineHeight = 24.sp,
                color = onSurface.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
            )
        }

        // Page indicators
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 32.dp),
        ) {
            repeat(3) { index ->
                val isSelected = pagerState.currentPage == index + 1
                Box(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .size(if (isSelected) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) accent else accent.copy(alpha = 0.25f)
                        ),
                )
            }
        }

        // Navigation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Previous",
                fontFamily = inter,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = subtle,
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage((pagerState.currentPage - 1).coerceIn(0, 3))
                        }
                    }
                    .border(
                        width = 1.5.dp,
                        color = subtle.copy(alpha = 0.20f),
                        shape = RoundedCornerShape(12.dp),
                    )
                    .padding(horizontal = 20.dp, vertical = 10.dp),
            )
            Text(
                text = if (pagerState.currentPage < 3) "Next" else "Get Started",
                fontFamily = inter,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (pagerState.currentPage < 3) onSurface else surface,
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) {
                        if (pagerState.currentPage < 3) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage((pagerState.currentPage + 1).coerceIn(0, 3))
                            }
                        } else {
                            onFinishOnboarding()
                        }
                    }
                    .then(
                        if (pagerState.currentPage < 3) {
                            Modifier.border(
                                width = 1.5.dp,
                                color = accent.copy(alpha = 0.30f),
                                shape = RoundedCornerShape(12.dp),
                            )
                        } else {
                            Modifier
                                .background(accent, RoundedCornerShape(12.dp))
                        }
                    )
                    .padding(horizontal = 20.dp, vertical = 10.dp),
            )
        }
    }
}
