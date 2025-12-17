package com.example.busbookingsystem.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.busbookingsystem.data.api.RetrofitClient
import com.example.busbookingsystem.data.model.trip.SeatAvailabilityDto
import com.example.busbookingsystem.ui.common.BusSeatItem
import com.example.busbookingsystem.ui.common.LegendItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeatSelectionScreen(
    tripId: Int,
    onNavigateBack: () -> Unit,
    onSeatSelected: (Int) -> Unit
) {
    var selectedSeat by remember { mutableStateOf<Int?>(null) }
    var seatList by remember { mutableStateOf<List<SeatAvailabilityDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current

    val PrimaryColor = Color(0xFF1E88E5)
    val BackgroundColor = Color(0xFFF5F5F5)
    val TakenColor = Color(0xFFFFEBEE)
    val TakenBorderColor = Color(0xFFEF9A9A)
    val SelectedColor = Color(0xFF43A047)

    LaunchedEffect(tripId) {
        try {
            val response = RetrofitClient.api.getTripSeats(tripId)
            if (response.success && response.data != null) {
                seatList = response.data.seats.sortedBy { it.seatNumber }
            } else {
                errorMessage = response.message ?: "Koltuk bilgisi alınamadı."
            }
        } catch (e: Exception) {
            errorMessage = "Hata: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

    val seatGridItems = remember(seatList) {
        val items = mutableListOf<SeatAvailabilityDto?>()
        val seatMap = seatList.associateBy { it.seatNumber }
        val maxSeatNumber = seatList.maxOfOrNull { it.seatNumber } ?: 40

        var seatCounter = 1

        while (seatCounter <= maxSeatNumber) {
            for (col in 1..2) {
                if (seatCounter <= maxSeatNumber) {
                    val seat = seatMap[seatCounter]
                    items.add(seat ?: SeatAvailabilityDto(seatCounter, true, "Available", null, null))
                    seatCounter++
                } else {
                    items.add(null)
                }
            }

            items.add(null)

            if (seatCounter <= maxSeatNumber) {
                val seat = seatMap[seatCounter]
                items.add(seat ?: SeatAvailabilityDto(seatCounter, true, "Available", null, null))
                seatCounter++
            } else {
                items.add(null)
            }
        }
        items.toList()
    }


    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Koltuk Seçimi", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryColor)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PrimaryColor)
            } else if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.error)
            } else {
                Column(modifier = Modifier.fillMaxSize()) {

                    Box(modifier = Modifier.weight(1f).padding(16.dp)) {
                        Card(
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp, bottom = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = "Kaptan",
                                        tint = PrimaryColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Kaptan",
                                        color = Color.DarkGray,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Divider(modifier = Modifier.padding(vertical = 12.dp))

                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(4),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(bottom = 100.dp)
                                ) {
                                    items(seatGridItems) { seat ->
                                        if (seat == null) {
                                            Spacer(modifier = Modifier.height(1.dp))
                                        } else {
                                            val isTaken = !seat.isAvailable
                                            val isSelected = selectedSeat == seat.seatNumber

                                            BusSeatItem(
                                                seatNumber = seat.seatNumber,
                                                isTaken = isTaken,
                                                isSelected = isSelected,
                                                status = seat.status,
                                                takenBorderColor = TakenBorderColor,
                                                selectedColor = SelectedColor,
                                                gender = seat.gender,
                                                onSeatClick = {
                                                    if (isTaken) {
                                                        Toast.makeText(context, "Koltuk ${seat.status} durumda!", Toast.LENGTH_SHORT).show()
                                                    } else {
                                                        selectedSeat = if (isSelected) null else seat.seatNumber
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        shadowElevation = 16.dp,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                LegendItem(color = TakenColor, borderColor = TakenBorderColor, text = "Dolu")
                                LegendItem(color = Color.White, borderColor = Color.LightGray, text = "Boş")
                                LegendItem(color = SelectedColor, borderColor = Color.Transparent, text = "Seçili")
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    if (selectedSeat != null) {
                                        onSeatSelected(selectedSeat!!)
                                    } else {
                                        Toast.makeText(context, "Lütfen bir koltuk seçin", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedSeat != null) PrimaryColor else Color.DarkGray
                                ),
                                enabled = selectedSeat != null
                            ) {
                                Text(
                                    text = if (selectedSeat != null) "Koltuk $selectedSeat ile Devam Et" else "Koltuk Seçiniz",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}