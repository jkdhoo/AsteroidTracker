package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.Network
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import com.udacity.asteroidradar.api.getNextSevenDaysFormattedDates

class AsteroidsRepository(private val database: AsteroidsDatabase) {

    private val dateRange = getNextSevenDaysFormattedDates()
    private val startDate = dateRange[0]
    private val endDate = dateRange[6]

    val asteroidsList: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getAsteroidsAsync()) {
        it?.asDomainModel()
    }

    val todaysAsteroidsList: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getTodaysAsteroidsAsync(startDate)) {
        it?.asDomainModel()
    }

    val weeklyAsteroidsList: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getWeeklyAsteroidsAsync(startDate, endDate)) {
        it?.asDomainModel()
    }

    val potd: LiveData<PictureOfDay> =
        Transformations.map(database.pictureOfDayDao.getPictureOfDayAsync(startDate)) {
            it?.asDomainModel()
        }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val responseString =
                Network.asteroids.getAsteroidsAsync(API_KEY, startDate).await()
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
}
