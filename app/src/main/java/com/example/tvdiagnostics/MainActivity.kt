package com.example.tvdiagnostics

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.tvdiagnostics.ui.theme.TVDiagnosticsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TVDiagnosticsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape
                ) {
                    SettingsHubScreen()
                }
            }
        }
    }
}

@Composable
fun SettingsHubScreen() {
    val context = LocalContext.current
    val diagnosticsManager = remember { DiagnosticsManager(context) }
    var diagnosticInfo by remember { mutableStateOf(diagnosticsManager.getDiagnostics()) }

    Box(modifier = Modifier.fillMaxSize()) {
        // LAYER 1: Carbon Fiber Background
        Image(
            painter = painterResource(id = R.drawable.bg_carbon),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // LAYER 2: Main Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp, vertical = 8.dp) // Tightened vertical padding
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Centered Logo Plate at 40%
            Image(
                painter = painterResource(id = R.drawable.fg_logo),
                contentDescription = "Creepy Bits Logo",
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .padding(top = 12.dp, bottom = 4.dp)
                    .wrapContentHeight(),
                contentScale = ContentScale.Fit
            )

            // Left-aligned Stats Panel
            DiagnosticStatsPanel(diagnosticInfo)

            Spacer(modifier = Modifier.height(8.dp))

            // Run Quick Clean Button
            Button(
                onClick = {
                    val freed = diagnosticsManager.quickClean()
                    Toast.makeText(context, "Cleaned $freed processes", Toast.LENGTH_SHORT).show()
                    diagnosticInfo = diagnosticsManager.getDiagnostics()
                },
                modifier = Modifier.width(300.dp) // Optimized for frame fit
            ) {
                Text("Run Quick Clean", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("System Shortcuts", fontSize = 22.sp, color = Color.White)

            Spacer(modifier = Modifier.height(4.dp))

            // Shortcut Grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                SettingsButton("Dev Options") { startSettingsIntent(context, Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS) }
                Spacer(modifier = Modifier.width(12.dp))
                SettingsButton("Apps List") { startSettingsIntent(context, Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS) }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                SettingsButton("Display") { startSettingsIntent(context, Settings.ACTION_DISPLAY_SETTINGS) }
                Spacer(modifier = Modifier.width(12.dp))
                SettingsButton("Storage") { startSettingsIntent(context, Settings.ACTION_INTERNAL_STORAGE_SETTINGS) }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Visit Link
            Button(
                onClick = {
                    @Suppress("SpellCheckingInspection")
                    val intent = Intent(Intent.ACTION_VIEW, "https://zanno.se".toUri())
                    context.startActivity(intent)
                },
                modifier = Modifier.padding(bottom = 24.dp) // Extra padding for overscan safety
            ) {
                Text("Visit zanno.se", fontSize = 14.sp)
            }
        }
    }
}

// --- HELPER FUNCTIONS ---

private fun startSettingsIntent(context: android.content.Context, action: String) {
    try {
        val intent = Intent(action)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Cannot open settings: $action", Toast.LENGTH_LONG).show()
    }
}

@Composable
fun SettingsButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.width(150.dp) // Slimmer to fit comfortably in frame
    ) {
        Text(text, fontSize = 14.sp)
    }
}

@Composable
fun DiagnosticStatsPanel(info: DiagnosticInfo) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text("System: ${info.model} (${info.manufacturer})", fontSize = 12.sp, color = Color.LightGray)
        Text("RAM: ${info.ramAvailable} / ${info.ramTotal}", fontSize = 16.sp, color = Color.Green)
        Text("Background Processes: ${info.processCount}", fontSize = 16.sp, color = Color.Yellow)
        Text("Android SDK: ${info.sdkVersion} (v${info.androidVersion})", fontSize = 12.sp, color = Color.LightGray)
    }
}