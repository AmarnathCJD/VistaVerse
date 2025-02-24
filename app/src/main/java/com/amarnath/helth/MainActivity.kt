package com.amarnath.helth

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.amarnath.helth.ui.theme.HelthTheme

class MainActivity : ComponentActivity() {
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private val cameraPermission = Manifest.permission.CAMERA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    Log.d("Camera Permission", "Permission Granted")
                } else {
                    Log.d("Camera Permission", "Permission Denied")
                }
            }

        setContent {
            HelthTheme {
                MainScreen()
            }
        }
    }


    override fun onResume() {
        super.onResume()
        // Check and request camera permission when the activity resumes
        if (!isCameraPermissionGranted()) {
            permissionLauncher.launch(cameraPermission)
        }
    }

    private fun isCameraPermissionGranted(): Boolean =
        ContextCompat.checkSelfPermission(
            this,
            cameraPermission
        ) == PackageManager.PERMISSION_GRANTED
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HelthTheme {
        Greeting("Android")
    }
}