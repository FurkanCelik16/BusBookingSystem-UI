package com.example.busbookingsystem.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.busbookingsystem.data.api.RetrofitClient
import com.example.busbookingsystem.data.model.bus.BusDto
import com.example.busbookingsystem.data.model.company.CompanyDto
import com.example.busbookingsystem.data.model.location.CityDto
import com.example.busbookingsystem.data.model.location.DistrictDto
import com.example.busbookingsystem.data.model.trip.CreateTripDto
import com.example.busbookingsystem.ui.common.*
import com.example.busbookingsystem.ui.theme.AppFonts
import kotlinx.coroutines.launch
import retrofit2.HttpException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTripScreen(
    onNavigateBack: () -> Unit,
    onTripAdded: () -> Unit
) {
    var cities by remember { mutableStateOf<List<CityDto>>(emptyList()) }
    var companies by remember { mutableStateOf<List<CompanyDto>>(emptyList()) }
    var buses by remember { mutableStateOf<List<BusDto>>(emptyList()) }

    var selectedOrigin by remember { mutableStateOf<CityDto?>(null) }
    var selectedOriginDistrict by remember { mutableStateOf<DistrictDto?>(null) }

    var selectedDestination by remember { mutableStateOf<CityDto?>(null) }
    var selectedDestinationDistrict by remember { mutableStateOf<DistrictDto?>(null) }

    var selectedCompany by remember { mutableStateOf<CompanyDto?>(null) }
    var selectedBus by remember { mutableStateOf<BusDto?>(null) }

    var price by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    var isOriginExpanded by remember { mutableStateOf(false) }
    var isOriginDistExpanded by remember { mutableStateOf(false) }
    var isDestExpanded by remember { mutableStateOf(false) }
    var isDestDistExpanded by remember { mutableStateOf(false) }


    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val AdminColor = Color(0xFFD32F2F)

    LaunchedEffect(Unit) {
        val cityRes = RetrofitClient.api.getCities()
        if (cityRes.success) cities = cityRes.data ?: emptyList()

        val compRes = RetrofitClient.api.getAllCompanies()
        if (compRes.success) companies = compRes.data ?: emptyList()
    }

    LaunchedEffect(selectedCompany) {
        selectedBus = null
        if (selectedCompany != null) {
            val busRes = RetrofitClient.api.getAllBuses()
            if (busRes.success) {
                buses = busRes.data?.filter { it.companyId == selectedCompany!!.id } ?: emptyList()
            }
        } else {
            buses = emptyList()
        }
    }

    // İl değişince ilçeyi sıfırla
    LaunchedEffect(selectedOrigin) { selectedOriginDistrict = null }
    LaunchedEffect(selectedDestination) { selectedDestinationDistrict = null }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Yeni Sefer Ekle", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = AppFonts) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AdminColor)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Güzergah Seçimi", color = AdminColor, fontWeight = FontWeight.Bold, fontFamily = AppFonts)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    CityDropdownField("Kalkış İli", selectedOrigin, cities, isOriginExpanded, { isOriginExpanded = it }, { selectedOrigin = it }, Icons.Default.LocationOn, AdminColor, AppFonts)
                }
                Box(modifier = Modifier.weight(1f)) {
                    DistrictDropdownField("İlçe (Opsiyonel)", selectedOriginDistrict, selectedOrigin?.districts ?: emptyList(), isOriginDistExpanded, { isOriginDistExpanded = it }, { selectedOriginDistrict = it }, selectedOrigin != null, AdminColor, AppFonts)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    CityDropdownField("Varış İli", selectedDestination, cities, isDestExpanded, { isDestExpanded = it }, { selectedDestination = it }, Icons.Default.LocationOn, AdminColor, AppFonts)
                }
                Box(modifier = Modifier.weight(1f)) {
                    DistrictDropdownField("İlçe (Opsiyonel)", selectedDestinationDistrict, selectedDestination?.districts ?: emptyList(), isDestDistExpanded, { isDestDistExpanded = it }, { selectedDestinationDistrict = it }, selectedDestination != null, AdminColor, AppFonts)
                }
            }

            Divider()

            Text("Araç Bilgileri", color = AdminColor, fontWeight = FontWeight.Bold, fontFamily = AppFonts)
            TripDropdownField("Firma Seçiniz", companies, selectedCompany, { selectedCompany = it }, { it.name }, Icons.Default.Business)

            TripDropdownField("Otobüs Seçiniz", buses, selectedBus, { selectedBus = it }, { "${it.plateNumber} (${it.totalSeatCount} Koltuk)" }, Icons.Default.DirectionsBus)

            Divider()

            Text("Zaman ve Fiyat", color = AdminColor, fontWeight = FontWeight.Bold, fontFamily = AppFonts)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    CustomTextField(value = date, onValueChange = { date = it }, label = "Tarih (YYYY-MM-DD)", icon = Icons.Default.CalendarToday, fontFamily = AppFonts)
                }
                Box(modifier = Modifier.weight(1f)) {
                    CustomTextField(value = time, onValueChange = { time = it }, label = "Saat (HH:MM)", icon = Icons.Default.Schedule, fontFamily = AppFonts)
                }
            }

            CustomTextField(value = price, onValueChange = { price = it }, label = "Bilet Fiyatı (TL)", icon = Icons.Default.AttachMoney, fontFamily = AppFonts)

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (selectedOrigin != null && selectedDestination != null && selectedBus != null && selectedCompany != null && date.isNotBlank() && time.isNotBlank() && price.isNotBlank()) {
                        isLoading = true
                        scope.launch {
                            try {
                                val dto = CreateTripDto(
                                    companyId = selectedCompany!!.id,
                                    busId = selectedBus!!.id,
                                    originCityId = selectedOrigin!!.id,
                                    originDistrictId = selectedOriginDistrict?.id,
                                    destinationCityId = selectedDestination!!.id,
                                    destinationDistrictId = selectedDestinationDistrict?.id,
                                    departureDate = date,
                                    departureTime = time,
                                    price = price.toDoubleOrNull() ?: 0.0
                                )

                                val response = RetrofitClient.api.createTrip(dto)

                                if (response.success) {
                                    Toast.makeText(context, "✅ Sefer Başarıyla Oluşturuldu!", Toast.LENGTH_LONG).show()
                                    onTripAdded()
                                } else {
                                    Toast.makeText(context, "Hata: ${response.message}", Toast.LENGTH_LONG).show()
                                }

                            } catch (e: HttpException) {
                                if (e.code() == 400) {
                                    Toast.makeText(
                                        context,
                                        "⚠️ Uyarı: Otobüs belirtilen saatte doludur. Seferler arasında en az 4 saat fark olmalıdır!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(context, "Sunucu Hatası: ${e.message()}", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Bir hata oluştu: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    } else {
                        Toast.makeText(context, "Lütfen tüm alanları doldurunuz.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AdminColor),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White) else Text("SEFERİ OLUŞTUR", fontWeight = FontWeight.Bold, fontFamily = AppFonts)
            }
        }
    }
}