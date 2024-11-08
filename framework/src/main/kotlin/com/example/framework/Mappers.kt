package com.example.framework

import com.example.domain.Movie
import com.example.framework.local.MovieEntity
import com.example.framework.network.MovieRemote

fun MovieRemote.toMovie(id: Int) : Movie {
    return Movie(
        id = id,
        title = title,
        description = description,
        posterPath = posterPath
    )
}
fun MovieEntity.toMovie() : Movie {
    return Movie(
        id = id,
        title = title,
        description = description,
        posterPath = posterPath
    )
}


fun Movie.toMovieEntity(): MovieEntity {
    return MovieEntity(
        id, title, description, posterPath
    )
}
