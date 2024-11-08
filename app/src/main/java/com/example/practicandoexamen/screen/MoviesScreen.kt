package com.example.practicandoexamen.screen

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.domain.Movie
import com.example.practicandoexamen.viewmodel.MovieViewModel

@Composable
fun MoviesScreen( onClick : (String) -> Unit, movieViewModel: MovieViewModel) {
    Scaffold(
        content = {
                paddingValues -> MoviesScreenContent(
            modifier = Modifier.padding(paddingValues),
            onClick = onClick, movieViewModel = movieViewModel)
        }
    )
}


@Composable
fun MoviesScreenContent(modifier: Modifier, onClick: (String) -> Unit, movieViewModel: MovieViewModel) {
    val localContext = LocalContext.current
    Log.d("MOVIESCREEN", "MoviesScreenContent")
    var listOfMovies by remember { mutableStateOf(listOf<Movie>()) }
    val context = LocalContext.current


    //movieViewModel.fetchData()


    val movieState by movieViewModel.state.collectAsStateWithLifecycle()


    when(movieState) {
        is MovieViewModel.MovieState.Loading -> {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = modifier.fillMaxSize()
                ){
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(16.dp))
                }

            }
        }
        is MovieViewModel.MovieState.Error -> {
            Toast.makeText(context, "Error ${(movieState as MovieViewModel.MovieState.Error).errorMessage}", Toast.LENGTH_SHORT).show()
        }
        is MovieViewModel.MovieState.Successful -> {
            listOfMovies = (movieState as MovieViewModel.MovieState.Successful).list
        }
    }


    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Peliculas Populares",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )
        Button(
            onClick = {
                if(isConexion(localContext)) {
                    Toast.makeText(localContext, "Tiene acceso a internet", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(localContext, "No tiene acceso a internet", Toast.LENGTH_LONG).show()
                }

            }
        ) {
            Text(
                text = "Verificar Conexion a Internet"
            )
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier) {
            items(listOfMovies.size) {
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    onClick = {
                        onClick(listOfMovies[it].id.toString())
                    }
                ) {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w500${listOfMovies[it].posterPath}", // URL dinámica usando posterPath
                        contentDescription = listOfMovies[it].title,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                    Text(
                        text = "${listOfMovies[it].title}",
                        modifier = Modifier
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@SuppressLint("ServiceCast")
fun isConexion(context: Context): Boolean {
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
