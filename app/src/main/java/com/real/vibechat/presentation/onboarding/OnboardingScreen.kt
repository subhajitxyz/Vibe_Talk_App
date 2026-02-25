package com.real.vibechat.presentation.onboarding

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.real.vibechat.navigation.AppScreen
import com.real.vibechat.presentation.common.ProfileImageSection
import com.real.vibechat.ui.theme.PrimaryColor

@Composable
fun OnboardingScreen(
    modifier: Modifier,
    navController: NavController,
    onboardingViewModel: OnboardingViewModel = hiltViewModel()
) {

    // OnboardScreenOne -> User can upload photo and Name.
    // OnboardScreenTwo -> user can choose Topics (at least 3 and at most 5)

    // OnboardResult -> only for the final screen.
    // OnboardUiState -> It helps to decide which screen user is currently in.

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        when (onboardingViewModel.currentOnboardScreen) {
            OnboardScreenType.OnboardScreenOne -> OnboardScreenOne(modifier.padding(innerPadding), onboardingViewModel)
            OnboardScreenType.OnboardScreenTwo -> OnboardScreenTwo(modifier.padding(innerPadding), onboardingViewModel, navController)
        }
    }

}


@Composable
fun OnboardScreenOne(
    modifier: Modifier,
    onboardingViewModel: OnboardingViewModel
) {

    val scrollState = rememberScrollState()
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            onboardingViewModel.setImageUri(it)
        }
    }

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState).imePadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "Make your Profile",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(50.dp))


            ProfileImageSection(
                selectedImageUri = onboardingViewModel.userImageUri
            ) {
                // select image using activity result api
                imagePickerLauncher.launch("image/*")
            }

            Spacer(Modifier.height(20.dp))


            // User Name Input
            OutlinedTextField(
                value = onboardingViewModel.userName,
                onValueChange = {
                    onboardingViewModel.userName = it
                    onboardingViewModel.userNameError = null
                                },
                label = { Text("Name") },
                placeholder = { Text("Enter Your Name") },
                isError = onboardingViewModel.userNameError != null,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
            )
            if (onboardingViewModel.userNameError != null) {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp),
                    text = onboardingViewModel.userNameError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(50.dp))
        }

        // Button in bottom right end
        Button(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .wrapContentWidth()
                .padding(horizontal = 40.dp, vertical = 40.dp),

            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
            onClick = {
                onboardingViewModel.moveToOnboardingScreenTwo()
            }
        ) {
            Text("NEXT")
        }
    }

}


@Composable
fun OnboardScreenTwo(
    modifier: Modifier = Modifier,
    onboardingViewModel: OnboardingViewModel,
    navController: NavController
) {
    val onboardResult by onboardingViewModel.userOnboardResult.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(onboardResult) {
        when(onboardResult) {
            OnboardResult.Success -> {
                navController.navigate(AppScreen.MainScreen.route) {
                    popUpTo(0) {inclusive = true}
                }
            }

            is OnboardResult.Error -> {
                Toast.makeText(context, (onboardResult as OnboardResult.Error).e, Toast.LENGTH_SHORT).show()
            }

            else -> Unit
        }

    }

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Choose Your Favourite",
                textAlign = TextAlign.Center,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            ShakingText(
                text = "Choose at least 3 options",
                isError = onboardingViewModel.selectedItemChoiceError
            )


            Spacer(modifier = Modifier.height(24.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                itemsIndexed(onboardingViewModel.predefinedChoiceList) { index, item ->

                    val isSelected = onboardingViewModel.isItemSelected(index)

                    FavouriteItemTile(
                        item = item,
                        isSelected = isSelected,
                        onClick = {
                            if (isSelected) {
                                onboardingViewModel.unSelectItem(index)
                            } else {
                                onboardingViewModel.selectItem(index)
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            if(onboardResult == OnboardResult.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = PrimaryColor,
                    strokeWidth = 2.dp
                )
            }

        }



        // Button in bottom right end
        Button(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),

            onClick = {
                onboardingViewModel.onboardUserProfile()
            }
        ) {
            Text("NEXT", modifier = Modifier.padding(horizontal = 10.dp))
        }

    }


}


@Composable
fun ShakingText(
    text: String,
    isError: Boolean,
    modifier: Modifier = Modifier
) {

    val shake = remember { Animatable(0f) }

    LaunchedEffect(isError) {
        if (isError) {
            shake.snapTo(0f)

            repeat(6) {
                shake.animateTo(
                    targetValue = if (it % 2 == 0) -10f else 10f,
                    animationSpec = tween(50)
                )
            }

            shake.animateTo(
                targetValue = 0f,
                animationSpec = tween(50)
            )
        }
    }

    Text(
        text = text,
        modifier = modifier.graphicsLayer {
            translationX = shake.value
        },
        style = MaterialTheme.typography.bodyMedium,
        color = if (isError) Color.Red else Color.Gray
    )
}




@Composable
fun FavouriteItemTile(
    item: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .aspectRatio(1f) // makes it square
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
    ) {

        Text(
            text = item,
            modifier = Modifier.align(Alignment.Center).padding(5.dp),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Green transparent overlay when selected
        if (isSelected) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Color.Green.copy(alpha = 0.35f)
                    )
            )
        }
    }
}
