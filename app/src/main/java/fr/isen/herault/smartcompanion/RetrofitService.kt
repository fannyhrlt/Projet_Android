package fr.isen.herault.smartcompanion


import fr.isen.herault.smartcompanion.Event
import retrofit2.Call
import retrofit2.http.GET


interface RetrofitService {
    @GET("events.json")
    fun getEvents(): Call<List<Event>>
}
