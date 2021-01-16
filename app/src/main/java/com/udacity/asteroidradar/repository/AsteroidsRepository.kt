package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.Network
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.database.DatabasePictureOfDay
import com.udacity.asteroidradar.database.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class AsteroidsRepository(private val database: AsteroidsDatabase) {

    /**
     * A playlist of videos that can be shown on the screen.
     */
    val asteroidsList: LiveData<List<DatabaseAsteroid>> by lazy { database.asteroidDao.getAsteroids() }
    val potd: LiveData<DatabasePictureOfDay> by lazy { database.pictureOfDayDao.getPictureOfDay(getCurrentDate()) }

    /**
     * Refresh the videos stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     * To actually load the videos for use, observe [asteroidsList]
     */
    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val responseString = Network.asteroids.getAsteroidsAsync(API_KEY, getCurrentDate()).await()
            val responseJson = JSONObject(responseString)
            val asteroidsArray = parseAsteroidsJsonResult(responseJson)
            Timber.i(asteroidsArray.toString())
            database.asteroidDao.insertAll(*asteroidsArray.asDatabaseModel())
            val responseObject: PictureOfDay = Network.potd.getPictureOfDayAsync(API_KEY).await()
            Timber.i(responseObject.toString())
            database.pictureOfDayDao.insert(responseObject.asDatabaseModel())
        }
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val currentTime = calendar.time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        return dateFormat.format(currentTime)
    }
}
