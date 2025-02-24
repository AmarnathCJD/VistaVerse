package com.amarnath.helth

import android.os.Bundle
import android.widget.RatingBar
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amarnath.helth.R // Replace with your actual R package
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material3.Icon as M3Icon
import androidx.compose.material3.IconButton as M3IconButton

@Composable
fun MainScreen() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFFF0F4F3) // Sidebar background color
            ) {
                Spacer(Modifier.height(12.dp))
                NavigationDrawerItem(
                    label = { Text("Home", color = Color(0xFF333333)) }, // Sidebar text color
                    selected = true,
                    onClick = { /*TODO*/ },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color(0xFFE0E7E5), // Selected item background
                        unselectedContainerColor = Color.Transparent
                    )
                )
                NavigationDrawerItem(
                    label = { Text("Profile", color = Color(0xFF333333)) }, // Sidebar text color
                    selected = false,
                    onClick = { /*TODO*/ },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color(0xFFE0E7E5), // Selected item background
                        unselectedContainerColor = Color.Transparent
                    )
                )
                // ...other drawer items
            }
        },
    ) {
        DoctorAppScreen(drawerState = drawerState, scope = scope)
    }
}

var selectedItem = mutableIntStateOf(0)
@Composable
fun DoctorAppScreen(drawerState: DrawerState, scope: CoroutineScope) {

    val navItems = listOf("Home", "Map", "Profile")
    val navIcons = listOf(Icons.Filled.Home, Icons.Filled.Map, Icons.Filled.Person) // Bottom nav icons

    MaterialTheme(
        typography = Typography(
            bodyLarge = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 16.sp,
                color = Color(0xFF333333)
            ),
            bodyMedium = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 14.sp,
                color = Color(0xFF333333)
            ),
            bodySmall = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 12.sp,
                color = Color(0xFF333333)
            ),
            titleLarge = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 20.sp,
                color = Color(0xFF333333)
            ),
            titleMedium = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 18.sp,
                color = Color(0xFF333333)
            ),
            titleSmall = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 16.sp,
                color = Color(0xFF333333)
            ),
            labelLarge = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 16.sp,
                color = Color(0xFF333333)
            ),
            labelMedium = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 14.sp,
                color = Color(0xFF333333)
            ),
            labelSmall = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 12.sp,
                color = Color(0xFF333333)
            )
        ),
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = Color(0xFF4CAF50),
            secondary = Color(0xFF8BC34A),
            background = Color(0xFFF9F9F9),
            surface = Color.White,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = Color(0xFF333333),
            onSurface = Color(0xFF333333),
            error = Color(0xFFB00020),
            onError = Color.White
        )
    ) {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFFE0E7E5)
                ) {
                    navItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { M3Icon(navIcons[index], contentDescription = item) },
                            label = { Text(item) },
                            selected = selectedItem.intValue == index,
                            onClick = { selectedItem.intValue = index },
                            colors = NavigationBarItemColors(
                                selectedIconColor = Color(0xFF4CAF50), // Primary color for selected icon
                                unselectedIconColor = Color(0xFF757575), // Medium Gray for unselected icon
                                selectedTextColor = Color(0xFF333333), // Dark gray for selected text
                                unselectedTextColor = Color(0xFF757575), // Medium Gray for unselected text
                                selectedIndicatorColor = Color(0xFFE0E7E5), // Keep indicator same as background for subtle effect
                                disabledIconColor = Color.LightGray, // Light Gray for disabled icon
                                disabledTextColor = Color.LightGray, // Light Gray for disabled text
                            )
                        )
                    }
                }
            }
        ) { paddingValues ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(paddingValues) // Apply padding from Scaffold
                        .padding(horizontal = if (selectedItem.intValue == 0) 16.dp else 0.dp)
                ) {
                    TopBar(drawerState = drawerState, scope = scope)
                    Spacer(modifier = Modifier.height(8.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    when (selectedItem.intValue) { // Content switching based on bottom nav item
                        0 -> HomeScreenContent() // Home screen content
                        5 -> AlertsScreenContent() // Alerts screen content
                        2 -> ProfilePageScreen() // Profile screen content
                        3 -> CamDetectorScreen() // Camera detector screen content
                        4 -> FunctionalSupportChatScreen() // AI Helper screen content
                        1 -> MapViewMain()
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreenContent() {
    Column {
        SearchBar()
        Spacer(modifier = Modifier.height(18.dp))
        CategoryGrid()
        Spacer(modifier = Modifier.height(24.dp))
        NearbyAlertsSection()
    }
}

@Composable
fun AlertsScreenContent() {
    Text("Alerts Screen Content", modifier = Modifier.padding(16.dp))
}

@Composable
fun ProfileScreenContent() {
    Text("Profile Screen Content", modifier = Modifier.padding(16.dp))
}


@Composable
fun TopBar(drawerState: DrawerState, scope: CoroutineScope) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        M3IconButton(onClick = {
            scope.launch {
                drawerState.open()
            }
        }) {
            M3Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color(0xFF494646))
        }
        Text(
            text = "Vista Verse",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Image(
            painter = painterResource(id = R.drawable.account_circle_24dp_e8eaed_fill0_wght400_grad0_opsz24),
            contentDescription = "Profile",
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun SearchBar() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(20.dp)),
        placeholder = {
            Text("Search For Diseases, Doctors, Hospitals...", color = Color.Gray, fontSize = 14.sp)
        },
        leadingIcon = {
            M3Icon(Icons.Filled.Search, contentDescription = "Search", tint = Color.Gray)
        },
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(
            cursorColor = Color.Black,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
        ),
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black, fontSize = 16.sp),
    )
}

@Composable
fun CategoryGrid() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            CategoryCard(
                text = "Community",
                color = Color(0xFFB39DDB),
                iconId = R.drawable.forum_24dp_e8eaed_fill0_wght400_grad0_opsz24
            )
        }
        item {
            CategoryCard(
                text = "EyeCam",
                color = Color(0xFFEF5350),
                iconId = R.drawable.face_2_24dp_e8eaed_fill0_wght400_grad0_opsz24,
                onClick = { selectedItem.intValue = 3 } // Navigate to Camera Detector screen
            )
        }
        item {
            CategoryCard(
                text = "Vista AI",
                color = Color(0xFFFFB74D),
                iconId = R.drawable.smart_toy_24dp_e8eaed_fill0_wght400_grad0_opsz24,
                onClick = { selectedItem.intValue = 4 }
            )
        }
        item {
            CategoryCard(
                text = "Medi Info",
                color = Color(0xFF58945A),
                iconId = R.drawable.emergency_24dp_e8eaed_fill0_wght400_grad0_opsz24
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun CategoryCard(text: String, color: Color, iconId: Int, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .height(110.dp)
            .padding(bottom = 4.dp)
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            color.copy(alpha = 0.9f),
                            color.copy(alpha = 1f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = iconId),
                    contentDescription = text,
                    modifier = Modifier.size(32.dp),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = text,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun NearbyAlertsSection() {
    Column {
        Text(
            text = "Nearby Emergency / Health Alerts",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(nearbyAlerts) { alert ->
                AlertCard(alert = alert)
            }
        }
    }
}

data class HealthAlert(
    val title: String,
    val description: String,
    val location: String,
    val time: String,
    val severity: String,
    val priority: Int,
    val iconResId: Int
)

@Composable
fun AlertCard(alert: HealthAlert) {
    val alertCardColor = when (alert.severity) {
        "High" -> Color(0xFFFDE2E2).copy(alpha = 0.2f) // Even lighter red tint
        "Medium" -> Color(0xFFFFF3CD).copy(alpha = 0.2f) // Even lighter orange tint
        else -> Color(0xFFE3FCEF).copy(alpha = 0.2f) // Even lighter green tint
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(
                width = 0.5.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            ) // Added border
            .shadow(
                elevation = 0.dp,
                shape = RoundedCornerShape(12.dp)
            ), // Shadow removed for cleaner look with border
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = alertCardColor),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth()
        ) {
            Image( // Added Icon at the top
                painter = painterResource(id = alert.iconResId),
                contentDescription = alert.title,
                modifier = Modifier.size(24.dp),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(MaterialTheme.colorScheme.onSurface) // Tint icon with text color
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = alert.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                M3Icon(
                    painter = painterResource(id = R.drawable.star_24dp_e8eaed_fill0_wght400_grad0_opsz24), // Replace with your priority icon
                    contentDescription = "Priority",
                    tint = if (alert.priority > 3) Color.Red else Color.Green,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = alert.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${alert.location} • ${alert.time}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    M3IconButton(onClick = { /*TODO: Implement upvote action*/ }) {
                        M3Icon(
                            Icons.Filled.KeyboardArrowUp,
                            contentDescription = "Upvote",
                            tint = Color.Green,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    M3IconButton(onClick = { /*TODO: Implement downvote action*/ }) {
                        M3Icon(
                            Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Downvote",
                            tint = Color.Red,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}


val nearbyAlerts = listOf(
    HealthAlert(
        title = "Flu Outbreak Alert",
        description = "Increased flu cases reported in the community. Practice good hygiene and consider vaccination.",
        location = "5km away",
        time = "10 mins ago",
        severity = "Medium",
        priority = 3,
        iconResId = R.drawable.sick_24dp_e8eaed_fill0_wght400_grad0_opsz24
    ),
    HealthAlert(
        title = "Nipah Virus Alert",
        description = "Confirmed cases of Nipah virus detected. Avoid consuming raw fruits and maintain hygiene.",
        location = "10km away",
        time = "30 mins ago",
        severity = "High",
        priority = 5,
        iconResId = R.drawable.microbiology_24dp_e8eaed_fill0_wght400_grad0_opsz24
    ),
    HealthAlert(
        title = "H1N1 Influenza Alert",
        description = "H1N1 cases on the rise. Use masks, maintain social distance, and avoid crowded places.",
        location = "8km away",
        time = "20 mins ago",
        severity = "High",
        priority = 5,
        iconResId = R.drawable.masks_24dp_e8eaed_fill0_wght400_grad0_opsz24
    ),
    HealthAlert(
        title = "Dengue Fever Warning",
        description = "Increased mosquito-borne disease cases reported. Avoid stagnant water and use repellents.",
        location = "3km away",
        time = "15 mins ago",
        severity = "High",
        priority = 4,
        iconResId = R.drawable.emoji_nature_24dp_e8eaed_fill0_wght400_grad0_opsz24
    ),
    HealthAlert(
        title = "Water Contamination Alert",
        description = "Contaminated water supply detected. Boil water before consumption to prevent diseases.",
        location = "2km away",
        time = "5 mins ago",
        severity = "Medium",
        priority = 3,
        iconResId = R.drawable.water_drop_24dp_e8eaed_fill0_wght400_grad0_opsz24
    ),
    HealthAlert(
        title = "Heat Stroke Advisory",
        description = "Severe heat wave conditions expected. Stay hydrated and avoid prolonged sun exposure.",
        location = "This location",
        time = "10 mins ago",
        severity = "Medium",
        priority = 3,
        iconResId = R.drawable.emergency_heat_2_24dp_e8eaed_fill0_wght400_grad0_opsz24
    ),
    HealthAlert(
        title = "Earthquake Reported",
        description = "A minor earthquake (magnitude 4.2) was detected nearby. No damage expected.",
        location = "25km away",
        time = "1 hour ago",
        severity = "Low",
        priority = 1,
        iconResId = R.drawable.earthquake_24dp_e8eaed_fill0_wght400_grad0_opsz24
    )
)
