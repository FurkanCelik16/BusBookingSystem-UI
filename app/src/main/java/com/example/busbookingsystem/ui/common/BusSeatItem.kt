package com.example.busbookingsystem.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BusSeatItem(
    seatNumber: Int,
    isTaken: Boolean,
    isSelected: Boolean,
    status: String,
    takenBorderColor: Color = Color.Gray,
    gender:Int,
    selectedColor: Color = Color(0xFF1E88E5),
    reservedColor: Color = Color(0xFFFF9800),
    onSeatClick: () -> Unit
) {

    val MaleColor = Color(0xFF42A5F5)
    val FemaleColor = Color(0xFFEC407A)
    val ReservedColor = Color(0xFFFF9800)
    val SelectedColor = Color(0xFF2E7D32)
    val EmptyColor = Color.White

    val backgroundColor = when {
        isSelected -> SelectedColor
        status == "Reserved" -> ReservedColor
        status == "Sold" && gender == 2 -> FemaleColor
        status == "Sold" && gender == 1 -> MaleColor
        status == "Sold" -> Color.Gray
        else -> EmptyColor
    }

    val textColor = if (backgroundColor == EmptyColor) Color.Black else Color.White
    val borderColor = when {
        isSelected -> selectedColor
        status == "Reserved" -> reservedColor
        status == "Sold" || isTaken -> takenBorderColor
        else -> Color(0xFFBDBDBD)
    }


    val seatShape = RoundedCornerShape(
        topStart = 12.dp,
        topEnd = 12.dp,
        bottomStart = 4.dp,
        bottomEnd = 4.dp
    )

    val shadowElevation = if (!isTaken && status == "Available") 2.dp else 0.dp

    Box(
        modifier = Modifier
            .height(50.dp)
            .width(44.dp)
            .shadow(shadowElevation, seatShape)
            .clip(seatShape)
            .background(backgroundColor)
            .border(width = 1.dp, color = borderColor, shape = seatShape)
            .clickable(enabled = (status == "Available")) { onSeatClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = seatNumber.toString(),
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            if (status == "Reserved") {
                Text(
                    text = "R",
                    fontSize = 8.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}