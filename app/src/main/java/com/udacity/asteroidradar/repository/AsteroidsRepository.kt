package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.Network
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class AsteroidsRepository(private val database: AsteroidsDatabase) {

    val asteroidsList: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAsteroidsAsync()) {
            it?.asDomainModel()
        }

    val potd: LiveData<PictureOfDay> =
        Transformations.map(database.pictureOfDayDao.getPictureOfDayAsync(getCurrentDate())) {
            it?.asDomainModel()
        }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val responseString =
                Network.asteroids.getAsteroidsAsync(API_KEY, getCurrentDate()).await()
            val responseJson = JSONObject(responseString)
            val asteroidsArray = parseAsteroidsJsonResult(responseJson)
            Timber.i(asteroidsArray.toString())
            database.asteroidDao.insertAll(*asteroidsArray.asDatabaseModel())
        }
    }

    suspend fun refreshPotd() {
        withContext(Dispatchers.IO) {
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
