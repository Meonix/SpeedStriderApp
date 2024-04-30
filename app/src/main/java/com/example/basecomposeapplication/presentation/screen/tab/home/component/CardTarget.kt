package com.example.basecomposeapplication.presentation.screen.tab.home.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CardTarget(numberTarget: Int = 50, typeTarget: String = " Week") {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 12.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row {
                Text(text = "$typeTarget goal", color = Color.Black, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "50 km",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.Gray,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Text(text = "35 km done", color = Color.Black, fontSize = 13.sp)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "15 km left", color = Color.Black, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                LinearProgressIndicator(
                    progress = 0.2f, color = MaterialTheme.colorScheme.primary, modifier = Modifier
                        .height(8.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)), trackColor = Color.LightGray
                )
            }
        }
    }
}