package fr.isen.herault.smartcompanion

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import kotlinx.coroutines.launch
import fr.isen.herault.isensmartcompanion.R
import fr.isen.herault.smartcompanion.AppDatabase
import fr.isen.herault.smartcompanion.database.ChatDao

sealed class Screens(val route: String) {
    object Home : Screens("home")
    object Events : Screens("events")
    object History : Screens("history")
}

@Composable
fun MainScreen(chatDao: ChatDao) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var userInput by remember { mutableStateOf(TextFieldValue("")) }

    val apiKey = context.getString(R.string.google_ai_api_key)
    val generativeModel = GenerativeModel("gemini-1.5-flash", apiKey)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F6FA)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White, shape = MaterialTheme.shapes.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = userInput,
                onValueChange = { userInput = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Posez une question...") }
            )

            IconButton(
                onClick = {
                    if (userInput.text.isNotEmpty()) {
                        val question = userInput.text
                        userInput = TextFieldValue("")

                        coroutineScope.launch {
                            val response = generativeModel.generateContent(question)
                            val answer = response.text ?: "Réponse non disponible."
                            chatDao.insertMessage(ChatMessage(question = question, answer = answer))
                        }
                    }
                },
                modifier = Modifier.size(50.dp).background(Color.Red, shape = CircleShape)
            ) {
                Icon(Icons.Filled.ArrowForward, contentDescription = "Envoyer", tint = Color.White)
            }
        }
    }
}

@Composable
fun EventsScreen(navController: NavHostController) {
    val context = LocalContext.current
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        RetrofitInstance.retrofitService.getEvents().enqueue(object : retrofit2.Callback<List<Event>> {
            override fun onResponse(call: retrofit2.Call<List<Event>>, response: retrofit2.Response<List<Event>>) {
                if (response.isSuccessful) {
                    events = response.body() ?: emptyList()
                    isLoading = false
                } else {
                    errorMessage = "Échec du chargement des événements"
                    isLoading = false
                }
            }

            override fun onFailure(call: retrofit2.Call<List<Event>>, t: Throwable) {
                errorMessage = "Erreur : ${t.localizedMessage}"
                isLoading = false
            }
        })
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Événements", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                CircularProgressIndicator()
            }
            errorMessage.isNotEmpty() -> {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
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
                                    val intent = Intent(context, EventDetailActivity::class.java)
                                    intent.putExtra("event_json", gson.toJson(event))
                                    context.startActivity(intent)
                                },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
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
fun HistoryScreen(chatDao: ChatDao) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context).chatDao() }
    val chatHistory = remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
}
