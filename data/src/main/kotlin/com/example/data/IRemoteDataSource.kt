package com.example.data

import com.example.domain.Movie

interface IRemoteDataSource {
    suspend fun fetchData(): NetworkResult<List<Movie>>
}