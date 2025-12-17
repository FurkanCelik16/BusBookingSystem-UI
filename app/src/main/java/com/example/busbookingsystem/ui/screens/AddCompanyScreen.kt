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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.busbookingsystem.data.api.RetrofitClient
import com.example.busbookingsystem.data.model.company.CreateCompanyDto
import com.example.busbookingsystem.ui.common.CustomTextField
import com.example.busbookingsystem.ui.theme.AppFonts
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCompanyScreen(
    onNavigateBack: () -> Unit
) {

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val AdminColor = Color(0xFFD32F2F)

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Yeni Firma Ekle", color = Color.White, fontWeight = FontWeight.Bold) },
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
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Default.Business, null, tint = Color(0xFF1565C0))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Buradan sisteme yeni otobüs firmaları (Örn: Kamil Koç, Metro) ekleyebilirsiniz.", fontSize = 13.sp, color = Color.DarkGray)
                }
            }

            CustomTextField(value = name, onValueChange = { name = it }, label = "Firma Adı *", fontFamily = AppFonts, icon = Icons.Default.Business)
            CustomTextField(value = phone, onValueChange = { phone = it }, label = "Telefon", fontFamily = AppFonts, icon = Icons.Default.Phone)
            CustomTextField(value = email, onValueChange = { email = it }, label = "E-Posta", fontFamily = AppFonts,icon = Icons.Default.Email)
            CustomTextField(value = address, onValueChange = { address = it }, label = "Adres", fontFamily = AppFonts, icon = Icons.Default.LocationOn)

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (name.isBlank()) {
                        Toast.makeText(context, "Firma adı zorunludur!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    scope.launch {
                        try {
                            val newCompany = CreateCompanyDto(name, phone, email, address)
                            //backend cevabı
                            val response = RetrofitClient.api.createCompany(newCompany)

                            if (response.success) {
                                Toast.makeText(context, "✅ Firma Eklendi!", Toast.LENGTH_LONG).show()
                                onNavigateBack()
                            } else {
                                Toast.makeText(context, "Hata: ${response.message}", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Hata: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AdminColor),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White) else Text("FİRMAYI KAYDET", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}