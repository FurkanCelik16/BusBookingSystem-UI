package com.example.busbookingsystem.ui.screens

import com.example.busbookingsystem.ui.theme.AppFonts
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.busbookingsystem.data.api.RetrofitClient
import com.example.busbookingsystem.data.model.auth.RegisterRequest
import com.example.busbookingsystem.ui.common.CustomTextField
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onRegistrationSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val PrimaryColor = Color(0xFF1E88E5)
    val BackgroundColor = Color(0xFFF0F4F8)
    val AppFonts = AppFonts

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .shadow(16.dp, shape = RoundedCornerShape(bottomStart = 60.dp, bottomEnd = 60.dp))
                .clip(RoundedCornerShape(bottomStart = 60.dp, bottomEnd = 60.dp))
                .background(PrimaryColor),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Yeni Hesap Oluştur",
                    color = Color.White,
                    fontFamily = AppFonts,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            }
            IconButton(onClick = onNavigateBack, modifier = Modifier.align(Alignment.TopStart).padding(top = 40.dp, start = 16.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(10.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = "Adınız",
                    icon = Icons.Default.Person,
                    fontFamily = AppFonts
                )
                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = "Soyadınız",
                    icon = Icons.Default.Person,
                    fontFamily = AppFonts
                )
                Spacer(modifier = Modifier.height(16.dp))
                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email Adresi",
                    icon = Icons.Default.Email,
                    fontFamily = AppFonts
                )
                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Şifre",
                    icon = Icons.Default.Lock,
                    isPassword = true,
                    isVisible = passwordVisible,
                    onVisibilityChange = { passwordVisible = !passwordVisible },
                    fontFamily = AppFonts
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank() || firstName.isBlank() || lastName.isBlank()) {
                            Toast.makeText(context, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isLoading = true
                        scope.launch {
                            try {
                                val request = RegisterRequest(email, password, firstName, lastName)
                                val response = RetrofitClient.authService.register(request)

                                if (response.success && response.data != null) {
                                    Toast.makeText(context, "Kayıt Başarılı! Giriş yapabilirsiniz.", Toast.LENGTH_LONG).show()
                                    onRegistrationSuccess()
                                } else {
                                    Toast.makeText(context, response.message ?: "Kayıt Başarısız!", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Bağlantı Hatası: ${e.message}", Toast.LENGTH_LONG).show()
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(text = "HESAP OLUŞTUR", fontFamily = AppFonts, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Zaten hesabınız var mı?",
            modifier = Modifier.padding(bottom = 32.dp)
                .align(Alignment.CenterHorizontally)
                .clickable { onNavigateBack() },
            color = PrimaryColor,
            fontFamily = AppFonts,
            fontWeight = FontWeight.Bold
        )
    }
}