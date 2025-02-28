package fr.isen.herault.smartcompanion

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(Screens.Home, Screens.Events, Screens.History, Screens.Agenda)
    val icons = listOf(Icons.Filled.Home, Icons.Filled.CalendarToday, Icons.Filled.History, Icons.Filled.Schedule)
    val labels = listOf("Accueil", "Événements", "Historique", "Agenda")

    var selectedItem by remember { mutableStateOf(0) }

    NavigationBar {
        items.forEachIndexed { index, screen ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = labels[index]) },
                label = { Text(labels[index]) },
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    navController.navigate(screen.route)
                }
            )
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier) {
    NavHost(navController, startDestination = Screens.Home.route, modifier = modifier) {
        composable(Screens.Home.route) { MainScreen() }
        composable(Screens.Events.route) { EventsScreen(navController) }
        composable(Screens.History.route) { HistoryScreen() }
        composable(Screens.Agenda.route) { AgendaScreen(navController) } // Ajout de l'Agenda
    }
}
