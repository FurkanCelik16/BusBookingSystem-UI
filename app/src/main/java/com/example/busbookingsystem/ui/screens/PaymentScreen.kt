package com.example.busbookingsystem.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.busbookingsystem.data.api.RetrofitClient
import com.example.busbookingsystem.data.model.ticket.CompleteReservationDto
import com.example.busbookingsystem.data.model.passenger.CreatePassengerDto
import com.example.busbookingsystem.data.model.ticket.CreateTicketDto
import com.example.busbookingsystem.ui.common.CustomTextField
import com.example.busbookingsystem.ui.theme.AppFonts
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    tripId: Int,
    seatNumber: Int,
    price: Double,
    passengerData: CreatePassengerDto,
    reservationId: Int? = null,
    onNavigateBack: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    var cardHolder by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val PrimaryColor = Color(0xFF1E88E5)

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Güvenli Ödeme", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = AppFonts) },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Yolcu", color = Color.Gray, fontFamily = AppFonts)
                        Text("${passengerData.firstName} ${passengerData.lastName}", fontWeight = FontWeight.Bold, fontFamily = AppFonts)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Koltuk", color = Color.Gray, fontFamily = AppFonts)
                        Surface(color = PrimaryColor, shape = RoundedCornerShape(4.dp)) {
                            Text(" $seatNumber ", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(2.dp), fontFamily = AppFonts)
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("TOPLAM TUTAR", fontWeight = FontWeight.Bold, fontSize = 18.sp, fontFamily = AppFonts)
                        Text("${price}0 ₺", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = PrimaryColor, fontFamily = AppFonts)
                    }
                }
            }

            Text("Kart Bilgileri", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(top = 8.dp), fontFamily = AppFonts)

            Card(
                modifier = Modifier.fillMaxWidth().height(180.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A237E)), // Lacivert Kart
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp).fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.CreditCard, null, tint = Color.White)
                        Text("WorldCard", color = Color.White, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, fontFamily = AppFonts)
                    }
                    Text(
                        text = if (cardNumber.isEmpty()) "**** **** **** ****" else cardNumber.chunked(4).joinToString(" "),
                        color = Color.White,
                        fontSize = 22.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text("CARD HOLDER", color = Color.Gray, fontSize = 10.sp, fontFamily = AppFonts)
                            Text(if (cardHolder.isEmpty()) "AD SOYAD" else cardHolder.uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontFamily = AppFonts)
                        }
                        Column {
                            Text("EXPIRES", color = Color.Gray, fontSize = 10.sp, fontFamily = AppFonts)
                            Text(if (expiryDate.isEmpty()) "MM/YY" else expiryDate, color = Color.White, fontWeight = FontWeight.Bold, fontFamily = AppFonts)
                        }
                    }
                }
            }
            CustomTextField(value = cardHolder, onValueChange = { cardHolder = it }, label = "Kart Üzerindeki İsim", icon = Icons.Default.Person, fontFamily = AppFonts)
            CustomTextField(value = cardNumber, onValueChange = { if (it.length <= 16) cardNumber = it }, label = "Kart Numarası", icon = Icons.Default.CreditCard, fontFamily = AppFonts)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    CustomTextField(value = expiryDate, onValueChange = { if (it.length <= 5) expiryDate = it }, label = "Ay/Yıl (MM/YY)", icon = Icons.Default.DateRange, fontFamily = AppFonts)
                }
                Box(modifier = Modifier.weight(1f)) {
                    CustomTextField(value = cvv, onValueChange = { if (it.length <= 3) cvv = it }, label = "CVV", icon = Icons.Default.Lock, fontFamily = AppFonts)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (cardHolder.isBlank() || cardNumber.length < 16 || expiryDate.length < 5 || cvv.length < 3) {
                        Toast.makeText(context, "Lütfen kart bilgilerini eksiksiz giriniz!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    scope.launch {
                        try {
                            val response = if (reservationId != null) {
                                val dto = CompleteReservationDto(paidAmount = price)
                                RetrofitClient.api.completeReservation(reservationId, dto)
                            } else {
                                val passResponse = RetrofitClient.api.createPassenger(passengerData)
                                if (passResponse.success && passResponse.data != null) {
                                    val ticketDto = CreateTicketDto(
                                        passengerId = passResponse.data.id,
                                        seatNumber = seatNumber,
                                        tripId = tripId,
                                        paidAmount = price
                                    )
                                    RetrofitClient.api.purchaseTicket(tripId, ticketDto)
                                } else {

                                    passResponse
                                }
                            }
                            if (response.success) {
                                Toast.makeText(context, "✅ Ödeme Başarılı! İyi Yolculuklar.", Toast.LENGTH_LONG).show()
                                onPaymentSuccess()
                            } else {
                                Toast.makeText(context, "Hata: ${response.message}", Toast.LENGTH_LONG).show()
                            }

                        } catch (e: HttpException) {
                            val errorBody = e.response()?.errorBody()?.string()
                            var errorMessage = "Bir hata oluştu: ${e.message()}"

                            if (!errorBody.isNullOrEmpty()) {
                                try {
                                    val jsonObject = JSONObject(errorBody)
                                    if (jsonObject.has("message")) {
                                        errorMessage = jsonObject.getString("message")
                                    }
                                } catch (e: Exception) {
                                    errorMessage = errorBody
                                }
                            }

                            Toast.makeText(context, "Hata: $errorMessage", Toast.LENGTH_LONG).show()

                        } catch (e: Exception) {
                            Toast.makeText(context, "Beklenmeyen hata: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White)
                else Text(text = "ÖDE & BİTİR", fontWeight = FontWeight.Bold, fontFamily = AppFonts)
            }
            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("256-bit SSL ile korunmaktadır", color = Color.Gray, fontSize = 12.sp, fontFamily = AppFonts)
            }
        }
    }
}