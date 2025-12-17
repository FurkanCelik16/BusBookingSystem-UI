package com.example.busbookingsystem.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.busbookingsystem.data.api.RetrofitClient
import com.example.busbookingsystem.data.model.trip.TripDto
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTripListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetails: (Int) -> Unit
) {
    var trips by remember { mutableStateOf<List<TripDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var ticketCounts by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var tripToDelete by remember { mutableStateOf<TripDto?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val AdminColor = Color(0xFFD32F2F)

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.api.getAllTrips()
            if (response.success && response.data != null) {
                val sortedTrips = response.data.sortedByDescending { it.id }
                trips = sortedTrips

                val counts = mutableMapOf<Int, Int>()
                val jobs = sortedTrips.map { trip ->
                    async {
                        try {
                            val ticketRes = RetrofitClient.api.getTicketsByTrip(trip.id)
                            if (ticketRes.success && ticketRes.data != null) {
                                val now = LocalDateTime.now()
                                val formatter = DateTimeFormatter.ISO_DATE_TIME

                                val validCount = ticketRes.data.count { ticket ->
                                    if (ticket.isPaid) true
                                    else if (ticket.isReserved && ticket.reservationExpiresAt != null) {
                                        try {
                                            val expireTime = LocalDateTime.parse(ticket.reservationExpiresAt, formatter)
                                            expireTime.isAfter(now)
                                        } catch (e: Exception) { false }
                                    } else false
                                }
                                return@async trip.id to validCount
                            }
                        } catch (e: Exception) {
                        }
                        return@async trip.id to trip.soldTicketCount
                    }
                }
                jobs.awaitAll().forEach { (id, count) ->
                    counts[id] = count
                }
                ticketCounts = counts
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    fun deleteTrip(trip: TripDto) {
        scope.launch {
            try {
                val response = RetrofitClient.api.deleteTrip(trip.id)
                if (response.success) {
                    Toast.makeText(context, "✅ Sefer Silindi", Toast.LENGTH_SHORT).show()
                    trips = trips.filter { it.id != trip.id }
                } else {
                    Toast.makeText(context, "Hata: ${response.message}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Sefer Yönetimi", color = Color.White, fontWeight = FontWeight.Bold) },
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
            } else if (trips.isEmpty()) {
                Text("Henüz sefer yok.", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(trips) { trip ->
                        val realCount = ticketCounts[trip.id] ?: trip.soldTicketCount

                        AdminTripCard(
                            trip = trip,
                            displayTicketCount = realCount,
                            onDeleteClick = {
                                tripToDelete = trip
                                showDeleteDialog = true
                            },
                            onCardClick = { onNavigateToDetails(trip.id) }
                        )
                    }
                }
            }
        }
        if (showDeleteDialog && tripToDelete != null) {
            val count = ticketCounts[tripToDelete!!.id] ?: tripToDelete!!.soldTicketCount
            val hasTickets = count > 0

            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text(if(hasTickets) "⚠️ Biletli Sefer!" else "Seferi Sil?", color = Color.DarkGray) },
                text = { Text(if(hasTickets) "Bu seferde $count aktif bilet var. Silerseniz biletler yanar." else "Silmek istediğinize emin misiniz?", color = Color.DarkGray) },
                confirmButton = {
                    Button(
                        onClick = { deleteTrip(tripToDelete!!); showDeleteDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) { Text("SİL") }
                },
                dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("İptal") } },
                containerColor = Color.White
            )
        }
    }
}

@Composable
fun AdminTripCard(
    trip: TripDto,
    displayTicketCount: Int,
    onDeleteClick: () -> Unit,
    onCardClick: () -> Unit
) {
    val originText = if (!trip.originDistrictName.isNullOrEmpty()) "${trip.originCityName} (${trip.originDistrictName})" else "${trip.originCityName} (Merkez)"
    val destinationText = if (!trip.destinationDistrictName.isNullOrEmpty()) "${trip.destinationCityName} (${trip.destinationDistrictName})" else "${trip.destinationCityName} (Merkez)"

    Card(
        modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(12.dp)).clickable { onCardClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "$originText ➝ $destinationText", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${trip.departureDate ?: ""} ${trip.departureTime?.take(5) ?: ""}", color = Color.DarkGray, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DirectionsBus, null, tint = Color(0xFF1976D2), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${trip.companyName}", color = Color(0xFF1976D2), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }

                if (displayTicketCount > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(color = Color(0xFFFFF3E0), shape = RoundedCornerShape(4.dp)) {
                        Text(
                            text = "$displayTicketCount Bilet Satıldı",
                            color = Color(0xFFE65100),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.SpaceBetween) {
                Text(text = "${trip.price} ₺", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF43A047))
                Spacer(modifier = Modifier.height(12.dp))
                IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, "Sil", tint = Color.Red.copy(alpha = 0.8f))
                }
            }
        }
    }
}