package com.amarnath.helth

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class RoadClosedData(
    val lat: Double,
    val lon: Double,
)

data class RoadBlockedData(
    val line: List<LatLng>,
    val streetName: String,
    val speed: Int,
)

data class RoadData(
    val roadClosed: MutableList<RoadClosedData>,
    val roadBlocked: MutableList<RoadBlockedData>,
)

val roadData = RoadData(
    roadClosed = mutableListOf(),
    roadBlocked = mutableListOf(),
)

val currentLocation = mutableStateOf(LatLng(10.953551, 75.946148))

data class MapAlertData(
    val id: Int,
    val lat: Double,
    val lon: Double,
    val radius: Int,
    val title: String,
    val message: String,
    val severity: String,
    val icon: String,
)

val testAlerts = listOf(
    MapAlertData(
        id = 1,
        lat = 9.5916,  // Kottayam
        lon = 76.5222,
        radius = 10,
        title = "Dengue Fever Alert",
        message = "Increase in dengue cases reported. Use mosquito repellents and avoid stagnant water.",
        severity = "High",
        icon = "dengue"
    ),
    MapAlertData(
        id = 2,
        lat = 9.9312,  // Ernakulam
        lon = 76.2673,
        radius = 10,
        title = "H1N1 Influenza Warning",
        message = "H1N1 cases rising. Avoid crowded places and wear masks.",
        severity = "High",
        icon = "influenza"
    ),
    MapAlertData(
        id = 3,
        lat = 11.2588,  // Kozhikode
        lon = 75.7804,
        radius = 10,
        title = "Nipah Virus Alert",
        message = "Confirmed Nipah virus cases detected. Follow health protocols strictly.",
        severity = "Severe",
        icon = "nipah"
    ),
    MapAlertData(
        id = 4,
        lat = 8.5241,  // Thiruvananthapuram
        lon = 76.9366,
        radius = 10,
        title = "Water Contamination Warning",
        message = "Unsafe drinking water reported in certain areas. Boil water before consumption.",
        severity = "Medium",
        icon = "water"
    ),
    MapAlertData(
        id = 5,
        lat = 10.7867,  // Thrissur
        lon = 76.6548,
        radius = 10,
        title = "Heatwave Advisory",
        message = "Extreme temperatures expected. Stay hydrated and avoid direct sunlight.",
        severity = "Medium",
        icon = "heatwave"
    ),
    MapAlertData(
        id = 6,
        lat = 8.8932,  // Kollam
        lon = 76.6141,
        radius = 10,
        title = "Flu Outbreak",
        message = "Rise in flu cases reported. Wash hands frequently and consider vaccination.",
        severity = "Medium",
        icon = "flu"
    )
)


@SuppressLint("UnrememberedMutableState")
@Composable
fun MapViewMain() {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation.value, 11f)
    }

    if (currentLocation.value.latitude == 10.953551 && currentLocation.value.longitude == 75.946148) {
        val accuracy = Priority.PRIORITY_HIGH_ACCURACY
        val client = LocationServices.getFusedLocationProviderClient(LocalContext.current)
        if (ActivityCompat.checkSelfPermission(
                LocalContext.current,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                LocalContext.current,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            RequestLocationPermission(
                onPermissionGranted = {},
                onPermissionDenied = {},
                onPermissionsRevoked = {}
            )
            return
        }
        client.getCurrentLocation(accuracy, CancellationTokenSource().token)
            .addOnSuccessListener { loc ->
                loc?.let {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(
                        LatLng(it.latitude, it.longitude),
                        11f
                    )
                    currentLocation.value = LatLng(it.latitude, it.longitude)
                }
            }
            .addOnFailureListener {
                currentLocation.value = LatLng(10.953551, 75.946148)
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            InfoPopupBoxWithZIndex()
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMyLocationClick = {
                    println("My Location Clicked")
                },
                properties = MapProperties(
                    isTrafficEnabled = true,
                    isBuildingEnabled = true,
                    isIndoorEnabled = true,
                    isMyLocationEnabled = isPermissionGranted(
                        LocalContext.current, Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    mapType = MapType.NORMAL,
                ),
                uiSettings = MapUiSettings(
                    compassEnabled = true,
                    scrollGesturesEnabled = true,
                    scrollGesturesEnabledDuringRotateOrZoom = true,
                    rotationGesturesEnabled = true,
                    mapToolbarEnabled = false,
                    zoomControlsEnabled = true,
                    zoomGesturesEnabled = true,
                ),
            ) {
                for (road in roadData.roadBlocked) {
                    Polyline(
                        points = road.line,
                        color = Color.Red,
                        width = 20f,
                        jointType = JointType.BEVEL
                    )

                    Marker(
                        position = road.line[0],
                        title = road.streetName,
                        snippet = "Speed: ${road.speed} km/h",
//                        icon = getCustomBitmapDescriptor(
//                            LocalContext.current,
//                            R.drawable.arrow_cool_down_24dp_e8eaed_fill0_wght400_grad0_opsz24,
//                            Color.Red,
//                            170,
//                            170,
//                        ),
//                        anchor = Offset(0.5f, 1f),
                    )
                }
//                for (road in roadData.roadClosed) {
//                    Marker(
//                        state = MarkerState(position = LatLng(road.lat, road.lon)),
//                        title = "Road Closed",
//                        snippet = "Road Closed",
//                        icon = getCustomBitmapDescriptor(
//                            LocalContext.current,
//                            R.drawable.remove_road_24dp_e8eaed_fill0_wght400_grad0_opsz24,
//                            Color.Transparent,
//                            170,
//                            170,
//                        ),
//                        anchor = Offset(0.5f, 1f),
//                    )
//                }

                for (alert in testAlerts) {
                    Circle(
                        center = LatLng(alert.lat, alert.lon),
                        radius = alert.radius.toDouble() * 1000,
                        fillColor = Color(0x170000FF),
                        tag = alert.id.toString(),
                        strokeColor = Color(0x220000FF),
                        strokeWidth = 5f,
                        onClick = {
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                                LatLng(alert.lat, alert.lon),
                                11f
                            )
                        }
                    )

                    if (cameraPositionState.position.zoom > 9) {
                        Marker(
                            position = LatLng(alert.lat, alert.lon),
                            title = alert.title,
                            snippet = alert.message,
                            anchor = Offset(0.5f, 1f),
                            onClick = {
                                locationNameForPopup.value = ""
                                PopupDataObj.value = PopupData(
                                    title = alert.title,
                                    description = alert.message,
                                    location = LatLng(alert.lat, alert.lon),
                                    radius = alert.radius.toDouble(),
                                    type = alert.severity,
                                    bgColor = Color(0xFFE3F2FD),
                                    id = alert.id
                                )
                                popupState.value = true
                                return@Marker true
                            }
                        )
                    }
                }
            }
        }
    }
}

class PopupData(
    val title: String,
    val description: String,
    val location: LatLng,
    val radius: Double,
    val type: String,
    val bgColor: Color = Color.White,
    val id: Int
)

var PopupDataObj = mutableStateOf(
    PopupData(
        title = "-",
        description = "Alert Data isEmpty",
        location = LatLng(10.953551, 75.946148),
        radius = 100.0,
        type = "~",
        bgColor = Color(0xFFE3F2FD),
        id = 0
    )
)

val locationNameForPopup = mutableStateOf("")

val popupState = mutableStateOf(false)

fun getAlertIcon(icon: String): Int {
    return R.drawable.allergy_24dp_e8eaed_fill0_wght400_grad0_opsz24
}

fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371.0
    val latDistance = Math.toRadians(lat2 - lat1)
    val lonDistance = Math.toRadians(lon2 - lon1)
    val a = sin(latDistance / 2) * sin(latDistance / 2) +
            (cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                    sin(lonDistance / 2) * sin(lonDistance / 2))
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return R * c
}

@Composable
fun InfoPopupBoxWithZIndex() {
    val distance = remember { mutableDoubleStateOf(0.0) }
    if (popupState.value) {
        distance.doubleValue = calculateDistance(
            currentLocation.value.latitude,
            currentLocation.value.longitude,
            PopupDataObj.value.location.latitude,
            PopupDataObj.value.location.longitude
        )

        Box(
            modifier = Modifier
                .zIndex(1f)
                .fillMaxSize()
                .padding(bottom = 5.dp)
                .padding(horizontal = 5.dp)
                .clip(RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .background(PopupDataObj.value.bgColor)
                    .clip(RoundedCornerShape(20.dp))
                    .padding(16.dp)
                    .fillMaxWidth(),
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        Text(
                            text = PopupDataObj.value.title,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    if (distance.doubleValue < PopupDataObj.value.radius) Color(
                                        0xFFE57373
                                    ) else Color(
                                        0xFF9CCC65
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                .clickable {
                                    println(
                                        "Update status clicked: ${
                                            currentLocation.value.latitude
                                        }, ${
                                            currentLocation.value.longitude
                                        }"
                                    )
                                }
                        ) {
                            Text(
                                text = "Update Status",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                            )
                        }
                    }

                    Text(
                        text = "Close",
                        color = Color(0xFF05445e),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .clickable { popupState.value = false }
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = PopupDataObj.value.description,
                    color = Color.Black,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(5.dp))

                Row {
                    Column {
                        Row {
                            Text(
                                text = "${PopupDataObj.value.location}",
                                color = Color(0xFF05445e),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Row {
                            Text(
                                text = "${PopupDataObj.value.radius} km",
                                color = Color(0xFF05445e),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = " | ",
                                color = Color(0xFF05445e),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${distance.doubleValue} km away~",
                                color = Color(0xFF05445e),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        if (locationNameForPopup.value != "") {
                            Spacer(modifier = Modifier.height(5.dp))
                            Row {
                                Text(
                                    text = locationNameForPopup.value,
                                    color = Color(0xFF05445e),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestLocationPermission(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    onPermissionsRevoked: () -> Unit
) {
    val permissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.POST_NOTIFICATIONS
        )
    )

    LaunchedEffect(key1 = permissionState) {
        val allPermissionsRevoked =
            permissionState.permissions.size == permissionState.revokedPermissions.size
        val permissionsToRequest = permissionState.permissions.filter {
            !it.hasPermission
        }
        if (permissionsToRequest.isNotEmpty()) permissionState.launchMultiplePermissionRequest()
        if (allPermissionsRevoked) {
            onPermissionsRevoked()
        } else {
            if (permissionState.allPermissionsGranted) {
                onPermissionGranted()
            } else {
                onPermissionDenied()
            }
        }
    }
}

fun isPermissionGranted(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}