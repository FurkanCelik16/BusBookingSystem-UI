package com.example.busbookingsystem.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.busbookingsystem.data.api.RetrofitClient
import com.example.busbookingsystem.data.model.ticket.TicketDto
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripPassengerListScreen(
    tripId: Int,
    onNavigateBack: () -> Unit
) {
    var tickets by remember { mutableStateOf<List<TicketDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val AdminColor = Color(0xFFD32F2F)

    // Verileri Çek
    LaunchedEffect(tripId) {
        try {
            val response = RetrofitClient.api.getTicketsByTrip(tripId)
            if (response.success && response.data != null) {

                val now = LocalDateTime.now()
                val formatter = DateTimeFormatter.ISO_DATE_TIME

                val validTickets = response.data.filter { ticket ->
                    if (ticket.isPaid) {
                        true
                    } else {

                        if (ticket.reservationExpiresAt != null) {
                            try {
                                val expireTime = LocalDateTime.parse(ticket.reservationExpiresAt, formatter)
                                expireTime.isAfter(now)
                            } catch (e: Exception) {
                                false
                            }
                        } else {
                            false
                        }
                    }
                }

                tickets = validTickets.sortedBy { it.seatNumber }

            } else {
                Toast.makeText(context, "Yolcu listesi alınamadı", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Yolcu Listesi (Sefer #$tripId)", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AdminColor)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = AdminColor)
            } else if (tickets.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Person, null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Bu seferde henüz (aktif) yolcu yok.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    item {
                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Text("No", modifier = Modifier.width(40.dp), fontWeight = FontWeight.Bold, color = Color.Gray)
                            Text("Yolcu Adı", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = Color.Gray)
                            Text("Durum", fontWeight = FontWeight.Bold, color = Color.Gray)
                        }
                        Divider()
                    }

                    items(tickets) { ticket ->
                        PassengerRowItem(ticket)
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            "Toplam ${tickets.size} Yolcu",
                            modifier = Modifier.fillMaxWidth(),
                            fontWeight = FontWeight.Bold,
                            color = AdminColor,
                            textAlign = androidx.compose.ui.text.style.TextAlign.End
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PassengerRowItem(ticket: TicketDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${ticket.seatNumber}",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                val fullName = if (ticket.passenger != null)
                    "${ticket.passenger.firstName} ${ticket.passenger.lastName}"
                else "Misafir Yolcu"
                val displayPrice = if (ticket.price > 0) {
                    ticket.price
                } else if (ticket.trip != null && ticket.trip.price > 0) {
                    ticket.trip.price
                } else {
                    0.0
                }

                Text(text = fullName, fontWeight = FontWeight.Bold, fontSize = 16.sp)

                Text(
                    text = "$displayPrice TL",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            val statusColor = if (ticket.isPaid) Color(0xFF43A047) else Color(0xFFFF9800)
            val statusText = if (ticket.isPaid) "ÖDENDİ" else "REZERVE"

            Surface(
                color = statusColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}