package com.amarnath.helth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ProfilePageScreen() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White), // White background for the whole screen
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileHeaderSection()
            ProfileDetailsSection()
        }
    }
}

@Composable
fun ProfileHeaderSection() {
    Box(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(id = R.drawable.test), // Replace with your header background image
            contentDescription = "Profile Background",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp), // Adjust height as needed
            contentScale = ContentScale.Crop
        )
        Image(
            painter = painterResource(id = R.drawable.profile_image_2), // Replace with user profile image
            contentDescription = "User Profile Picture",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .align(Alignment.Center)
                .offset(y = 30.dp), // Adjust offset to position correctly over background
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )
    }
}

@Composable
fun ProfileDetailsSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 40.dp, bottom = 24.dp) // Add padding below header image
    ) {
        Text("Jane Cooper", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("@janecooper", fontSize = 16.sp, color = Color.Gray)
        Text(
            "janecooper@gmail.com",
            fontSize = 14.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(top = 8.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "BLOOD TYPE",
            fontSize = 16.sp,
            color = Color.DarkGray
        )
        Text(
            "O+",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "LOCATION",
            fontSize = 16.sp,
            color = Color.DarkGray
        )
        Text(
            "New York, USA",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "PHONE",
            fontSize = 16.sp,
            color = Color.DarkGray
        )

        Text(
            "+1 234 567 890",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
