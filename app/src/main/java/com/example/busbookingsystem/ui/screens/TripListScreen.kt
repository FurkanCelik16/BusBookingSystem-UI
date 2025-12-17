package com.example.busbookingsystem.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.busbookingsystem.data.api.RetrofitClient
import com.example.busbookingsystem.data.model.trip.TripDto
import com.example.busbookingsystem.ui.common.TripCard
import com.example.busbookingsystem.ui.theme.AppFonts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripListScreen(
    originId: Int,
    destinationId: Int,
    date: String,
    onNavigateBack: () -> Unit,
    originDistrictId: Int?,
    destinationDistrictId: Int?,
    onTripSelected: (Int, Double, String, String, String, String, String, String,String?, String?) -> Unit
) {
    var trips by remember { mutableStateOf<List<TripDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    var sortOption by remember { mutableStateOf("TIME") }
    var selectedTimeFilter by remember { mutableStateOf<String?>(null) }
    var selectedCompany by remember { mutableStateOf<String?>(null) }

    var isCompanyMenuExpanded by remember { mutableStateOf(false) }

    val PrimaryColor = Color(0xFF1E88E5)
    val BackgroundColor = Color(0xFFF5F5F5)

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.api.searchTrips(
                originId,
                originDistrictId,
                destinationId,
                destinationDistrictId,
                date
            )
            if (response.success && response.data != null) {
                trips = response.data
            } else {
                errorMessage = response.message ?: "Sefer bulunamadı."
            }
        } catch (e: Exception) {
            errorMessage = "Hata: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    val availableCompanies = remember(trips) {
        trips.mapNotNull { it.companyName }.distinct().sorted()
    }

    val processedTrips = remember(trips, sortOption, selectedTimeFilter, selectedCompany) {
        var result = trips

        if (selectedTimeFilter != null) {
            result = result.filter { trip ->
                val hour = trip.departureTime?.split(":")?.get(0)?.toIntOrNull() ?: 0
                when (selectedTimeFilter) {
                    "00-06" -> hour in 0..5
                    "06-12" -> hour in 6..11
                    "12-18" -> hour in 12..17
                    "18-24" -> hour in 18..23
                    else -> true
                }
            }
        }
        if (selectedCompany != null) {
            result = result.filter { it.companyName == selectedCompany }
        }

        when (sortOption) {
            "PRICE" -> result.sortedBy { it.price }
            else -> result.sortedBy { it.departureTime }
        }
    }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Sefer Sonuçları", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = AppFonts) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = Color.White)
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
                .background(BackgroundColor)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 12.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp), // Kenar boşluğu
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Saat:", color = Color.Gray, fontSize = 12.sp, fontFamily = AppFonts, fontWeight = FontWeight.Bold)

                    CompactTimeChip("00-06", selectedTimeFilter == "00-06") { selectedTimeFilter = if (selectedTimeFilter == "00-06") null else "00-06" }
                    CompactTimeChip("06-12", selectedTimeFilter == "06-12") { selectedTimeFilter = if (selectedTimeFilter == "06-12") null else "06-12" }
                    CompactTimeChip("12-18", selectedTimeFilter == "12-18") { selectedTimeFilter = if (selectedTimeFilter == "12-18") null else "12-18" }
                    CompactTimeChip("18-24", selectedTimeFilter == "18-24") { selectedTimeFilter = if (selectedTimeFilter == "18-24") null else "18-24" }
                    CompactTimeChip("00-24", selectedTimeFilter == "00-24") { selectedTimeFilter = if (selectedTimeFilter == "00-24") null else "00-24" }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2. SATIR: SIRALAMA VE FİRMA SEÇİMİ
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SortChip(label = "🕒 Saat", isSelected = sortOption == "TIME") { sortOption = "TIME" }
                    SortChip(label = "💰 Fiyat", isSelected = sortOption == "PRICE") { sortOption = "PRICE" }

                    Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.LightGray))

                    Box {
                        Surface(
                            onClick = { isCompanyMenuExpanded = true },
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedCompany != null) PrimaryColor.copy(alpha = 0.1f) else Color.Transparent,
                            border = BorderStroke(1.dp, if (selectedCompany != null) PrimaryColor else Color.LightGray)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedCompany ?: "Firma Seç",
                                    fontSize = 12.sp,
                                    fontFamily = AppFonts,
                                    color = if (selectedCompany != null) PrimaryColor else Color.Gray,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.widthIn(max = 120.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                if (selectedCompany != null) {
                                    Icon(
                                        Icons.Default.Close,
                                        null,
                                        modifier = Modifier.size(14.dp).clickable { selectedCompany = null },
                                        tint = PrimaryColor
                                    )
                                } else {
                                    Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                                }
                            }
                        }

                        DropdownMenu(
                            expanded = isCompanyMenuExpanded,
                            onDismissRequest = { isCompanyMenuExpanded = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Tüm Firmalar", fontFamily = AppFonts,color = Color.Black) },
                                onClick = {
                                    selectedCompany = null
                                    isCompanyMenuExpanded = false
                                }
                            )
                            availableCompanies.forEach { company ->
                                DropdownMenuItem(
                                    text = { Text(company, fontFamily = AppFonts, color = Color.Black) },
                                    onClick = {
                                        selectedCompany = company
                                        isCompanyMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Divider(color = Color.LightGray.copy(alpha = 0.3f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = PrimaryColor)
                } else if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = MaterialTheme.colorScheme.error, fontFamily = AppFonts)
                } else if (processedTrips.isEmpty()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.FilterList, null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Bu kriterlere uygun sefer yok.", color = Color.Gray, fontFamily = AppFonts)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(processedTrips) { trip ->
                            TripCard(
                                trip = trip,
                                onClick = {
                                    onTripSelected(
                                        trip.id,
                                        trip.price,
                                        trip.originCityName ?: "",
                                        trip.destinationCityName ?: "",
                                        trip.departureDate,
                                        trip.departureTime ?: "",
                                        trip.busPlateNumber ?: "",
                                        trip.companyName ?: "",
                                        trip.originDistrictName,
                                        trip.destinationDistrictName
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompactTimeChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val PrimaryColor = Color(0xFF1E88E5)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (isSelected) PrimaryColor else Color.White,
        border = if (isSelected) null else BorderStroke(1.dp, Color.LightGray),
        shadowElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Text(
            text = label,
            // 👇 DAHA AZ PADDING, DAHA KÜÇÜK FONT
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = if (isSelected) Color.White else Color.DarkGray,
            fontSize = 11.sp,
            fontFamily = AppFonts,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun SortChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    val SortColor = Color(0xFFEF6C00)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) SortColor.copy(alpha = 0.1f) else Color.Transparent,
        border = BorderStroke(1.dp, if(isSelected) SortColor else Color.LightGray)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            color = if (isSelected) SortColor else Color.Gray,
            fontSize = 12.sp,
            fontFamily = AppFonts,
            fontWeight = FontWeight.Medium
        )
    }
}