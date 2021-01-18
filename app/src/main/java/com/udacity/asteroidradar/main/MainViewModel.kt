package com.udacity.asteroidradar.main

import androidx.lifecycle.*
import com.udacity.asteroidradar.AsteroidApplication
import com.udacity.asteroidradar.api.AsteroidService
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.IllegalArgumentException

class MainViewModel(application: AsteroidApplication) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val repository = AsteroidsRepository(database)

    private val asteroidFilter = MutableLiveData(AsteroidService.AsteroidsFilter.SAVED)

    val potd = repository.potd

    val asteroidList = Transformations.switchMap(asteroidFilter) {
        when (it!!) {
            AsteroidService.AsteroidsFilter.WEEKLY -> repository.weeklyAsteroidsList
            AsteroidService.AsteroidsFilter.TODAY -> repository.todaysAsteroidsList
            else -> repository.asteroidsList
        }
    }

    init {
        viewModelScope.launch {
            try {
                repository.refreshPotd()
                repository.refreshAsteroids()
                Timber.i("Successfully refreshed asteroid list.")
            } catch (ex: Exception) {
                Timber.i("Failed to refresh asteroid list.")
            }
        }
    }

    fun setAsteroidFilter(filter: AsteroidService.AsteroidsFilter) {
        asteroidFilter.postValue(filter)
    }

    class Factory(private val app: AsteroidApplication) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}