package com.example.busbookingsystem.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
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
import com.example.busbookingsystem.data.model.location.CityDto
import com.example.busbookingsystem.data.model.location.DistrictDto
import com.example.busbookingsystem.data.model.ticket.TicketDto
import com.example.busbookingsystem.ui.common.CityDropdownField
import com.example.busbookingsystem.ui.common.DistrictDropdownField
import com.example.busbookingsystem.ui.theme.AppFonts
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(
    userId: Int,
    onSearchClick: (Int, Int?, Int, Int?, String) -> Unit,
    onMyTicketsClick: () -> Unit,
    onReservationClick: (TicketDto) -> Unit,
    onLogoutClick: () -> Unit
) {
    var cities by remember { mutableStateOf<List<CityDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    // 📍 KALKIŞ SEÇİMLERİ
    var selectedOriginCity by remember { mutableStateOf<CityDto?>(null) }
    var selectedOriginDistrict by remember { mutableStateOf<DistrictDto?>(null) }

    // 📍 VARIŞ SEÇİMLERİ
    var selectedDestinationCity by remember { mutableStateOf<CityDto?>(null) }
    var selectedDestinationDistrict by remember { mutableStateOf<DistrictDto?>(null) }

    var selectedDateInMillis by remember { mutableStateOf(System.currentTimeMillis()) }

    // DROPDOWN KONTROLLERİ
    var isOriginExpanded by remember { mutableStateOf(false) }
    var isOriginDistrictExpanded by remember { mutableStateOf(false) }
    var isDestinationExpanded by remember { mutableStateOf(false) }
    var isDestinationDistrictExpanded by remember { mutableStateOf(false) }

    var isDatePickerOpen by remember { mutableStateOf(false) }

    // SAYAÇ ve REZERVASYON STATE'LERİ
    var activeReservation by remember { mutableStateOf<TicketDto?>(null) }
    var remainingTimeText by remember { mutableStateOf("") }

    val context = LocalContext.current
    val PrimaryColor = Color(0xFF1E88E5)
    val BackgroundColor = Color(0xFFF0F4F8)

    val OrangeColor = Color(0xFFEF6C00)
    val OrangeBg = Color(0xFFFFF3E0)

    val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale("tr"))

    LaunchedEffect(selectedOriginCity) { selectedOriginDistrict = null }
    LaunchedEffect(selectedDestinationCity) { selectedDestinationDistrict = null }

    // --- VERİ YÜKLEME ---
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.api.getCities()
            if (response.success && response.data != null) {
                cities = response.data
                if (cities.size >= 2) {
                    selectedOriginCity = cities[0]
                    selectedDestinationCity = cities[1]
                }
            } else {
                errorMessage = response.message ?: "Şehirler yüklenemedi."
            }

            if (userId != 0) {
                val ticketsResponse = RetrofitClient.api.getPassengerTickets(userId)
                if (ticketsResponse.success && ticketsResponse.data != null) {
                    val reservedTicket = ticketsResponse.data.find { ticket ->
                        ticket.isReserved && !ticket.isPaid
                    }
                    activeReservation = reservedTicket
                }
            }
        } catch (e: Exception) {
            errorMessage = "Bağlantı hatası: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(activeReservation) {
        if (activeReservation != null && activeReservation!!.reservationExpiresAt != null) {
            val formatter = DateTimeFormatter.ISO_DATE_TIME
            val expireTime = LocalDateTime.parse(activeReservation!!.reservationExpiresAt, formatter)

            while (true) { // Döngü
                val now = LocalDateTime.now()
                val secondsLeft = ChronoUnit.SECONDS.between(now, expireTime)

                if (secondsLeft <= 0) {
                    activeReservation = null
                    break // Döngüyü kır
                } else {
                    val minutes = secondsLeft / 60
                    val seconds = secondsLeft % 60
                    remainingTimeText = String.format("%02d:%02d", minutes, seconds)
                }
                delay(1000)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(PrimaryColor)
                .padding(24.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Merhaba, Nereye Gidiyoruz?",
                    fontFamily = AppFonts,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onMyTicketsClick) {
                        Icon(Icons.Default.ConfirmationNumber, contentDescription = "Biletlerim", tint = Color.White)
                    }
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Çıkış Yap", tint = Color.White)
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (activeReservation != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .shadow(6.dp, RoundedCornerShape(12.dp))
                        .clickable { onReservationClick(activeReservation!!) },
                    colors = CardDefaults.cardColors(containerColor = OrangeBg), // Daima turuncu
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Ödeme Bekleyen Bilet!",
                                    fontFamily = AppFonts,
                                    fontWeight = FontWeight.Bold,
                                    color = OrangeColor,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.ArrowForward,
                                    null,
                                    tint = OrangeColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = "Koltuk No: ${activeReservation!!.seatNumber}",
                                fontFamily = AppFonts,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        Surface(
                            color = OrangeColor,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Timer, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = remainingTimeText,
                                    fontFamily = AppFonts,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryColor)
                }
            } else if (errorMessage.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = errorMessage, color = MaterialTheme.colorScheme.error, fontFamily = AppFonts)
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text("Kalkış Noktası", fontWeight = FontWeight.Bold, color = PrimaryColor, fontSize = 14.sp, fontFamily = AppFonts)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                CityDropdownField(
                                    label = "İl Seçiniz",
                                    selectedCity = selectedOriginCity,
                                    cities = cities,
                                    isExpanded = isOriginExpanded,
                                    onExpandChange = { isOriginExpanded = it },
                                    onCitySelected = { selectedOriginCity = it },
                                    icon = Icons.Default.LocationOn,
                                    primaryColor = PrimaryColor,
                                    fontFamily = AppFonts
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                DistrictDropdownField(
                                    label = "İlçe (Opsiyonel)",
                                    selectedDistrict = selectedOriginDistrict,
                                    districts = selectedOriginCity?.districts ?: emptyList(),
                                    isExpanded = isOriginDistrictExpanded,
                                    onExpandChange = { isOriginDistrictExpanded = it },
                                    onDistrictSelected = { selectedOriginDistrict = it },
                                    isEnabled = selectedOriginCity != null,
                                    primaryColor = PrimaryColor,
                                    fontFamily = AppFonts
                                )
                            }
                        }

                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Divider(color = Color.LightGray.copy(alpha = 0.5f))
                            IconButton(
                                onClick = {
                                    val tempCity = selectedOriginCity
                                    val tempDistrict = selectedOriginDistrict
                                    selectedOriginCity = selectedDestinationCity
                                    selectedOriginDistrict = selectedDestinationDistrict
                                    selectedDestinationCity = tempCity
                                    selectedDestinationDistrict = tempDistrict
                                },
                                modifier = Modifier
                                    .background(Color.White, RoundedCornerShape(50))
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(50))
                            ) {
                                Icon(Icons.Default.SwapVert, contentDescription = "Değiştir", tint = PrimaryColor)
                            }
                        }

                        Text("Varış Noktası", fontWeight = FontWeight.Bold, color = PrimaryColor, fontSize = 14.sp, fontFamily = AppFonts)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                CityDropdownField(
                                    label = "İl Seçiniz",
                                    selectedCity = selectedDestinationCity,
                                    cities = cities,
                                    isExpanded = isDestinationExpanded,
                                    onExpandChange = { isDestinationExpanded = it },
                                    onCitySelected = { selectedDestinationCity = it },
                                    icon = Icons.Default.LocationOn,
                                    primaryColor = PrimaryColor,
                                    fontFamily = AppFonts
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                DistrictDropdownField(
                                    label = "İlçe (Opsiyonel)",
                                    selectedDistrict = selectedDestinationDistrict,
                                    districts = selectedDestinationCity?.districts ?: emptyList(),
                                    isExpanded = isDestinationDistrictExpanded,
                                    onExpandChange = { isDestinationDistrictExpanded = it },
                                    onDistrictSelected = { selectedDestinationDistrict = it },
                                    isEnabled = selectedDestinationCity != null,
                                    primaryColor = PrimaryColor,
                                    fontFamily = AppFonts
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = dateFormatter.format(Date(selectedDateInMillis)),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("GİDİŞ TARİHİ", fontFamily = AppFonts, color = Color.Gray) },
                            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, tint = PrimaryColor) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isDatePickerOpen = true },
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = Color.Black,
                                disabledBorderColor = Color.LightGray,
                                disabledLeadingIconColor = PrimaryColor,
                                disabledLabelColor = Color.Gray,
                                disabledContainerColor = Color.White
                            ),
                            textStyle = LocalTextStyle.current.copy(fontFamily = AppFonts, fontWeight = FontWeight.Medium)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (selectedOriginCity != null && selectedDestinationCity != null) {
                                    val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(selectedDateInMillis))
                                    onSearchClick(
                                        selectedOriginCity!!.id,
                                        selectedOriginDistrict?.id,
                                        selectedDestinationCity!!.id,
                                        selectedDestinationDistrict?.id,
                                        dateString
                                    )
                                } else {
                                    Toast.makeText(context, "Lütfen en azından illeri seçin", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp).shadow(4.dp, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                        ) {
                            Text("Otobüs Ara", fontFamily = AppFonts, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { onMyTicketsClick() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .shadow(4.dp, RoundedCornerShape(12.dp)),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF546E7A)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("BİLETLERİMİ SORGULA", fontFamily = AppFonts, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (isDatePickerOpen) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateInMillis)
        DatePickerDialog(
            onDismissRequest = { isDatePickerOpen = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { selectedDateInMillis = it }
                    isDatePickerOpen = false
                }) {
                    Text("Tamam", fontFamily = AppFonts, fontWeight = FontWeight.Bold, color = PrimaryColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { isDatePickerOpen = false }) {
                    Text("İptal", fontFamily = AppFonts, color = Color.Gray)
                }
            },
            colors = DatePickerDefaults.colors(containerColor = Color.White)
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    headlineContentColor = PrimaryColor,
                    todayContentColor = PrimaryColor,
                    todayDateBorderColor = PrimaryColor,
                    selectedDayContainerColor = PrimaryColor,
                    selectedDayContentColor = Color.White,
                    dayContentColor = Color.Black,
                    containerColor = Color.White
                )
            )
        }
    }
}