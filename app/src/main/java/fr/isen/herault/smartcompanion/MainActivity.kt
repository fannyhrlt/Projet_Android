package fr.isen.herault.isensmartcompanion


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController


import
fr.isen.herault.smartcompanion.ui.theme.ISENSmartCompanionTheme


import fr.isen.herault.smartcompanion.NavigationGraph
import fr.isen.herault.smartcompanion.BottomNavigationBar


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ISENSmartCompanionTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(navController) }
                ) { innerPadding ->
                    NavigationGraph(navController,
                        Modifier.padding(innerPadding))
                }
            }
        }
    }
}