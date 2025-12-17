package com.example.busbookingsystem.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AddRoad
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.EditRoad
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    onNavigateToAddTrip: () -> Unit,
    onNavigateToListTrips: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToAddBus: () -> Unit,
    onNavigateToAddCompany:()->Unit
) {
    val PrimaryColor = Color(0xFFD32F2F)
    val BackgroundColor = Color(0xFFF5F5F5)

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Yönetici Paneli", color = Color.White, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, "Çıkış", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryColor)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Hoş Geldiniz, Admin", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                    Text("Sistemi buradan yönetebilirsiniz.", color = Color.Gray, fontSize = 14.sp)
                }
            }

            Text("Hızlı İşlemler", fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(start = 4.dp, top = 8.dp))


            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AdminMenuCard(
                    title = "Sefer Ekle",
                    icon = Icons.Default.AddRoad,
                    color = Color(0xFF4CAF50),
                    onClick = onNavigateToAddTrip,
                    modifier = Modifier.weight(1f)
                )

                AdminMenuCard(
                    title = "Seferleri Yönet",
                    icon = Icons.Default.EditRoad,
                    color = Color(0xFF2196F3),
                    onClick = onNavigateToListTrips,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AdminMenuCard(
                    title = "Otobüs Ekle",
                    icon = Icons.Default.DirectionsBus,
                    color = Color(0xFFFF9800), // Turuncu
                    onClick = { onNavigateToAddBus()},
                    modifier = Modifier.weight(1f)
                )
                AdminMenuCard(
                    title = "Firma Ekle",
                    icon = Icons.Default.DirectionsBus,
                    color = Color(0xFFFF9800), // Turuncu
                    onClick = { onNavigateToAddCompany()},
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun AdminMenuCard(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray)
        }
    }
}

