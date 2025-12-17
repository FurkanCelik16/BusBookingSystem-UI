package com.example.busbookingsystem.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.busbookingsystem.data.api.RetrofitClient
import com.example.busbookingsystem.data.model.passenger.CreatePassengerDto
import com.example.busbookingsystem.ui.common.CustomTextField // Eski TextField'ın
import com.example.busbookingsystem.ui.theme.AppFonts
import kotlinx.coroutines.launch
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassengerDetailsScreen(
    tripId: Int,
    seatNumber: Int,
    price: Double,
    originName: String,
    destinationName: String,
    originDistrict: String?,
    destinationDistrict: String?,
    date: String,
    time: String,
    busPlateNumber: String,
    onNavigateBack: () -> Unit,
    companyName: String,
    onPassengerDataReady: (CreatePassengerDto, Boolean) -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var tcNo by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Erkek") }

    var isLoading by remember { mutableStateOf(false) }
    var showStatValidationDialog by remember { mutableStateOf(false) }
    var validationErrorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val PrimaryColor = Color(0xFF1E88E5)
    val BackgroundColor = Color(0xFFF5F5F5)

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Yolcu Bilgileri", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryColor)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 16.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            validateAndProceed(
                                context, scope, tripId, seatNumber, firstName, lastName, tcNo, email, phone, gender,
                                setLoading = { isLoading = it },
                                onError = { msg -> validationErrorMessage = msg; showStatValidationDialog = true },
                                onSuccess = { data -> onPassengerDataReady(data, true) }
                            )
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, PrimaryColor),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryColor),
                        enabled = !isLoading
                    ) {
                        Text("Rezervasyon", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            validateAndProceed(
                                context, scope, tripId, seatNumber, firstName, lastName, tcNo, email, phone, gender,
                                setLoading = { isLoading = it },
                                onError = { msg -> validationErrorMessage = msg; showStatValidationDialog = true },
                                onSuccess = { data -> onPassengerDataReady(data, false) }
                            )
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Ödemeye Geç", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            InfoCard(originName, originDistrict,destinationName,destinationDistrict ,date, time, seatNumber, price,busPlateNumber,companyName,)

            Spacer(modifier = Modifier.height(24.dp))

            CustomTextField(value = firstName, onValueChange = { firstName = it }, label = "Ad", icon = Icons.Default.Person, fontFamily = androidx.compose.ui.text.font.FontFamily.Default)
            Spacer(modifier = Modifier.height(10.dp))

            CustomTextField(value = lastName, onValueChange = { lastName = it }, label = "Soyad", icon = Icons.Default.Person, fontFamily = androidx.compose.ui.text.font.FontFamily.Default)
            Spacer(modifier = Modifier.height(10.dp))

            CustomTextField(value = tcNo, onValueChange = { if (it.length <= 11) tcNo = it }, label = "TC Kimlik No", icon = Icons.Default.Badge,fontFamily = androidx.compose.ui.text.font.FontFamily.Default)
            Spacer(modifier = Modifier.height(10.dp))

            CustomTextField(value = email, onValueChange = { email = it }, label = "E-posta", icon = Icons.Default.Email, fontFamily = androidx.compose.ui.text.font.FontFamily.Default)
            Spacer(modifier = Modifier.height(10.dp))

            CustomTextField(value = phone, onValueChange = { if (it.length <= 11) phone = it }, label = "Telefon", icon = Icons.Default.Phone,fontFamily = androidx.compose.ui.text.font.FontFamily.Default)
            Spacer(modifier = Modifier.height(10.dp))

            Text("Cinsiyet", fontWeight = FontWeight.Medium, color = Color.Gray)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GenderButton(text = "Erkek", isSelected = gender == "Erkek", onClick = { gender = "Erkek" })
                GenderButton(text = "Kadın", isSelected = gender == "Kadın", onClick = { gender = "Kadın" })
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        if (showStatValidationDialog) {
            AlertDialog(
                onDismissRequest = { showStatValidationDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, null, tint = Color.Red)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Uyarı", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                },
                text = { Text(validationErrorMessage) },
                confirmButton = {
                    Button(onClick = { showStatValidationDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)) {
                        Text("Tamam")
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
fun RowScope.GenderButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val color = if (isSelected) Color(0xFF1E88E5) else Color.LightGray
    Button(
        onClick = onClick,
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text)
    }
}

@Composable
fun InfoCard(
    origin: String,
    originDistrict: String?,
    dest: String,
    destDistrict: String?,
    date: String,
    time: String,
    seat: Int,
    price: Double,
    plateNumber: String,
    companyName: String
) {
    val originText = if (!originDistrict.isNullOrEmpty()) "$origin ($originDistrict)" else "$origin (Merkez)"
    val destText = if (!destDistrict.isNullOrEmpty()) "$dest ($destDistrict)" else "$dest (Merkez)"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = companyName,
                        color = Color(0xFF1565C0),
                        fontWeight = FontWeight.Bold,
                        fontFamily = AppFonts,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }


                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = date,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = originText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = AppFonts,
                    color = Color(0xFF333333)
                )

                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color(0xFF1E88E5), // Mavi ok
                    modifier = Modifier.padding(horizontal = 8.dp).size(18.dp)
                )

                Text(
                    text = destText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = AppFonts,
                    color = Color(0xFF333333)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color.LightGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Text(" $time", fontSize = 14.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)

                        Spacer(modifier = Modifier.width(12.dp))

                        Icon(Icons.Default.EventSeat, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Text(" No: $seat", fontSize = 14.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DirectionsBus, null, tint = Color.LightGray, modifier = Modifier.size(14.dp))
                        Text(" $plateNumber", fontSize = 12.sp, color = Color.Gray)
                    }
                }

                Text(
                    text = "${price}0 ₺",
                    fontFamily = AppFonts,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color(0xFF1E88E5)
                )
            }
        }
    }
}

fun validateAndProceed(
    context: android.content.Context,
    scope: kotlinx.coroutines.CoroutineScope,
    tripId: Int,
    seatNumber: Int,
    firstName: String,
    lastName: String,
    tcNo: String,
    email: String,
    phone: String,
    gender: String,
    setLoading: (Boolean) -> Unit,
    onError: (String) -> Unit,
    onSuccess: (CreatePassengerDto) -> Unit
) {
    if (firstName.length < 2 || lastName.length < 2) {
        Toast.makeText(context, "Ad ve Soyad eksik!", Toast.LENGTH_SHORT).show()
        return
    }
    if (tcNo.length != 11) {
        Toast.makeText(context, "TC No 11 haneli olmalı!", Toast.LENGTH_SHORT).show()
        return
    }
    setLoading(true)
    scope.launch {
        val genderValue = if (gender == "Erkek") 1 else 2
        try {
            val response = RetrofitClient.api.validateSeatGender(tripId, seatNumber, genderValue)

            if (response.success) {
                val passengerData = CreatePassengerDto(
                    firstName = firstName,
                    lastName = lastName,
                    tcNo = tcNo,
                    email = email,
                    phoneNumber = phone,
                    gender = genderValue,
                    dateOfBirth = "1995-01-01T00:00:00"
                )
                onSuccess(passengerData)
            }
        } catch (e: retrofit2.HttpException) {
            val errorBodyStr = e.response()?.errorBody()?.string()
            var cleanMessage = "Seçiminiz kurallara uygun değil."
            try {
                if (errorBodyStr != null) {
                    val jsonObject = JSONObject(errorBodyStr)
                    if (jsonObject.has("message")) cleanMessage = jsonObject.getString("message")
                }
            } catch (_: Exception) {
                if (errorBodyStr != null) cleanMessage = errorBodyStr
            }
            onError(cleanMessage)
        } catch (e: Exception) {
            onError("Bağlantı hatası: ${e.message}")
        } finally {
            setLoading(false)
        }
    }
}