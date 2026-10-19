package com.animeblack.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.animeblack.app.data.repository.FirebaseRepository
import com.animeblack.app.ui.components.CyberButton
import com.animeblack.app.ui.components.GlassCard
import com.animeblack.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    firebaseRepository: FirebaseRepository,
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isRegisterMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(Dimensions.spaceLg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Branding Logo
            Text(
                text = "أنمي بلاك",
                color = CyberBlue,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(Dimensions.spaceXs))
            Text(
                text = if (isRegisterMode) "إنشاء حساب أوتاكو جديد" else "تسجيل الدخول إلى حسابك",
                color = TextSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(Dimensions.space2xl))

            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isRegisterMode) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("الاسم المستعار") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = CyberBlue) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = CyberBlue,
                            unfocusedBorderColor = DarkBorder,
                            focusedLabelColor = CyberBlue,
                            unfocusedLabelColor = TextSecondary
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(Dimensions.spaceMd))
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("البريد الإلكتروني") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = CyberBlue) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = CyberBlue,
                        unfocusedBorderColor = DarkBorder,
                        focusedLabelColor = CyberBlue,
                        unfocusedLabelColor = TextSecondary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(Dimensions.spaceMd))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("كلمة المرور") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CyberBlue) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = TextSecondary
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = CyberBlue,
                        unfocusedBorderColor = DarkBorder,
                        focusedLabelColor = CyberBlue,
                        unfocusedLabelColor = TextSecondary
                    ),
                    singleLine = true
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(Dimensions.spaceMd))
                    Text(
                        text = errorMessage ?: "",
                        color = StatusError,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(Dimensions.spaceXl))

                if (isLoading) {
                    CircularProgressIndicator(
                        color = CyberBlue,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    CyberButton(
                        text = if (isRegisterMode) "إنشاء الحساب" else "تسجيل الدخول",
                        onClick = {
                            if (email.isBlank() || password.isBlank() || (isRegisterMode && name.isBlank())) {
                                errorMessage = "يرجى تعبئة كافة الحقول المطلوبة"
                                return@CyberButton
                            }
                            isLoading = true
                            errorMessage = null
                            coroutineScope.launch {
                                val result = if (isRegisterMode) {
                                    firebaseRepository.signUpWithEmail(email.trim(), password, name.trim())
                                } else {
                                    firebaseRepository.signInWithEmail(email.trim(), password)
                                }
                                isLoading = false
                                result.fold(
                                    onSuccess = { onLoginSuccess() },
                                    onFailure = { errorMessage = it.localizedMessage ?: "حدث خطأ أثناء المصادقة" }
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(Dimensions.spaceMd))

                // Guest Mode / Anonymous Sign In
                OutlinedButton(
                    onClick = {
                        isLoading = true
                        errorMessage = null
                        coroutineScope.launch {
                            val res = firebaseRepository.signInAnonymously()
                            isLoading = false
                            res.fold(
                                onSuccess = { onLoginSuccess() },
                                onFailure = { errorMessage = it.localizedMessage ?: "تعذر الدخول كزائر" }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimensions.buttonHeight),
                    shape = RoundedCornerShape(Dimensions.radiusMd),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = GradCyberBlue)
                ) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = CyberBlue)
                    Spacer(modifier = Modifier.width(Dimensions.spaceSm))
                    Text("المتابعة كزائر أوتاكو", color = CyberBlue, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(Dimensions.spaceMd))

                // Toggle Login / Register
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isRegisterMode) "لديك حساب بالفعل؟ " else "ليس لديك حساب؟ ",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = if (isRegisterMode) "سجّل دخولك الآن" else "أنشئ حساباً جديداً",
                        color = CyberBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.clickable {
                            isRegisterMode = !isRegisterMode
                            errorMessage = null
                        }
                    )
                }
            }
        }
    }
}
