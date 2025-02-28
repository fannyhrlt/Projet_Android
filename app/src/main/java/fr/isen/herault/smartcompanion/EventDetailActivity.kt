package fr.isen.herault.smartcompanion

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import fr.isen.herault.smartcompanion.ui.theme.ISENSmartCompanionTheme

class EventDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val jsonEvent = intent.getStringExtra("event_json")
        val event = Gson().fromJson(jsonEvent, Event::class.java)

        setContent {
            ISENSmartCompanionTheme {
                EventDetailScreen(event, this, onBackPressed = { finish() })
            }
        }
    }
}

@Composable
fun EventDetailScreen(event: Event?, context: Context, onBackPressed: () -> Unit) {
    val sharedPreferences = context.getSharedPreferences("EventPrefs", Context.MODE_PRIVATE)
    var isNotificationEnabled by remember { mutableStateOf(sharedPreferences.getBoolean(event?.id ?: "", false)) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = { onBackPressed() }, modifier = Modifier.padding(bottom = 16.dp)) {
            Text("Retour aux événements")
        }

        event?.let {
            Text(text = it.title, style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Date: ${it.date}")
            Text(text = "Lieu: ${it.location}")
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it.description)
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                isNotificationEnabled = !isNotificationEnabled
                sharedPreferences.edit().putBoolean(it.id, isNotificationEnabled).apply()

                if (isNotificationEnabled) {
                    scheduleNotification(context, it)
                }
            }) {
                Text(if (isNotificationEnabled) "Annuler le rappel" else "Activer le rappel")
            }
        } ?: run {
            Text(text = "Aucun événement trouvé")
        }
    }
}

fun scheduleNotification(context: Context, event: Event) {
    createNotificationChannel(context)

    Handler(Looper.getMainLooper()).postDelayed({
        val intent = Intent(context, EventDetailActivity::class.java).apply {
            putExtra("event_json", Gson().toJson(event))
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "event_notifications")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Rappel d'événement")
            .setContentText("Ne manquez pas : ${event.title} à ${event.location}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED
            ) {
                notify(event.getNumericId() ?: 0, notification)
                Log.d("NotificationTest", "Notification envoyée !")
            } else {
                Log.d("NotificationTest", "Permission non accordée !")
            }
        }
    }, 2000) // 10 secondes
}


fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "event_notifications",
            "Event Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications pour rappeler les événements"
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}
