package fr.isen.herault.smartcompanion




import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.google.ai.client.generativeai.GenerativeModel


import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.semantics.Role.Companion.Button
import kotlinx.coroutines.launch


import fr.isen.herault.isensmartcompanion.R




sealed class Screens(val route: String) {
    object Home : Screens("home")
    object Events : Screens("events")
    object History : Screens("history")
    object Agenda : Screens("agenda")
}




@Composable
fun MainScreen() {
    AssistantUI()
}




@Composable
fun AssistantUI() {
    val context = LocalContext.current
    val db = remember {
        AppDatabase.getDatabase(context).interactionDao() }
    var userInput by remember { mutableStateOf(TextFieldValue("")) }
    val chatHistory = remember {
        mutableStateOf<List<Interaction>>(emptyList()) }
    val apiKey = context.getString(R.string.google_ai_api_key)
    val generativeModel = GenerativeModel("gemini-1.5-flash",
        apiKey)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            chatHistory.value = db.getAllInteractions()
        }
    }

    fun saveInteraction(question: String, answer: String) {
        coroutineScope.launch {
            val interaction = Interaction(question = question,
                answer = answer)
            db.insertInteraction(interaction)
            chatHistory.value = db.getAllInteractions()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F6FA)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        Text(text = "ISEN", fontSize = 40.sp, fontWeight =
        FontWeight.Bold, color = Color.Red)
        Text(text = "Smart Companion", fontSize = 16.sp, color =
        Color.Gray)
        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            items(chatHistory.value) { interaction ->
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Vous : ${interaction.question}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Gemini : ${interaction.answer}",
                        fontSize = 16.sp,
                        color = Color.Blue
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White, shape =
                MaterialTheme.shapes.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = userInput,
                onValueChange = { userInput = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                placeholder = { Text("Posez une question...") }
            )

            IconButton(
                onClick = {
                    if (userInput.text.isNotEmpty()) {
                        val question = userInput.text
                        userInput = TextFieldValue("")

                        coroutineScope.launch {
                            val response =
                                generativeModel.generateContent(question)
                            val answer = response.text ?: "Réponse non disponible."
                            saveInteraction(question, answer)
                        }
                    }
                },
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.Red, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Envoyer",
                    tint = Color.White
                )
            }
        }
    }
}








@Composable




fun EventsScreen(navController: NavHostController) {
    val context = LocalContext.current
    var events by remember {
        mutableStateOf<List<Event>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }




    LaunchedEffect(Unit) {
        RetrofitInstance.retrofitService.getEvents().enqueue(object
            : retrofit2.Callback<List<Event>> {
            override fun onResponse(call:
                                    retrofit2.Call<List<Event>>, response:
                                    retrofit2.Response<List<Event>>) {
                if (response.isSuccessful) {
                    events = response.body() ?: emptyList()
                    isLoading = false
                } else {
                    errorMessage = "Échec du chargement des événements"
                    isLoading = false
                }
            }




            override fun onFailure(call:
                                   retrofit2.Call<List<Event>>, t: Throwable) {
                errorMessage = "Erreur : ${t.localizedMessage}"
                isLoading = false
            }
        })
    }




    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Événements", fontSize = 24.sp, fontWeight =
        FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))




        when {
            isLoading -> {
                CircularProgressIndicator()
            }
            errorMessage.isNotEmpty() -> {
                Text(text = errorMessage, color =
                MaterialTheme.colorScheme.error)
            }
            else -> {
                LazyColumn {
                    items(events) { event ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    val gson = Gson()
                                    val intent = Intent(context,
                                        EventDetailActivity::class.java)
                                    intent.putExtra("event_json",
                                        gson.toJson(event))
                                    context.startActivity(intent)
                                },
                            colors =
                            CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                        ) {
                            Text(
                                text = event.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}








@Composable
fun HistoryScreen() {
    val context = LocalContext.current
    val db = remember {
        AppDatabase.getDatabase(context).interactionDao() }
    val coroutineScope = rememberCoroutineScope()
    val chatHistory = remember {
        mutableStateOf<List<Interaction>>(emptyList()) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            chatHistory.value = db.getAllInteractions()
        }
    }

    fun deleteInteraction(id: Int) {
        coroutineScope.launch {
            db.deleteInteraction(id)
            chatHistory.value = db.getAllInteractions()
        }
    }

    fun deleteAllHistory() {
        coroutineScope.launch {
            db.deleteAllInteractions()
            chatHistory.value = emptyList()
        }
    }
    composable@
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = { deleteAllHistory() }) {
            Text("Supprimer tout l'historique")
        }

        LazyColumn {
            items(chatHistory.value) { interaction ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            deleteInteraction(interaction.id) }
                ) {
                    Text("${interaction.question} → ${interaction.answer}")
                }
            }
        }
    }
}

@Composable
fun AgendaScreen(navController: NavHostController) {
    val context = LocalContext.current
    var courses by remember { mutableStateOf<List<Course>>(emptyList()) }
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val agendaPrefs = context.getSharedPreferences("AgendaPrefs", Context.MODE_PRIVATE)
        val savedEvents = agendaPrefs.getStringSet("agenda_events", mutableSetOf()) ?: mutableSetOf()
        val loadedEvents = savedEvents.map { Gson().fromJson(it, Event::class.java) }

        courses = listOf(
            Course("Mathématiques", "2025-03-10", "08:30", "Salle 101"),
            Course("Physique", "2025-03-11", "10:00", "Salle 102")
        )

        events = fakeEvents + loadedEvents // Ajoute les événements activés
        isLoading = false
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Agenda", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn {
                items(courses + events) { item ->
                    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        Text(
                            text = when (item) {
                                is Course -> "${item.name} - ${item.date} à ${item.time}"
                                is Event -> "${item.title} - ${item.date} à ${item.location}"
                                else -> ""
                            },
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

data class Course(val name: String, val date: String, val time: String, val location: String)