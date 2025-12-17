package com.example.busbookingsystem.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.busbookingsystem.data.api.RetrofitClient
import com.example.busbookingsystem.data.model.company.CompanyDto
import com.example.busbookingsystem.data.model.bus.CreateBusDto
import com.example.busbookingsystem.ui.common.CustomTextField
import com.example.busbookingsystem.ui.common.TripDropdownField
import com.example.busbookingsystem.ui.theme.AppFonts
import kotlinx.coroutines.launch
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBusScreen(
    onNavigateBack: () -> Unit
) {
    var plateNumber by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var seatCount by remember { mutableStateOf("") }
    var companies by remember { mutableStateOf<List<CompanyDto>>(emptyList()) }
    var selectedCompany by remember { mutableStateOf<CompanyDto?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val AdminColor = Color(0xFFD32F2F)

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.api.getAllCompanies()
            if (response.success && response.data != null) {
                companies = response.data
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Firmalar yüklenemedi", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Yeni Otobüs Ekle", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = Color.White)
                    }
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

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Default.DirectionsBus, null, tint = AdminColor)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Filoya yeni araç ekliyorsunuz. Firma seçimini doğru yaptığınızdan emin olun.",
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 1. PLAKA
            CustomTextField(
                value = plateNumber,
                onValueChange = { plateNumber = it.uppercase() },
                label = "Otobüs Plakası (Örn: 34 ABC 123)",
                icon = Icons.Default.DirectionsBus,
                fontFamily = AppFonts
            )

            // 2. MARKA
            CustomTextField(
                value = brand,
                onValueChange = { brand = it },
                label = "Marka (Örn: Mercedes)",
                icon = Icons.Default.DirectionsBus,
                fontFamily = AppFonts
            )

            // 3. KOLTUK SAYISI
            CustomTextField(
                value = seatCount,
                onValueChange = { seatCount = it },
                label = "Koltuk Sayısı",
                icon = Icons.Default.EventSeat,
                fontFamily = AppFonts
            )

            // 4. FİRMA SEÇİMİ
            TripDropdownField(
                label = "Hangi Firmaya Ait?",
                options = companies,
                selectedOption = selectedCompany,
                onOptionSelected = { selectedCompany = it },
                itemLabel = { it.name },
                icon = Icons.Default.Business
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val cleanPlate = plateNumber.trim()
                    val cleanBrand = brand.trim()

                    if (cleanPlate.isBlank() || cleanBrand.isBlank() || seatCount.isBlank() || selectedCompany == null) {
                        Toast.makeText(context, "Lütfen tüm alanları doldurun ve Firma seçin!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val seatCountInt = seatCount.toIntOrNull()
                    if (seatCountInt == null || seatCountInt <= 0) {
                        Toast.makeText(context, "Geçersiz koltuk sayısı", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    scope.launch {
                        try {
                            val newBus = CreateBusDto(
                                plateNumber = cleanPlate,
                                brand = cleanBrand,
                                totalSeatCount = seatCountInt,
                                companyId = selectedCompany!!.id
                            )

                            val response = RetrofitClient.api.createBus(newBus)

                            if (response.success) {
                                Toast.makeText(context, "✅ Otobüs Başarıyla Eklendi!", Toast.LENGTH_LONG).show()
                                onNavigateBack()
                            } else {
                                Toast.makeText(context, "Hata: ${response.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                        catch (e: retrofit2.HttpException) {
                            val errorBody = e.response()?.errorBody()?.string()
                            val cleanMessage = parseBackendError(errorBody)

                            Toast.makeText(context, "⚠️ $cleanMessage", Toast.LENGTH_LONG).show()
                        }
                        catch (e: Exception) {
                            Toast.makeText(context, "Bağlantı Hatası: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AdminColor),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("OTOBÜSÜ KAYDET", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
fun parseBackendError(errorBody: String?): String {
    if (errorBody.isNullOrEmpty()) return "Bilinmeyen bir hata oluştu."
    return try {
        val jsonObject = JSONObject(errorBody)
        val errors = jsonObject.optJSONObject("errors")
        if (errors != null) {
            val firstKey = errors.keys().next()
            errors.getJSONArray(firstKey).getString(0)
        } else {
            jsonObject.optString("title", "Sunucu hatası oluştu.")
        }
    } catch (e: Exception) {
        "Hata: $errorBody"
    }
}