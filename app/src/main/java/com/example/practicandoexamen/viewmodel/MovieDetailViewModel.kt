package com.example.practicandoexamen.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MovieRepository
import com.example.domain.Movie
import com.example.framework.local.LocalDataSource
import com.example.framework.network.RemoteDataSource
import com.example.framework.network.RetrofitBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieDetailViewModel: ViewModel() {
    sealed class MovieDetailState {
        object Loading : MovieDetailState()
        class Error(val message: String) : MovieDetailState()
        class Successful(val movie: Movie) : MovieDetailState()
    }


    val state : LiveData<MovieDetailState>
        get() = _state
    private val _state = MutableLiveData<MovieDetailState>()


    fun findMovie(context: Context, id: String) {


        viewModelScope.launch(Dispatchers.IO) {
            val movie = MovieRepository(
                RemoteDataSource(
                    RetrofitBuilder
                ),
                LocalDataSource(
                    context
                )
            ).findById(id)
            withContext(Dispatchers.Main) {
                if ( movie == null) {
                    _state.value = MovieDetailState.Error("No se encontro el ${id}")
                } else {
                    _state.value = MovieDetailState.Successful(movie)
                }
            }
        }
    }
}
