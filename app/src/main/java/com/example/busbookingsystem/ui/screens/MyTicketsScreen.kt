package com.example.busbookingsystem.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WarningAmber
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
import com.example.busbookingsystem.data.model.ticket.TicketDto
import com.example.busbookingsystem.ui.common.CustomTextField
import com.example.busbookingsystem.ui.theme.AppFonts // Fontların olduğu varsayıldı
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTicketsScreen(
    onNavigateBack: () -> Unit
) {
    var tcNo by remember { mutableStateOf("") }
    var tickets by remember { mutableStateOf<List<TicketDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var hasSearched by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val PrimaryColor = Color(0xFF1E88E5)
    val BackgroundColor = Color(0xFFF5F5F5)

    fun cancelMyTicket(ticketId: Int) {
        scope.launch {
            try {
                val response = RetrofitClient.api.cancelTicket(ticketId)
                if (response.success) {
                    Toast.makeText(context, "✅ Biletiniz başarıyla iptal edildi.", Toast.LENGTH_LONG).show()
                    tickets = tickets.filter { it.id != ticketId }
                } else {
                    Toast.makeText(context, "İptal edilemedi: ${response.message}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Biletlerim", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = AppFonts) },
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
                .padding(16.dp)
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Sorgulama Ekranı", fontWeight = FontWeight.Bold, color = PrimaryColor, fontFamily = AppFonts)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.weight(1f)) {
                            CustomTextField(
                                value = tcNo,
                                onValueChange = { if (it.length <= 11) tcNo = it },
                                label = "TC Kimlik Numaranız",
                                icon = Icons.Default.Search,
                                fontFamily = AppFonts
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = {
                                if (tcNo.length != 11) {
                                    Toast.makeText(context, "Lütfen 11 haneli TC giriniz", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                isLoading = true
                                hasSearched = true
                                scope.launch {
                                    try {
                                        val passengerResp = RetrofitClient.api.getPassengerByTc(tcNo)
                                        if (passengerResp.success && passengerResp.data != null) {
                                            val ticketsResp = RetrofitClient.api.getPassengerTickets(passengerResp.data.id)
                                            if (ticketsResp.success && ticketsResp.data != null) {
                                                tickets = ticketsResp.data.filter { it.isPaid || it.isReserved }
                                            } else {
                                                tickets = emptyList()
                                                Toast.makeText(context, "Aktif bilet bulunamadı.", Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            tickets = emptyList()
                                            Toast.makeText(context, "Bu TC ile kayıtlı yolcu bulunamadı.", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                        ) {
                            if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            else Text("ARA", fontWeight = FontWeight.Bold, fontFamily = AppFonts)
                        }
                    }
                }
            }

            if (tickets.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(tickets) { ticket ->
                        PurchasedTicketCard(
                            ticket = ticket,
                            onCancelClick = { cancelMyTicket(ticket.id) }
                        )
                    }
                }
            } else if (hasSearched && !isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Search, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Henüz biletiniz yok veya bulunamadı.", color = Color.Gray, fontFamily = AppFonts)
                    }
                }
            }
        }
    }
}
@Composable
fun PurchasedTicketCard(ticket: TicketDto, onCancelClick: () -> Unit) {
    var showCancelDialog by remember { mutableStateOf(false) }

    val origin = ticket.trip?.originCityName ?: "Bilinmiyor"
    val destination = ticket.trip?.destinationCityName ?: "Bilinmiyor"
    val date = ticket.trip?.departureDate ?: "Tarih Yok"
    val time = ticket.trip?.departureTime?.take(5) ?: "00:00"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DateRange, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("$date • $time", color = Color.Gray, fontSize = 13.sp, fontFamily = AppFonts)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(origin, fontWeight = FontWeight.Bold, fontSize = 18.sp, fontFamily = AppFonts, color = Color.Black)
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color(0xFF1E88E5),
                            modifier = Modifier.padding(horizontal = 8.dp).size(18.dp)
                        )
                        Text(destination, fontWeight = FontWeight.Bold, fontSize = 18.sp, fontFamily = AppFonts, color = Color.Black)
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Koltuk", fontSize = 12.sp, color = Color.Gray, fontFamily = AppFonts)
                    Surface(
                        color = Color(0xFFE3F2FD),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = "${ticket.seatNumber}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            fontFamily = AppFonts
                        )
                    }
                }
            }

            Divider(color = Color.LightGray.copy(alpha = 0.3f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFAFAFA))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val statusColor = if (ticket.isPaid) Color(0xFF2E7D32) else Color(0xFFEF6C00)
                    val statusText = if (ticket.isPaid) "Satın Alındı" else "Rezervasyon"

                    Box(modifier = Modifier.size(8.dp).background(statusColor, RoundedCornerShape(50)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(statusText, fontSize = 13.sp, color = statusColor, fontWeight = FontWeight.Bold, fontFamily = AppFonts)
                }

                TextButton(
                    onClick = { showCancelDialog = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFD32F2F))
                ) {
                    Icon(Icons.Default.HighlightOff, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Bileti İptal Et", fontWeight = FontWeight.Bold, fontFamily = AppFonts)
                }
            }
        }
    }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            containerColor = Color.White,
            icon = { Icon(Icons.Default.WarningAmber, contentDescription = null, tint = Color(0xFFD32F2F)) },
            title = { Text("Bileti İptal Et?", fontFamily = AppFonts, fontWeight = FontWeight.Bold) },
            text = { Text("Bu işlem geri alınamaz. Biletiniz silinecektir.", fontFamily = AppFonts) },
            confirmButton = {
                Button(
                    onClick = {
                        onCancelClick()
                        showCancelDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Evet, İptal Et", fontFamily = AppFonts, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Vazgeç", fontFamily = AppFonts, color = Color.Black)
                }
            }
        )
    }
}