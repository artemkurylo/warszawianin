package pl.warszawianin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import pl.warszawianin.navigation.WarszawianinNavHost
import pl.warszawianin.ui.theme.WarszawianinTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WarszawianinTheme {
                WarszawianinNavHost()
            }
        }
    }
}
