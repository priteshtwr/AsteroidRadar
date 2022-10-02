package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// Since we only have one service, this can all go in one file.
// If you add more services, split this to multiple files and make sure to share the retrofit
// object between services.
const val READ_TIME_OUT = 180
const val CONNECTION_TIME_OUT = 180
/**
 * A retrofit service to fetch a asteroid data.
 */
interface AsteroidsService {
    @GET("neo/rest/v1/feed")
     fun getAsteroidsAsync(@Query("api_key") api_key: String): Deferred<String>

    @GET("planetary/apod")
    suspend fun getPictureOfTheDay(@Query("api_key") api_key: String): PictureOfDay
}

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * Main entry point for network access. Call like `Network.devbytes.getPlaylist()`
 */
object Network {
    /**
     * Needed to Control Time out
     */
    private val client = OkHttpClient.Builder()
        .readTimeout(READ_TIME_OUT.toLong(), TimeUnit.SECONDS)
        .connectTimeout(CONNECTION_TIME_OUT.toLong(), TimeUnit.SECONDS)
        .build()
    // Configure retrofit to parse JSON and use coroutines
    private val retrofitString = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(client)
        .build()

    private val retrofitMoshi= Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(client)
        .build()

    val nasaService = retrofitString.create(AsteroidsService::class.java)

    val nasaServiceJson = retrofitMoshi.create(AsteroidsService::class.java)
}