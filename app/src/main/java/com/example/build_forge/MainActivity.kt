package com.example.build_forge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------- PANTALLAS DE LA APP ----------
// Por ahora solo la 1 (Bienvenida). Las demás las vamos agregando aquí.
enum class Pantalla { BIENVENIDA, VIDEOJUEGOS }

// ---------- TEMA (oscuro con azul, como en el Figma) ----------
private val Azul = Color(0xFF2F81F7)
private val Fondo = Color(0xFF0D1117)
private val Gris = Color(0xFF8B949E)

@Composable
fun TemaBuildForge(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Azul,
            onPrimary = Color.White,
            background = Fondo,
            surface = Fondo,
            onBackground = Color.White,
            onSurface = Color.White
        ),
        content = content
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TemaBuildForge {
                BuildForgeApp()
            }
        }
    }
}

// Decide qué pantalla se muestra
@Composable
fun BuildForgeApp() {
    var pantalla by remember { mutableStateOf(Pantalla.BIENVENIDA) }

    // Botón "atrás" del celular regresa a la Bienvenida
    BackHandler(enabled = pantalla != Pantalla.BIENVENIDA) {
        pantalla = Pantalla.BIENVENIDA
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        when (pantalla) {
            Pantalla.BIENVENIDA -> PantallaBienvenida(
                onEntrar = { pantalla = Pantalla.VIDEOJUEGOS }
            )
            Pantalla.VIDEOJUEGOS -> PantallaVideojuegos(
                onVolver = { pantalla = Pantalla.BIENVENIDA }
            )
        }
    }
}

// ---------- PANTALLA 1: BIENVENIDA ----------
@Composable
fun PantallaBienvenida(onEntrar: () -> Unit) {
    val esquinas = RoundedCornerShape(28.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "BUILD FORGE",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Azul
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Mejora tus armas y compara cómo cambian sus estadísticas",
            color = Gris,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(48.dp))

        Button(
            onClick = onEntrar,
            shape = esquinas,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Entrar con mi cuenta")
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = onEntrar,
            shape = esquinas,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Continuar con Google")
        }
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onEntrar) {
            Text("Entrar sin cuenta")
        }
    }
}

// ---------- PANTALLA 2: (pendiente, solo es un espacio por ahora) ----------
@Composable
fun PantallaVideojuegos(onVolver: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Elige tu videojuego", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("(esta pantalla es el siguiente paso)", color = Gris)
        Spacer(Modifier.height(24.dp))
        OutlinedButton(
            onClick = onVolver,
            shape = RoundedCornerShape(28.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Volver")
        }
    }
}