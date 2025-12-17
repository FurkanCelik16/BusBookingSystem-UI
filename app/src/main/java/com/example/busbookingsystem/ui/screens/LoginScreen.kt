package com.example.busbookingsystem.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.busbookingsystem.data.api.RetrofitClient
import com.example.busbookingsystem.data.model.auth.LoginRequest
import com.example.busbookingsystem.ui.common.CustomTextField
import com.example.busbookingsystem.ui.theme.AppFonts
import com.example.busbookingsystem.utils.TokenManager
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun LoginScreen(
    onNavigateToAdmin: () -> Unit,
    onNavigateToUser: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val primaryColor = Color(0xFF1E88E5)
    val backgroundColor = Color(0xFFF0F4F8)
    val busOffsetX = remember { Animatable(-300f) }

    LaunchedEffect(Unit) {
        busOffsetX.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .shadow(16.dp, shape = RoundedCornerShape(bottomStart = 60.dp, bottomEnd = 60.dp))
                .clip(RoundedCornerShape(bottomStart = 60.dp, bottomEnd = 60.dp))
                .background(primaryColor),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Icon(
                    imageVector = Icons.Default.DirectionsBus,
                    contentDescription = "Bus Logo",
                    tint = Color.White,
                    modifier = Modifier
                        .size(100.dp)
                        .offset { IntOffset(busOffsetX.value.roundToInt(), 0) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Güvenli Seyahat",
                    color = Color.White,
                    fontFamily = AppFonts,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
                Text(
                    text = "Hesabınıza Giriş Yapın",
                    color = Color.White.copy(alpha = 0.9f),
                    fontFamily = AppFonts,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                )
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
                Text(
                    text = "Hoş Geldiniz",
                    fontFamily = AppFonts,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.height(24.dp))

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
                        if (email.isBlank() || password.isBlank()) {
                            Toast.makeText(context, "Lütfen alanları doldurun", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isLoading = true
                        scope.launch {
                            try {
                                val request = LoginRequest(email, password)
                                val response = RetrofitClient.authService.login(request)

                                if (response.success && response.data != null) {
                                    val user = response.data

                                    TokenManager.saveToken(user.token)

                                    Toast.makeText(context, "Hoşgeldin ${user.firstName}!", Toast.LENGTH_SHORT).show()

                                    if (user.role == "Admin") onNavigateToAdmin() else onNavigateToUser()
                                } else {
                                    Toast.makeText(context, response.message ?: "Giriş başarısız", Toast.LENGTH_LONG).show()
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
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "GİRİŞ YAP",
                            fontFamily = AppFonts,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .padding(bottom = 32.dp)
                .align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Hesabınız yok mu? ",
                fontFamily = AppFonts,
                color = Color.Gray
            )
            Text(
                text = "Kayıt Ol",
                fontFamily = AppFonts,
                fontWeight = FontWeight.Bold,
                color = primaryColor,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
    }
}