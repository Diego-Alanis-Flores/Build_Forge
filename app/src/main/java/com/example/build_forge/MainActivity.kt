package com.example.build_forge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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

// ---------- TEMA (oscuro con azul, como en el Figma) ----------
private val Azul = Color(0xFF2F81F7)
private val Fondo = Color(0xFF0D1117)
private val Panel = Color(0xFF161B22)
private val Gris = Color(0xFF8B949E)
private val Esquinas = RoundedCornerShape(28.dp)

@Composable
fun TemaBuildForge(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Azul,
            onPrimary = Color.White,
            background = Fondo,
            surface = Fondo,
            onBackground = Color.White,
            onSurface = Color.White,
            secondaryContainer = Color(0xFF1F3A5F),
            onSecondaryContainer = Color.White
        ),
        content = content
    )
}

class MainActivity : ComponentActivity() {

    private val vm: BuildViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TemaBuildForge {
                BuildForgeApp(vm)
            }
        }
    }
}

// Decide qué pantalla mostrar según el estado del ViewModel
@Composable
fun BuildForgeApp(vm: BuildViewModel) {
    // El botón "atrás" del celular regresa a la pantalla anterior
    BackHandler(enabled = vm.pantalla != Pantalla.BIENVENIDA) { vm.volver() }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        if (vm.pantalla == Pantalla.BIENVENIDA) {
            PantallaBienvenida(onEntrar = { vm.entrar() })
        } else {
            Scaffold(
                containerColor = Fondo,
                bottomBar = { BarraNavegacion(vm) }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    when (vm.pantalla) {
                        Pantalla.VIDEOJUEGOS -> PantallaVideojuegos(vm)
                        Pantalla.ARMAS -> PantallaArmas(vm)
                        Pantalla.DETALLE -> PantallaDetalle(vm)
                        Pantalla.BUILD -> PantallaBuild(vm)
                        Pantalla.MIS_BUILDS -> PantallaMisBuilds(vm)
                        Pantalla.BIENVENIDA -> {}
                    }
                }
            }
        }
    }
}

// ---------- PANTALLA 1: BIENVENIDA ----------
@Composable
fun PantallaBienvenida(onEntrar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("BUILD FORGE", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Azul)
        Spacer(Modifier.height(8.dp))
        Text(
            "Mejora tus armas y compara cómo cambian sus estadísticas",
            color = Gris,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(48.dp))
        Button(
            onClick = onEntrar,
            shape = Esquinas,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text("Entrar con mi cuenta") }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = onEntrar,
            shape = Esquinas,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) { Text("Continuar con Google") }
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onEntrar) { Text("Entrar sin cuenta") }
    }
}

// ---------- PANTALLA 2: ELIGE TU VIDEOJUEGO (01/03) ----------
@Composable
fun ColumnScope.PantallaVideojuegos(vm: BuildViewModel) {
    var busqueda by remember { mutableStateOf("") }

    Text("01/03", color = Gris)
    Text("Elige tu videojuego", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(12.dp))
    OutlinedTextField(
        value = busqueda,
        onValueChange = { busqueda = it },
        label = { Text("Buscar videojuego") },
        singleLine = true,
        shape = Esquinas,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(12.dp))

    val lista = vm.videojuegos.filter { it.nombre.contains(busqueda, ignoreCase = true) }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
        items(lista) { juego ->
            Card(
                shape = Esquinas,
                colors = CardDefaults.cardColors(containerColor = Panel),
                modifier = Modifier.fillMaxWidth().clickable { vm.elegirJuego(juego) }
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(juego.nombre, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Text("${juego.armas.size} armas", color = Gris)
                }
            }
        }
    }
}

// ---------- PANTALLA 3: ELIGE TU ARMA (02/03) ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColumnScope.PantallaArmas(vm: BuildViewModel) {
    val juego = vm.juego ?: return
    var filtro by remember { mutableStateOf("Todas") }

    Text("02/03", color = Gris)
    Text("Elige tu arma", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    Text(juego.nombre, color = Gris)
    Spacer(Modifier.height(12.dp))

    // Filtro por tipo (Asalto, Pistola, etc.)
    val tipos = listOf("Todas") + juego.armas.map { it.tipo }.distinct()
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.horizontalScroll(rememberScrollState())
    ) {
        tipos.forEach { tipo ->
            FilterChip(
                selected = filtro == tipo,
                onClick = { filtro = tipo },
                label = { Text(tipo) }
            )
        }
    }
    Spacer(Modifier.height(12.dp))

    val lista = if (filtro == "Todas") juego.armas else juego.armas.filter { it.tipo == filtro }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
        items(lista) { arma ->
            Card(
                shape = Esquinas,
                colors = CardDefaults.cardColors(containerColor = Panel),
                modifier = Modifier.fillMaxWidth().clickable { vm.elegirArma(arma) }
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(arma.nombre, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Text(arma.tipo, color = Gris)
                }
            }
        }
    }
    OutlinedButton(
        onClick = { vm.volver() },
        shape = Esquinas,
        modifier = Modifier.fillMaxWidth()
    ) { Text("Volver") }
}

// ---------- PANTALLA 4: CONOCE TU ARMA (03/03) ----------
@Composable
fun ColumnScope.PantallaDetalle(vm: BuildViewModel) {
    val arma = vm.arma ?: return

    Text("03/03", color = Gris)
    Text("Conoce tu ${arma.nombre}", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    Text(arma.tipo, color = Gris)
    Spacer(Modifier.height(16.dp))
    TablaStats(arma.base)
    Spacer(Modifier.weight(1f))
    Button(
        onClick = { vm.irAConfigurar() },
        shape = Esquinas,
        modifier = Modifier.fillMaxWidth().height(52.dp)
    ) { Text("Configurar mejoras") }
    Spacer(Modifier.height(8.dp))
    OutlinedButton(
        onClick = { vm.volver() },
        shape = Esquinas,
        modifier = Modifier.fillMaxWidth()
    ) { Text("Volver") }
}

// ---------- PANTALLA 5: TU ARMA, MEJORADA ----------
@Composable
fun ColumnScope.PantallaBuild(vm: BuildViewModel) {
    val arma = vm.arma ?: return
    val resultado = vm.resultado ?: return

    Text("Tu ${arma.nombre}, mejorada", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    Text("Mejoras: ${vm.mejorasElegidas.size}/${vm.maxMejoras}", color = Gris)
    Spacer(Modifier.height(8.dp))

    LazyColumn(modifier = Modifier.weight(1f)) {
        items(arma.mejoras) { mejora ->
            val puesta = mejora in vm.mejorasElegidas
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().clickable { vm.alternarMejora(mejora) }
            ) {
                Checkbox(
                    checked = puesta,
                    onCheckedChange = { vm.alternarMejora(mejora) }
                )
                Text(mejora.nombre)
            }
        }
        item {
            Spacer(Modifier.height(16.dp))
            Text("Antes vs. ahora", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            TablaStats(arma.base, resultado)
        }
    }

    Spacer(Modifier.height(8.dp))
    Button(
        onClick = { vm.guardarBuild() },
        shape = Esquinas,
        modifier = Modifier.fillMaxWidth().height(52.dp)
    ) { Text("Guardar build") }
    Spacer(Modifier.height(8.dp))
    OutlinedButton(
        onClick = { vm.volver() },
        shape = Esquinas,
        modifier = Modifier.fillMaxWidth()
    ) { Text("Volver") }
}

// ---------- PANTALLA 6: MIS BUILDS ----------
@Composable
fun ColumnScope.PantallaMisBuilds(vm: BuildViewModel) {
    Text("Mis builds", fontSize = 24.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(12.dp))

    if (vm.buildsGuardadas.isEmpty()) {
        Text(
            "Todavía no has guardado ninguna build.",
            color = Gris,
            modifier = Modifier.weight(1f)
        )
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(vm.buildsGuardadas) { build ->
                Card(
                    shape = Esquinas,
                    colors = CardDefaults.cardColors(containerColor = Panel),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text(build.nombre, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Text(build.arma.tipo, color = Gris)
                        Spacer(Modifier.height(4.dp))
                        val texto = if (build.mejoras.isEmpty()) {
                            "ninguna"
                        } else {
                            build.mejoras.joinToString { it.nombre }
                        }
                        Text("Mejoras: $texto")
                    }
                }
            }
        }
    }
}

// ---------- BARRA DE NAVEGACIÓN (Explorar y Mis builds) ----------
@Composable
fun BarraNavegacion(vm: BuildViewModel) {
    val enMisBuilds = vm.pantalla == Pantalla.MIS_BUILDS

    NavigationBar(containerColor = Panel) {
        NavigationBarItem(
            selected = !enMisBuilds,
            onClick = { vm.explorar() },
            icon = { Text("🔍") },
            label = { Text("Explorar") }
        )
        NavigationBarItem(
            selected = enMisBuilds,
            onClick = { vm.verMisBuilds() },
            icon = { Text("⭐") },
            label = { Text("Mis builds") }
        )
    }
}

// ---------- PIEZAS REUTILIZABLES ----------

@Composable
fun TablaStats(base: Estadisticas, final: Estadisticas? = null) {
    FilaStat("Daño", base.dano, final?.dano)
    FilaStat("Cadencia", base.cadencia, final?.cadencia)
    FilaStat("Alcance", base.alcance, final?.alcance)
    FilaStat("Movilidad", base.movilidad, final?.movilidad)
    FilaStat("Control", base.control, final?.control)
}

@Composable
fun FilaStat(nombre: String, antes: Int, ahora: Int? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(nombre)
        if (ahora == null) {
            Text("$antes", fontWeight = FontWeight.SemiBold)
        } else {
            val diferencia = ahora - antes
            val signo = if (diferencia >= 0) "+" else ""
            Text("$antes → $ahora ($signo$diferencia)", fontWeight = FontWeight.SemiBold)
        }
    }
}