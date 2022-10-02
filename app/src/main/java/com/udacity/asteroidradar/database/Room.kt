package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface AsteroidDao {
    /**
     * For Option Menu Saved Asteroids
     */
    @Query("SELECT * FROM asteroids ORDER BY closeApproachDate DESC")
    fun getSavedAsteroids(): LiveData<List<DatabaseAsteroid>>

    /**
     * For Today
     */
    @Query("SELECT * FROM asteroids WHERE closeApproachDate = :startDate ORDER BY closeApproachDate DESC")
    fun getAsteroidsDay(startDate: String): LiveData<List<DatabaseAsteroid>>

    /**
     * For Weekly
     */
    @Query("SELECT * FROM asteroids WHERE closeApproachDate BETWEEN :startDate AND :endDate ORDER BY closeApproachDate DESC")
    fun getAsteroidsDate(startDate: String, endDate: String): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)
}

@Database(entities = [DatabaseAsteroid::class], version = 1)
abstract class AsteroidDatabase: RoomDatabase(){
    abstract val asteroidDao: AsteroidDao
}

private lateinit var INSTANCE:AsteroidDatabase

fun getDatabase(context: Context): AsteroidDatabase {
    synchronized(AsteroidDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AsteroidDatabase::class.java,
                "videos"
            ).build()
        }
    }
    return INSTANCE
}
