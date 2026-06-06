package com.harsh.swipey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.harsh.swipey.ui.navigation.SwipeyApp
import com.harsh.swipey.ui.theme.SwipeyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SwipeyTheme {
                SwipeyApp()
            }
        }
    }
}
