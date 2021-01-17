package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.Deferred

@Dao
interface AsteroidDao {
    @Query("select * from databaseasteroid")
    fun getAsteroidsAsync(): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)

    @Query("DELETE FROM databaseasteroid")
    fun clear()
}

@Dao
interface PictureOfDayDao {
    @Query("select * from databasepictureofday WHERE date = :date")
    fun getPictureOfDayAsync(date: String): LiveData<DatabasePictureOfDay>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg potd: DatabasePictureOfDay)

    @Query("DELETE FROM databasepictureofday")
    fun clear()
}

@Database(entities = [DatabaseAsteroid::class, DatabasePictureOfDay::class], version = 1, exportSchema = false)

abstract class AsteroidsDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
    abstract val pictureOfDayDao: PictureOfDayDao
}

private lateinit var INSTANCE: AsteroidsDatabase

fun getDatabase(context: Context): AsteroidsDatabase {
    synchronized(AsteroidsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AsteroidsDatabase::class.java,
                "asteroids"
            ).build()
        }
    }
    return INSTANCE
}
