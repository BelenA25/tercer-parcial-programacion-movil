package com.example.practicandoexamen.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MovieRepository
import com.example.data.NetworkResult
import com.example.domain.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    sealed class MovieState {
        object Loading : MovieState()
        class Error( val errorMessage: String? = null): MovieState()
        class Successful(val list: List<Movie> = emptyList()): MovieState()
    }


    private val _state = MutableStateFlow<MovieState>(MovieState.Loading)
    val state: StateFlow<MovieState> = _state


    init {
        fetchData()
    }

    fun fetchData() {


        viewModelScope.launch(Dispatchers.IO) {

            try {
                if (isConexion(context)) {
                    val movies = movieRepository.obtainMovies()
                    withContext(Dispatchers.Main) {
                        _state.value = MovieState.Successful(list = movies)
                    }
                }
                else {
                    when (val result = movieRepository.localDataSource.getList()) {
                        is NetworkResult.Success -> {
                            val movies = result.data
                            withContext(Dispatchers.Main) {
                                _state.value = MovieState.Successful(list = movies)
                            }
                        }
                        is NetworkResult.Error -> {
                            Log.e("MOVIE", "Error fetching movies from local source.")
                            withContext(Dispatchers.Main) {
                                _state.value = MovieState.Error(errorMessage = "Error fetching local movies.")
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                // Handle error state
                Log.e("MOVIE", "Error fetching movies", e)
                withContext(Dispatchers.Main) {
                    _state.value = MovieState.Error( errorMessage = e.message)
                }
            }
        }
    }
    @SuppressLint("ServiceCast")
    private fun isConexion(context: Context): Boolean {
        // register activity with the connectivity manager service
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}
