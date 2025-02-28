package fr.isen.herault.smartcompanion






import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()

            .baseUrl("https://isen-smart-companion-default-rtdb.europe-west1.firebasedatabase.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }


    val retrofitService: RetrofitService by lazy {
        retrofit.create(RetrofitService::class.java)
    }
}