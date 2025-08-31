package com.goldenraven.devkitwallet.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.goldenraven.devkitwallet.ui.theme.DevkitWalletColors

@Composable
fun NeutralButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = DevkitWalletColors.secondary,
            disabledContainerColor = DevkitWalletColors.secondary,
        ),
        shape = RoundedCornerShape(16.dp),
        enabled = enabled,
        modifier = Modifier
            .height(80.dp)
            .fillMaxWidth(0.9f)
            .padding(vertical = 8.dp, horizontal = 8.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
    ) {
        Text(
            text = text,
        )
    }
}
