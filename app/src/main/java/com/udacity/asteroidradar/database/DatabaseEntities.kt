package com.udacity.asteroidradar.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay

@Entity
data class DatabaseAsteroid constructor(
    @PrimaryKey
    val id: Long,
    val codename: String,
    val closeApproachDate: String,
    val absoluteMagnitude: Double,
    val estimatedDiameter: Double,
    val relativeVelocity: Double,
    val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
)

fun List<DatabaseAsteroid>.asDomainModel(): List<Asteroid> {
    return map {
        Asteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }
}

fun ArrayList<Asteroid>.asDatabaseModel(): Array<DatabaseAsteroid> {
    return map {
        DatabaseAsteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }.toTypedArray()
}

@Entity
data class DatabasePictureOfDay constructor(
    @PrimaryKey
    val date: String,
    val explanation: String,
    val hdurl: String,
    val mediaType: String,
    val serviceVersion: String,
    val title: String,
    val url: String
)

fun DatabasePictureOfDay.asDomainModel() = PictureOfDay(
    date = date,
    explanation = explanation,
    hdurl = hdurl,
    mediaType = mediaType,
    serviceVersion = serviceVersion,
    title = title,
    url = url)

fun PictureOfDay.asDatabaseModel() = DatabasePictureOfDay(
    date = date,
    explanation = explanation,
    hdurl = hdurl,
    mediaType = mediaType,
    serviceVersion = serviceVersion,
    title = title,
    url = url)