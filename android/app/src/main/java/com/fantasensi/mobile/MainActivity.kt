package com.fantasensi.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.fantasensi.mobile.ui.screens.DashboardScreen
import com.fantasensi.mobile.ui.theme.FantaSensiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FantaSensiTheme {
                DashboardScreen()
            }
        }
    }
}
