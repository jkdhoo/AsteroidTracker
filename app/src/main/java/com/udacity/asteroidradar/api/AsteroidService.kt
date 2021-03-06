package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.PictureOfDay
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface AsteroidService {

    enum class AsteroidsFilter(val value: String) { WEEKLY("weekly"), TODAY("today"), SAVED("saved") }

    @GET("neo/rest/v1/feed")
    fun getAsteroidsAsync(
        @Query("api_key") apiKey: String,
        @Query("start_date") startDate: String
    ): Deferred<String>

    @GET("planetary/apod")
    fun getPictureOfDayAsync(
        @Query("api_key") apiKey: String
    ): Deferred<PictureOfDay>
}

object Network {

    private val retrofitAsteroids = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val asteroids: AsteroidService = retrofitAsteroids.create(AsteroidService::class.java)

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofitPotd = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val potd: AsteroidService = retrofitPotd.create(AsteroidService::class.java)
}