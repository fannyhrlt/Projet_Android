package fr.isen.herault.smartcompanion




import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import fr.isen.herault.smartcompanion.Event
import fr.isen.herault.smartcompanion.ui.theme.ISENSmartCompanionTheme




class EventDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        // Récupérer l'événement passé en JSON
        val jsonEvent = intent.getStringExtra("event_json")
        val event = Gson().fromJson(jsonEvent, Event::class.java)




        setContent {
            ISENSmartCompanionTheme {
                EventDetailScreen(event, onBackPressed = { finish() })
            }
        }
    }
}




@Composable
fun EventDetailScreen(event: Event?, onBackPressed: () -> Unit) {
    val context = LocalContext.current




    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Bouton de retour
        Button(onClick = { onBackPressed() }, modifier = Modifier.padding(bottom = 16.dp)) {
            Text("Retour aux événements")
        }




        // Détails de l'événement
        event?.let {
            Text(text = it.title, style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Date: ${it.date}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Lieu: ${it.location}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Catégorie: ${it.category}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it.description, style = MaterialTheme.typography.bodyMedium)
        } ?: run {
            Text(text = "Aucun événement trouvé", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
