package com.example.build_forge

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import org.mindrot.jbcrypt.BCrypt

// ---------- PANTALLAS ----------
enum class Pantalla { BIENVENIDA, VIDEOJUEGOS, ARMAS, DETALLE, BUILD, MIS_BUILDS }

// ---------- MODEL (datos de ejemplo, luego se cambian por Room) ----------

data class Estadisticas(
    val dano: Int,
    val cadencia: Int,
    val alcance: Int,
    val movilidad: Int,
    val control: Int
)

// "cambio" es cuánto suma o resta la mejora a cada estadística
data class Mejora(val nombre: String, val cambio: Estadisticas)

data class Arma(
    val nombre: String,
    val tipo: String,
    val base: Estadisticas,
    val mejoras: List<Mejora>
)

data class Videojuego(val nombre: String, val armas: List<Arma>)

data class Build(val nombre: String, val arma: Arma, val mejoras: List<Mejora>)

// ---------- DATOS DE EJEMPLO ----------

private val mejorasComunes = listOf(
    Mejora("Cañón largo", Estadisticas(6, 0, 10, -5, 2)),
    Mejora("Acople vertical", Estadisticas(0, 0, 0, -2, 8)),
    Mejora("Empuñadura", Estadisticas(0, 0, 0, 3, 6)),
    Mejora("Mira óptica", Estadisticas(0, 0, 8, -3, 4)),
    Mejora("Cargador extendido", Estadisticas(2, 3, 0, -4, 0)),
    Mejora("Culata ligera", Estadisticas(0, 0, -3, 8, -2))
)

val videojuegosEjemplo = listOf(
    Videojuego(
        "Call of Duty",
        listOf(
            Arma("M4", "Asalto", Estadisticas(45, 70, 55, 60, 62), mejorasComunes),
            Arma("AK-47", "Asalto", Estadisticas(58, 60, 60, 50, 48), mejorasComunes),
            Arma("MP5", "SMG", Estadisticas(38, 80, 35, 78, 65), mejorasComunes),
            Arma("Kar98k", "Francotirador", Estadisticas(90, 15, 95, 30, 40), mejorasComunes),
            Arma("Glock 18", "Pistola", Estadisticas(30, 85, 20, 85, 55), mejorasComunes)
        )
    ),
    Videojuego(
        "Warframe",
        listOf(
            Arma("Braton", "Asalto", Estadisticas(40, 72, 50, 65, 58), mejorasComunes),
            Arma("Soma", "Asalto", Estadisticas(35, 90, 55, 55, 40), mejorasComunes),
            Arma("Lato", "Pistola", Estadisticas(28, 60, 30, 80, 70), mejorasComunes),
            Arma("Lanka", "Francotirador", Estadisticas(95, 10, 98, 25, 35), mejorasComunes)
        )
    ),
    Videojuego(
        "Fortnite",
        listOf(
            Arma("Rifle de asalto", "Asalto", Estadisticas(36, 65, 55, 60, 55), mejorasComunes),
            Arma("Escopeta táctica", "Escopeta", Estadisticas(85, 30, 15, 55, 45), mejorasComunes),
            Arma("Pistola", "Pistola", Estadisticas(25, 70, 30, 80, 60), mejorasComunes)
        )
    )
)

// ---------- LÓGICA ----------

// Hace las cuentas: suma los cambios de las mejoras a las estadísticas base
object CalculadoraMejoras {
    fun calcular(base: Estadisticas, mejoras: List<Mejora>): Estadisticas {
        var dano = base.dano
        var cadencia = base.cadencia
        var alcance = base.alcance
        var movilidad = base.movilidad
        var control = base.control
        for (m in mejoras) {
            dano += m.cambio.dano
            cadencia += m.cambio.cadencia
            alcance += m.cambio.alcance
            movilidad += m.cambio.movilidad
            control += m.cambio.control
        }
        // Las estadísticas se quedan entre 0 y 100
        return Estadisticas(
            dano.coerceIn(0, 100),
            cadencia.coerceIn(0, 100),
            alcance.coerceIn(0, 100),
            movilidad.coerceIn(0, 100),
            control.coerceIn(0, 100)
        )
    }
}

// ---------- VIEWMODEL ----------

class BuildViewModel : ViewModel() {

    var pantalla by mutableStateOf(Pantalla.BIENVENIDA)
        private set
    var juego by mutableStateOf<Videojuego?>(null)
        private set
    var arma by mutableStateOf<Arma?>(null)
        private set

    // Mensaje de error del login (null = no hay error)
    var errorLogin by mutableStateOf<String?>(null)
        private set

    val videojuegos: List<Videojuego> = videojuegosEjemplo
    val mejorasElegidas = mutableStateListOf<Mejora>()
    val buildsGuardadas = mutableStateListOf<Build>()

    val maxMejoras = 5

    // Estadísticas finales con las mejoras puestas
    val resultado: Estadisticas?
        get() = arma?.let { CalculadoraMejoras.calcular(it.base, mejorasElegidas) }

    // ---------- BCRYPT ----------
    // Hash bcrypt de la contraseña "123" (se genera al abrir la app)
    private val hashAdmin: String = BCrypt.hashpw("123", BCrypt.gensalt())

    init {
        // "Print" en la terminal: búscalo en Logcat con el filtro BCRYPT
        Log.d("BCRYPT", "Hash de la contraseña: $hashAdmin")
    }

    // Revisa usuario y contraseña. Si están bien, entra a la app
    fun iniciarSesion(usuario: String, contrasena: String) {
        if (usuario == "admin" && BCrypt.checkpw(contrasena, hashAdmin)) {
            errorLogin = null
            pantalla = Pantalla.VIDEOJUEGOS
        } else {
            errorLogin = "Nombre o contraseña equivocada"
        }
    }

    // Quita el aviso rojo cuando empiezan a escribir otra vez
    fun limpiarError() { errorLogin = null }

    fun explorar() { pantalla = Pantalla.VIDEOJUEGOS }

    fun elegirJuego(j: Videojuego) {
        juego = j
        pantalla = Pantalla.ARMAS
    }

    fun elegirArma(a: Arma) {
        arma = a
        pantalla = Pantalla.DETALLE
    }

    fun irAConfigurar() {
        mejorasElegidas.clear()
        pantalla = Pantalla.BUILD
    }

    fun alternarMejora(m: Mejora) {
        if (m in mejorasElegidas) {
            mejorasElegidas.remove(m)
        } else if (mejorasElegidas.size < maxMejoras) {
            mejorasElegidas.add(m)
        }
    }

    fun guardarBuild() {
        val a = arma ?: return
        val numero = buildsGuardadas.size + 1
        buildsGuardadas.add(Build("Mi ${a.nombre} #$numero", a, mejorasElegidas.toList()))
        mejorasElegidas.clear()
        pantalla = Pantalla.MIS_BUILDS
    }

    fun verMisBuilds() { pantalla = Pantalla.MIS_BUILDS }

    // Botón "atrás": regresa a la pantalla anterior
    fun volver() {
        pantalla = when (pantalla) {
            Pantalla.ARMAS -> Pantalla.VIDEOJUEGOS
            Pantalla.DETALLE -> Pantalla.ARMAS
            Pantalla.BUILD -> Pantalla.DETALLE
            Pantalla.MIS_BUILDS -> Pantalla.VIDEOJUEGOS
            Pantalla.VIDEOJUEGOS -> Pantalla.BIENVENIDA
            Pantalla.BIENVENIDA -> Pantalla.BIENVENIDA
        }
    }
}