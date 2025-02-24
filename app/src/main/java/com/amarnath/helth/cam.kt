package com.amarnath.helth

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun CamDetectorScreen() {
    var predictionResult by remember { mutableStateOf("Prediction will be displayed here") }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) } // State for camera selector

    if (ActivityCompat.checkSelfPermission(
            LocalContext.current,
            Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        RequestLocationPermission(
            onPermissionGranted = {},
            onPermissionDenied = {},
            onPermissionsRevoked = {}
        )
        return
    }

    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            surface = Color.White,
            onPrimary = Color.White,
            onSecondary = Color.White,
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Eye Disease Detector",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .border(2.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
            ) {
                val previewView = remember { PreviewView(context) }
                LaunchedEffect(cameraSelector) {
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    imageCapture = ImageCapture.Builder().build()

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture
                        )
                    } catch (e: Exception) {
                        Log.e("CameraPreview", "Binding failed! :(", e)
                    }
                }
                AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else {
                            CameraSelector.DEFAULT_BACK_CAMERA
                        }
                    },
                    shape = RoundedCornerShape(12.dp) // Rounded button
                ) {
                    Icon(Icons.Filled.Cameraswitch, contentDescription = "Swap Camera")
                }

                Spacer(modifier = Modifier.width(20.dp))

                Button(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            imageCapture?.let { capture ->
                                val fileName = "captured_image.jpg"
                                val file = ContextCompat.getExternalFilesDirs(context, null).firstOrNull()?.resolve(fileName)

                                if (file != null) {
                                    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

                                    capture.takePicture(
                                        outputOptions,
                                        ContextCompat.getMainExecutor(context),
                                        object : ImageCapture.OnImageSavedCallback {
                                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                                val capturedBitmap = BitmapFactory.decodeFile(file.absolutePath)
                                                val base64Image = bitmapToBase64(capturedBitmap)

                                                CoroutineScope(Dispatchers.Main).launch {
                                                    predictionResult = sendImageToApi(base64Image, context)
                                                }
                                            }

                                            override fun onError(exception: ImageCaptureException) {
                                                Log.e("CameraCapture", "Image capture failed: ${exception.message}", exception)
                                                predictionResult = "Image capture failed. Please try again."
                                            }
                                        })
                                } else {
                                    predictionResult = "Error saving captured image."
                                }
                            } ?: run {
                                predictionResult = "Camera not initialized properly."
                            }
                        } else {
                            predictionResult = "Camera permission not granted."
                        }
                    },
                    shape = RoundedCornerShape(12.dp) // Rounded button
                ) {
                    Icon(Icons.Filled.Camera, contentDescription = "Capture Image")
                }
            }


            Spacer(modifier = Modifier.height(20.dp))
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = predictionResult,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun bitmapToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}


private val client = OkHttpClient()

private suspend fun sendImageToApi(base64Image: String, context: Context): String = withContext(Dispatchers.IO) {
    return@withContext try {
        val url = "https://8da7-2603-c021-4003-f7ff-f442-94e5-8b2a-cee1.ngrok-free.app/detect"
        val json = JSONObject().apply {
            put("image", base64Image)
        }.toString()

        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                val js = JSONObject(JSONArray(response.body!!.string()).get(0).toString())
                js.optJSONArray("detections")?.let { detections ->
                    if (detections.length() > 0) {
                        detections.getJSONObject(0).optString("class") + " detected with " + detections.getJSONObject(0).optString("confidence") + " confidence."
                    } else {
                        "No disease detected."
                    }
                } ?: run {
                    "No disease detected."
                }
            } else {
                "Processing Please Wait..."
            }
        }
    } catch (e: IOException) {
        "Error: ${e.message}"
    }
}