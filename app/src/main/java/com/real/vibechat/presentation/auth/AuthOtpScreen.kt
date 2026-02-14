package com.real.vibechat.presentation.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.real.vibechat.R
import com.real.vibechat.navigation.AppScreen
import com.real.vibechat.ui.theme.PrimaryColor

@Composable
fun AuthOtpScreen(
    modifier: Modifier,
    navController: NavController,
    authOtpViewModel: AuthSharedViewModel
) {

    val authState by authOtpViewModel.authState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        when(authState) {
            AuthResult.NewUser -> {
                navController.navigate(AppScreen.Onboarding.route) {
                    popUpTo(0) {inclusive = true}
                }
            }
            AuthResult.ExistingUser -> {
                navController.navigate(AppScreen.HomeScreen.route) {
                    popUpTo(0) {inclusive = true}
                }
            }
            is AuthResult.Error -> {
                Toast.makeText(context, (authState as AuthResult.Error).e, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .padding(horizontal = 30.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            modifier = Modifier.size(150.dp),
            painter = painterResource(R.drawable.otp_icon),
            contentDescription = "phone icon"
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "OTP Verification",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = "We've sent you the verification code in you mobile number."
        )
        Spacer(modifier = Modifier.height(8.dp))

        //Phone Number Input
        OutlinedTextField(
            value = authOtpViewModel.otp,
            onValueChange = { authOtpViewModel.otp = it },
            label = { Text("OTP") },
            placeholder = { Text("Enter Your OTP") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(40.dp))

        Button(
            modifier = Modifier
                .wrapContentWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
            onClick = {
                authOtpViewModel.verifyOtp(authOtpViewModel.otp)
            }
        ) {
            Text(text = "SENT OTP", modifier = Modifier.padding(horizontal = 10.dp))
        }


        Spacer(modifier = Modifier.height(40.dp))

        if(authState == AuthResult.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = PrimaryColor,
                strokeWidth = 2.dp
            )
        }
        Spacer(modifier = Modifier.height(40.dp))

    }

}