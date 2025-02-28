package fr.isen.herault.smartcompanion




import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import fr.isen.herault.smartcompanion.database.ChatDao

val chatViewModel: ChatDao = TODO()

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(Screens.Home, Screens.Events, Screens.History)
    val icons = listOf(Icons.Filled.Home, Icons.Filled.CalendarToday, Icons.Filled.History)
    val labels = listOf("Accueil", "Événements", "Historique")




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
fun NavigationGraph(navController: NavHostController, modifier: Modifier, chatDao: ChatDao) {
    NavHost(navController, startDestination = Screens.Home.route, modifier = modifier) {
        composable(Screens.Home.route) { MainScreen(chatDao) }
        composable(Screens.Events.route) { EventsScreen(navController) }
        composable(Screens.History.route) { HistoryScreen(chatDao) }
        composable("history") {
            HistoryScreen(chatViewModel)
        }

    }
}
