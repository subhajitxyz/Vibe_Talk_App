package com.real.vibechat.presentation.auth

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import com.real.vibechat.navigation.AuthScreen
import com.real.vibechat.ui.theme.PrimaryColor

@Composable
fun AuthPhoneNumberScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authPhoneNumViewModel: AuthSharedViewModel
) {

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

        val context = LocalContext.current
        val activity = context as Activity
        val scrollState = rememberScrollState()

        val authState by authPhoneNumViewModel.authState.collectAsStateWithLifecycle()

        LaunchedEffect(authState) {
            when(authState) {
                AuthResult.OtpSent -> {
                    navController.navigate(AuthScreen.AuthOtp.route) {
                        popUpTo(0) {inclusive = true}
                    }
                }
                is AuthResult.Error -> {
                    Toast.makeText(context, (authState as AuthResult.Error).e, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }

        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(horizontal = 30.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                modifier = Modifier.size(150.dp),
                painter = painterResource(R.drawable.phone_icon),
                contentDescription = "phone icon"
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "Hey What's your number?",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "Enter your phone number below and get sms pin to activate your account"
            )

            Spacer(modifier = Modifier.height(10.dp))

            //Phone Number Input
            OutlinedTextField(
                value = authPhoneNumViewModel.phNumber,
                onValueChange = { authPhoneNumViewModel.phNumber = it },
                label = { Text("Phone Number") },
                placeholder = { Text("Enter Your Phone Number") },
                isError = authPhoneNumViewModel.PhNumberError != null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            if (authPhoneNumViewModel.PhNumberError != null) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = authPhoneNumViewModel.PhNumberError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Register button
            Button(
                modifier = Modifier
                    .wrapContentWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                onClick = {
                    authPhoneNumViewModel.sendOtp(activity)
                }
            ) {
                Text(text = "CONTINUE", modifier = Modifier.padding(horizontal = 10.dp))
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
}
