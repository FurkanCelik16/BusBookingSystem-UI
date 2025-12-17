package com.example.busbookingsystem.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> TripDropdownField(
    label: String,
    options: List<T>,
    selectedOption: T?,
    onOptionSelected: (T) -> Unit,
    itemLabel: (T) -> String,
    icon: ImageVector? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedOption?.let { itemLabel(it) } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label, color = Color.Gray) }, // Label hafif gri kalabilir, şık durur
            leadingIcon = icon?.let { { Icon(it, contentDescription = null, tint = Color.Black) } },
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    "Aç",
                    Modifier.clickable { expanded = !expanded },
                    tint = Color.Black
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFD32F2F),
                focusedLabelColor = Color(0xFFD32F2F),

                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,

                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { expanded = !expanded }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth(0.9f)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(itemLabel(option), color = Color.Black)
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = Color.Black,
                    )
                )
            }
        }
    }
}