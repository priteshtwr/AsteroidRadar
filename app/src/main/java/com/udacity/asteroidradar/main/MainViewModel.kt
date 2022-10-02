package com.udacity.asteroidradar.main

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.Network
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@RequiresApi(Build.VERSION_CODES.O)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)
    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
      get() { return _pictureOfDay}
    private var dataState = MutableLiveData(OptionState.ALL)
    @RequiresApi(Build.VERSION_CODES.O)
            /**
             * Use Transformations.switchMap when there is interdependency.
             * Suppose one thing changes and you want to update another thing.
             * Here are option state changes and our list live data should also change.
             */
    val asteroids = Transformations.switchMap(dataState) {
        when (it!!) {
            OptionState.WEEK -> asteroidRepository.weekAsteroids
            OptionState.TODAY -> asteroidRepository.todayAsteroids
            else -> asteroidRepository.allAsteroids
        }
    }
    init {
        Timber.i("Init of View Model is called")
        viewModelScope.launch {
            Timber.i("Requesting for Asteroid Data")
             asteroidRepository.refreshVideos()
             getPictureOfDay()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun optionsChanged(optionState: OptionState){
        Timber.i("Options has changed"+optionState)
        dataState.postValue(optionState)

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getPictureOfDay() {
        CoroutineScope(Dispatchers.IO).launch {
                try {
                    Timber.i("Making Request for Picture Of The Day")
                    val pictureOfDay = Network.nasaServiceJson.getPictureOfTheDay(Constants.NASA_KEY)
                    Timber.i("Got result for Picture Of the Day"+pictureOfDay.title)
                    Timber.i("Asteroid size is"+asteroids.value?.size)
                    /**
                     * We can't call .value from a background thread.
                     */
                    _pictureOfDay.postValue(pictureOfDay)
                } catch (err: Exception) {
                    Timber.e(err)
                }
            }
        }
    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

    }
