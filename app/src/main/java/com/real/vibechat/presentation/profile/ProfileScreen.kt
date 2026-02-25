package com.real.vibechat.presentation.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.real.vibechat.R
import com.real.vibechat.domain.models.toFormattedDate
import com.real.vibechat.navigation.AppScreen
import com.real.vibechat.ui.theme.PrimaryColor
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun ProfileScreen(
    rootNavController: NavController,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {


    val state by profileViewModel.state.collectAsState()
    val context = LocalContext.current

    val scrollState = rememberScrollState()

    // Load once
    LaunchedEffect(Unit) {
        profileViewModel.onIntent(ProfileIntent.LoadProfile)
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            profileViewModel.onIntent(ProfileIntent.ImageChanged(it))
        }
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            profileViewModel.onIntent(ProfileIntent.IntroVideoSelected(it))
        }
    }

    // Collect one-time effects
    LaunchedEffect(Unit) {
        profileViewModel.effect.collect { effect ->
            when (effect) {
                is ProfileEffect.ShowToast -> {
                    Toast.makeText(
                        context,
                        effect.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    if (state.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                modifier = Modifier.size(25.dp),
                strokeWidth = 2.dp
            )
        }
        return
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = 30.dp)
            .verticalScroll(scrollState)
            .imePadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            text = "Your Profile",
            fontSize = 34.sp,
            fontWeight = FontWeight.ExtraBold,
            color = PrimaryColor
        )
        Spacer(Modifier.height(50.dp))

        ProfileImageUpdateSection(
            editedImageUri = state.editedImageUri,
            editedIntroVideoUri = state.editedIntroVideoUri,
            profileImage = state.profile?.imageUrl,
            introVideo = state.profile?.videoUrl,
            onImageEditClick = {
                // select image using activity result api
                imagePickerLauncher.launch("image/*")
            },
            onVideoEditClick = {
                videoPickerLauncher.launch("video/*")
            },
            onClickProfile = { imageUrl, videoUrl ->
                rootNavController.navigate(
                    AppScreen.Story.createRoute(imageUrl, videoUrl)
                )
            }

        )

        Text(
            modifier = Modifier.fillMaxWidth().padding(1.dp),
            textAlign = TextAlign.Center,
            maxLines = 1,
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Name: ")
                }
                append(state.profile?.name ?: "Name")
            }
        )
        Text(
            modifier = Modifier.fillMaxWidth().padding(1.dp),
            textAlign = TextAlign.Center,
            maxLines = 1,
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("About: ")
                }
                append(state.profile?.caption ?: "")
            }
        )

        Text(
            modifier = Modifier.fillMaxWidth().padding(1.dp),
            textAlign = TextAlign.Center,
            maxLines = 1,
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Created At: ")
                }
                append(state.profile?.createdAt?.toFormattedDate()?: "")
            }
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = state.editedName,
            onValueChange = {
                profileViewModel.onIntent(ProfileIntent.NameChanged(it))
            },
            isError = state.editedNameError != null,
            label = { Text("Name") },
            placeholder = { Text("Edit your name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.editedCaption,
            onValueChange = {
                profileViewModel.onIntent(ProfileIntent.CaptionChanged(it))
            },
            isError = state.editedCaptionError != null,
            label = { Text("About") },
            placeholder = { Text("Write about yourself") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(40.dp))


        Button(
            modifier = Modifier
                .wrapContentWidth(),

            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
            onClick = {
                profileViewModel.onIntent(ProfileIntent.SaveProfile)
            }
        ) {
            Text("SAVE")
        }

        Spacer(Modifier.height(20.dp))

        Box(
            Modifier.size(20.dp)
        ) {
            if (state.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            }
        }

        Spacer(Modifier.height(50.dp))
    }
}



@Composable
fun ProfileImageUpdateSection(
    editedImageUri: Uri?,
    profileImage: String?,
    editedIntroVideoUri: Uri?,
    introVideo: String?,
    onImageEditClick: () -> Unit,
    onVideoEditClick: () -> Unit,
    onClickProfile: (String?, String?) -> Unit
) {

    val userImage = editedImageUri
        ?: profileImage

    Box(
        modifier = Modifier.size(150.dp),
        contentAlignment = Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .size(130.dp) // slightly bigger than image
                .clip(CircleShape)
                .border(
                    width = 4.dp,
                    color = if(editedIntroVideoUri != null) {
                        Color.Green
                    } else if(introVideo != null) {
                        Color(0xFFFF1CB3)
                    } else {
                        Color.Gray
                    },
                    shape = CircleShape
                )
                .padding(4.dp) // space between ring & image
                .clickable(
                    onClick = {
                        //play the video if available
                        // first check intro video -> if available in profile
                        // then check if the editedintrovideuri ->
                        // if any vidoe is available show a hilighted ring in profile.
                        onClickProfile(profileImage, introVideo)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(userImage)
                    .crossfade(true)
                    .build(),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.image_placeholder_icon),
                error = painterResource(R.drawable.image_placeholder_icon)
            )
        }


        IconButton(
            onClick = onImageEditClick,
            modifier = Modifier.align(Alignment.BottomEnd).padding(6.dp)
        ) {
            Icon(
                modifier = Modifier.size(50.dp),
                painter = painterResource(id = R.drawable.image_edit_icon),
                contentDescription = "Edit",
                tint = Color.Unspecified
            )
        }

        IconButton(
            onClick = onVideoEditClick,
            modifier = Modifier.align(Alignment.BottomStart).padding(6.dp)
        ) {
            Icon(
                modifier = Modifier.size(50.dp),
                painter = painterResource(id = R.drawable.intro_edit_icon),
                contentDescription = "Intro Video Icon",
                tint = Color.Unspecified
            )
        }
    }
}