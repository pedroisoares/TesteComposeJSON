package com.example.testecomposejson

import android.content.res.AssetManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStream


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val movies: List<Movie> = readMoviesFromJson(assets)
        setContent {
            val navController = rememberNavController()
            NavHost(navController, startDestination = "MyScreen6") {
                composable("MyScreen6") { MyScreen6(navController, movies) }
                composable("DetailScreen/{title}") { backStackEntry ->
                    val title = backStackEntry.arguments?.getString("title")
                    val movie = findMovieByTitle(title, movies)
                    if(movie != null) {
                        DetailScreen(navController, movie)
                    }
                    else {
                        // Caso o filme não seja encontrado, exiba uma mensagem de erro ou retorno
                        Text(text="Movie not Found")
                    }
                }
            }
        }
    }
}

@Composable
fun MyScreen6(navController: NavController, movies: List<Movie>) {

    // Agrupando os filmes por grupo
    val groupedByGroup = movies.groupBy { it.group }

    LazyColumn {
        groupedByGroup.forEach { (group, moviesByGroup) ->
            item {
                Text(
                    text = group,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    modifier = Modifier.padding(8.dp)
                )
            }
            item {
                LazyRow {
                    items(moviesByGroup.size) { index ->
                        MoviePoster(movie = moviesByGroup[index], navController = navController)
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun MovieListItem(movie: Movie, navController: NavController) {
    val imageResourceId = getImageResourceId(movie.imageResource)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("DetailScreen/${movie.title}")
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = imageResourceId),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = movie.title)
            Text(text = movie.group)
            Text(text = movie.synopsis)
        }
    }
}

@Composable
fun DetailScreen(navController: NavController, movie: Movie) {
    val imageResourceId = getImageResourceId(movie.imageResource)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .clickable { navController.popBackStack() }
                    .size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Movie Details", fontWeight = FontWeight.Bold)
        }
        Image(
            painter = painterResource(id = imageResourceId),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Row() {
            Text(text = "Title: ", fontWeight = FontWeight.Bold)
            Text(text = "${movie.title}")
        }
        Row() {
            Text(text = "Group: ", fontWeight = FontWeight.Bold)
            Text(text = "${movie.group}")
        }
        Row() {
            Text(text = "Synopsis: ", fontWeight = FontWeight.Bold)
            Text(text = "${movie.synopsis}")
        }
        Row() {
            Text(text = "Original Title: ", fontWeight = FontWeight.Bold)
            Text(text = "${movie.originalTitle}")
        }
        Row() {
            Text(text = "Genre: ", fontWeight = FontWeight.Bold)
            Text(text = "${movie.genre}")
        }
        Row() {
            Text(text = "Episodes: ", fontWeight = FontWeight.Bold)
            Text(text = "${movie.episodes}")
        }
        Row() {
            Text(text = "Year: ", fontWeight = FontWeight.Bold)
            Text(text = "${movie.year}")
        }
        Row() {
            Text(text = "Country: ", fontWeight = FontWeight.Bold)
            Text(text = "${movie.country}")
        }
        Row() {
            Text(text = "Director: ", fontWeight = FontWeight.Bold)
            Text(text = "${movie.director}")
        }
        Row() {
            Text(text = "Cast: ", fontWeight = FontWeight.Bold)
            Text(text = "${movie.cast.joinToString()}")
        }
        Row() {
            Text(text = "Available Until: ", fontWeight = FontWeight.Bold)
            Text(text = "${movie.availableUntil}")
        }
    }
}

data class Movie(
    val imageResource: String,
    val title: String,
    val group: String,
    val synopsis: String,
    val originalTitle: String,
    val genre: String,
    val episodes: Int,
    val year: Int,
    val country: String,
    val director: String,
    val cast: List<String>,
    val availableUntil: String
)

@Composable
fun MoviePoster(movie: Movie, navController: NavController) {
    val imageResourceId = getImageResourceId(movie.imageResource)
    Column(
        modifier = Modifier.clickable {
            navController.navigate("DetailScreen/${movie.title}")
        }
    ) {
        Image(
            painter = painterResource(id = imageResourceId),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp, 180.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Text(
            text = movie.title,
            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

fun findMovieByTitle(title: String?, movies: List<Movie>): Movie? {
    // Verifica se o título não é nulo
    if (title.isNullOrEmpty()) {
        return null
    }

    // Busca o filme na lista de filmes
    return movies.find { it.title == title }
}

fun readMoviesFromJson(assetManager: AssetManager): List<Movie> {
    val inputStream: InputStream = assetManager.open("movies.json")
    val json = inputStream.bufferedReader().use { it.readText() }
    return Gson().fromJson(json, object : TypeToken<List<Movie>>() {}.type)
}

fun getImageResourceId(imageName: String): Int {
    // Obtenha a classe R.drawable
    val clazz = R.drawable::class.java
    try {
        // Obtenha o ID do campo da imagem pelo seu nome usando reflexão
        val field = clazz.getDeclaredField(imageName)
        // Como os campos são estáticos, passe null como o objeto de instância
        return field.getInt(null)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    // Se não encontrar a imagem, retorne um valor padrão
    return -1
}