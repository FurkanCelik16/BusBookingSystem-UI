package com.example.busbookingsystem.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.busbookingsystem.data.model.location.CityDto
import com.example.busbookingsystem.ui.theme.AppFonts
import kotlin.collections.forEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityDropdownField(
    label: String,
    selectedCity: CityDto?,
    cities: List<CityDto>,
    isExpanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onCitySelected: (CityDto) -> Unit,
    icon: ImageVector,
    primaryColor: Color,
    fontFamily: FontFamily
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedCity?.name ?: "Şehir Seçiniz",
            onValueChange = {},
            readOnly = true,
            label = { Text(label, fontFamily = AppFonts, color = Color.Gray) },
            leadingIcon = { Icon(icon, contentDescription = null, tint = primaryColor) },
            trailingIcon = {
                IconButton(onClick = { onExpandChange(!isExpanded) }) {
                    Icon(
                        if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),

            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = Color.DarkGray,

                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,

                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,

                focusedLabelColor = primaryColor,
                unfocusedLabelColor = Color.DarkGray
            ),
            textStyle = LocalTextStyle.current.copy(fontFamily = fontFamily, fontWeight = FontWeight.Medium)
        )

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { onExpandChange(false) },
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(Color.White)
                .heightIn(max = 300.dp)
        ) {
            cities.forEach { city ->
                DropdownMenuItem(
                    text = { Text(city.name, fontFamily = AppFonts, color = Color.DarkGray) },
                    onClick = {
                        onCitySelected(city)
                        onExpandChange(false)
                    },
                    leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null, tint = primaryColor) }
                )
            }
        }
    }
}