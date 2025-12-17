package com.example.busbookingsystem.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.busbookingsystem.data.model.location.DistrictDto
import kotlin.collections.forEach

@Composable
fun DistrictDropdownField(
    label: String,
    selectedDistrict: DistrictDto?,
    districts: List<DistrictDto>,
    isExpanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onDistrictSelected: (DistrictDto) -> Unit,
    isEnabled: Boolean = true,
    primaryColor: Color,
    fontFamily: androidx.compose.ui.text.font.FontFamily
) {
    Box {
        OutlinedTextField(
            value = selectedDistrict?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label, fontFamily = fontFamily, fontSize = 12.sp) },
            trailingIcon = {
                Icon(if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, null)
            },
            enabled = isEnabled,
            modifier = Modifier.fillMaxWidth().clickable(enabled = isEnabled) { onExpandChange(true) },
            shape = RoundedCornerShape(12.dp),

            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = Color.LightGray,
                disabledBorderColor = Color.LightGray.copy(alpha = 0.5f),

                focusedLabelColor = primaryColor,
                unfocusedLabelColor = Color.Gray,
                disabledLabelColor = Color.Gray.copy(alpha = 0.5f),

                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Gray
            ),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = fontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        )

        // Tıklama yakalayıcı (Aynı kaldı)
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(enabled = isEnabled) { onExpandChange(true) }
        )

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { onExpandChange(false) },
            modifier = Modifier.background(Color.White)
        ) {
            if (districts.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("İlçe bulunamadı", color = Color.Gray, fontFamily = fontFamily) },
                    onClick = { onExpandChange(false) }
                )
            } else {
                districts.forEach { district ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = district.name,
                                color = Color.Black,
                                fontFamily = fontFamily
                            )
                        },
                        onClick = {
                            onDistrictSelected(district)
                            onExpandChange(false)
                        }
                    )
                }
            }
        }
    }
}