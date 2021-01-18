package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import retrofit2.HttpException
import timber.log.Timber

class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidsRepository(applicationContext, database)
        return try {
            repository.refreshPotd()
            repository.refreshAsteroids()
            Timber.i("Successfully refreshed asteroid list.")
            Result.success()
        } catch (e: HttpException) {
            Timber.i("Failed to refresh asteroid list.")
            Result.retry()
        }
    }
}
