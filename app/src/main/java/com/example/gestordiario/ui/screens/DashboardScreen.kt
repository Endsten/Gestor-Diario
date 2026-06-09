package com.example.gestordiario.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.gestordiario.data.*
import com.example.gestordiario.ui.MainViewModel
import com.example.gestordiario.util.CsvExporter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    
    val itensUso by viewModel.allUsoItens.collectAsState()
    val itensEstoque by viewModel.allItens.collectAsState()
    val abastecimentos by viewModel.allAbastecimentos.collectAsState()
    val manutencoes by viewModel.allManutencoes.collectAsState()
    val motos by viewModel.allMotos.collectAsState()

    var reportType by remember { mutableIntStateOf(0) }
    var selectedMotoId by remember { mutableStateOf<Int?>(null) }
    
    // Estados para Edição
    var editingAbast by remember { mutableStateOf<Abastecimento?>(null) }
    var editingManut by remember { mutableStateOf<Manutencao?>(null) }
    var editingUso by remember { mutableStateOf<UsoItem?>(null) }
    var editingMoto by remember { mutableStateOf<Motocicleta?>(null) }
    var showMotoDeleteConfirm by remember { mutableStateOf<Motocicleta?>(null) }

    // Filtros de Data
    var daysFilter by remember { mutableIntStateOf(30) }
    var isCustomDate by remember { mutableStateOf(false) }
    var startDateStr by remember { mutableStateOf("") }
    var endDateStr by remember { mutableStateOf("") }

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))

    // Lógica de cálculo do intervalo de datas
    val dateRange = remember(daysFilter, isCustomDate, startDateStr, endDateStr) {
        if (!isCustomDate) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -daysFilter)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.timeInMillis..Long.MAX_VALUE
        } else {
            val start = try { dateFormatter.parse(startDateStr)?.time ?: 0L } catch (e: Exception) { 0L }
            val end = try { 
                dateFormatter.parse(endDateStr)?.let {
                    val cal = Calendar.getInstance()
                    cal.time = it
                    cal.set(Calendar.HOUR_OF_DAY, 23)
                    cal.set(Calendar.MINUTE, 59)
                    cal.set(Calendar.SECOND, 59)
                    cal.timeInMillis
                } ?: Long.MAX_VALUE 
            } catch (e: Exception) { Long.MAX_VALUE }
            start..end
        }
    }

    val filteredAbast = abastecimentos.filter { (selectedMotoId == null || it.motoId == selectedMotoId) && it.data in dateRange }
    val filteredManut = manutencoes.filter { (selectedMotoId == null || it.motoId == selectedMotoId) && it.data in dateRange }
    val filteredUso = itensUso.filter { it.data in dateRange }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv"),
        onResult = { uri: Uri? ->
            uri?.let {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    if (reportType == 0) {
                        val motosExport = if (selectedMotoId == null) motos else motos.filter { it.id == selectedMotoId }
                        CsvExporter.exportMotosToCsv(outputStream, motosExport, filteredAbast, filteredManut)
                    } else {
                        CsvExporter.exportAlmoxarifadoToCsv(outputStream, itensEstoque, filteredUso)
                    }
                }
            }
        }
    )

    // Toda a tela dentro de um LazyColumn para rolagem unificada
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Cabeçalho
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Painel de Controle", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                IconButton(onClick = { 
                    val fileName = if (reportType == 0) "Relatorio_Motos.csv" else "Relatorio_Almoxarifado.csv"
                    exportLauncher.launch(fileName)
                }) {
                    Icon(Icons.Default.Share, contentDescription = "Exportar CSV", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // 2. Tabs
        item {
            TabRow(selectedTabIndex = reportType) {
                Tab(selected = reportType == 0, onClick = { reportType = 0 }) { Text("Motos", modifier = Modifier.padding(16.dp)) }
                Tab(selected = reportType == 1, onClick = { reportType = 1 }) { Text("Almoxarifado", modifier = Modifier.padding(16.dp)) }
            }
        }

        // 3. Gráfico (apenas para motos)
        if (reportType == 0 && selectedMotoId == null && motos.isNotEmpty()) {
            item {
                Text("Gastos por Moto (R$)", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(8.dp))
                MotoCostChart(motos, abastecimentos, manutencoes)
            }
        }

        // 4. Filtros
        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                    Text("Filtros e Período", style = MaterialTheme.typography.titleSmall)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        var dateExpanded by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedButton(onClick = { dateExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                                Text(if(isCustomDate) "Personalizado" else "$daysFilter dias")
                            }
                            DropdownMenu(expanded = dateExpanded, onDismissRequest = { dateExpanded = false }) {
                                listOf(7, 15, 30, 90).forEach { days ->
                                    DropdownMenuItem(text = { Text("Últimos $days dias") }, onClick = { daysFilter = days; isCustomDate = false; dateExpanded = false })
                                }
                                DropdownMenuItem(text = { Text("Personalizado") }, onClick = { isCustomDate = true; dateExpanded = false })
                            }
                        }

                        if (reportType == 0) {
                            var motoExpanded by remember { mutableStateOf(false) }
                            val currentSelectedMoto = motos.find { it.id == selectedMotoId }
                            
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedButton(onClick = { motoExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                                    Text(currentSelectedMoto?.placa ?: "Todas")
                                }
                                DropdownMenu(expanded = motoExpanded, onDismissRequest = { motoExpanded = false }) {
                                    DropdownMenuItem(text = { Text("Todas") }, onClick = { selectedMotoId = null; motoExpanded = false })
                                    motos.forEach { moto ->
                                        DropdownMenuItem(text = { Text(moto.placa) }, onClick = { selectedMotoId = moto.id; motoExpanded = false })
                                    }
                                }
                            }
                            
                            if (currentSelectedMoto != null) {
                                Row {
                                    IconButton(onClick = { editingMoto = currentSelectedMoto }) {
                                        Icon(Icons.Default.Edit, "Editar Moto", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = { showMotoDeleteConfirm = currentSelectedMoto }) {
                                        Icon(Icons.Default.Delete, "Excluir Moto", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }

                    if (isCustomDate) {
                        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = startDateStr,
                                onValueChange = { startDateStr = it },
                                label = { Text("Início (dd/mm/aaaa)") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            OutlinedTextField(
                                value = endDateStr,
                                onValueChange = { endDateStr = it },
                                label = { Text("Fim (dd/mm/aaaa)") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    }
                }
            }
        }

        // 5. Resumo e Itens da Lista
        if (reportType == 0) {
            val total = filteredAbast.sumOf { it.valor } + filteredManut.sumOf { it.valor }
            item {
                SummaryCard(
                    title = "Total de Gastos", 
                    mainValue = currencyFormatter.format(total), 
                    details = listOf(
                        "Combustível: ${currencyFormatter.format(filteredAbast.sumOf { it.valor })}", 
                        "Manutenção: ${currencyFormatter.format(filteredManut.sumOf { it.valor })}"
                    )
                )
            }
            
            items(filteredAbast) { abast -> 
                ReportItem(
                    title = "Abastecimento - ${motos.find { it.id == abast.motoId }?.placa ?: "N/A"}", 
                    subtitle = "${dateFormatter.format(Date(abast.data))} | ${abast.litros}L", 
                    value = currencyFormatter.format(abast.valor), 
                    color = Color(0xFF4CAF50),
                    onDelete = { viewModel.excluirAbastecimento(abast) },
                    onEdit = { editingAbast = abast }
                ) 
            }
            items(filteredManut) { manut -> 
                ReportItem(
                    title = "Manutenção - ${motos.find { it.id == manut.motoId }?.placa ?: "N/A"}", 
                    subtitle = manut.descricao, 
                    value = currencyFormatter.format(manut.valor), 
                    color = Color(0xFFF44336),
                    onDelete = { viewModel.excluirManutencao(manut) },
                    onEdit = { editingManut = manut }
                ) 
            }
        } else {
            val totalAlmox = filteredUso.sumOf { uso -> 
                val item = itensEstoque.find { it.id == uso.itemId }
                (item?.valor ?: 0.0) * uso.quantidade 
            }
            item {
                SummaryCard("Custo de Retiradas", currencyFormatter.format(totalAlmox), listOf("${filteredUso.size} registros no período"))
            }
            
            items(filteredUso) { uso ->
                val item = itensEstoque.find { it.id == uso.itemId }
                ReportItem(
                    title = item?.nome ?: "N/A", 
                    subtitle = "Por: ${uso.responsavel} | Qtd: ${uso.quantidade}", 
                    value = currencyFormatter.format((item?.valor ?: 0.0) * uso.quantidade), 
                    color = MaterialTheme.colorScheme.primary,
                    onDelete = { viewModel.excluirUsoItem(uso) },
                    onEdit = { editingUso = uso }
                )
            }
        }
    }

    // --- DIÁLOGOS DE EDIÇÃO ---
    if (editingAbast != null) {
        EditAbastecimentoDialog(editingAbast!!, onDismiss = { editingAbast = null }, onConfirm = { viewModel.updateAbastecimento(it); editingAbast = null })
    }
    if (editingManut != null) {
        EditManutencaoDialog(editingManut!!, onDismiss = { editingManut = null }, onConfirm = { viewModel.updateManutencao(it); editingManut = null })
    }
    if (editingUso != null) {
        EditUsoDialog(editingUso!!, onDismiss = { editingUso = null }, onConfirm = { viewModel.updateUsoItem(it); editingUso = null })
    }
    if (editingMoto != null) {
        EditMotoDialog(moto = editingMoto!!, onDismiss = { editingMoto = null }, onConfirm = { viewModel.updateMoto(it); editingMoto = null })
    }
    if (showMotoDeleteConfirm != null) {
        AlertDialog(
            onDismissRequest = { showMotoDeleteConfirm = null },
            title = { Text("Excluir Moto") },
            text = { Text("Deseja excluir a moto ${showMotoDeleteConfirm?.placa}?") },
            confirmButton = { Button(onClick = { viewModel.excluirMoto(showMotoDeleteConfirm!!); selectedMotoId = null; showMotoDeleteConfirm = null }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Excluir") } },
            dismissButton = { TextButton(onClick = { showMotoDeleteConfirm = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
fun EditAbastecimentoDialog(abast: Abastecimento, onDismiss: () -> Unit, onConfirm: (Abastecimento) -> Unit) {
    var valor by remember { mutableStateOf(abast.valor.toString()) }
    var litros by remember { mutableStateOf(abast.litros.toString()) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Editar Abastecimento") },
        text = { Column {
            OutlinedTextField(value = valor, onValueChange = { valor = it }, label = { Text("Valor (R$)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            OutlinedTextField(value = litros, onValueChange = { litros = it }, label = { Text("Litros") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
        }},
        confirmButton = { Button(onClick = { onConfirm(abast.copy(valor = valor.toDoubleOrNull() ?: abast.valor, litros = litros.toDoubleOrNull() ?: abast.litros)) }) { Text("Salvar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun EditManutencaoDialog(manut: Manutencao, onDismiss: () -> Unit, onConfirm: (Manutencao) -> Unit) {
    var valor by remember { mutableStateOf(manut.valor.toString()) }
    var desc by remember { mutableStateOf(manut.descricao) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Editar Manutenção") },
        text = { Column {
            OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descrição") })
            OutlinedTextField(value = valor, onValueChange = { valor = it }, label = { Text("Valor (R$)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
        }},
        confirmButton = { Button(onClick = { onConfirm(manut.copy(valor = valor.toDoubleOrNull() ?: manut.valor, descricao = desc)) }) { Text("Salvar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun EditUsoDialog(uso: UsoItem, onDismiss: () -> Unit, onConfirm: (UsoItem) -> Unit) {
    var qtd by remember { mutableStateOf(uso.quantidade.toString()) }
    var resp by remember { mutableStateOf(uso.responsavel) }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Editar Retirada") },
        text = { Column {
            OutlinedTextField(value = qtd, onValueChange = { qtd = it }, label = { Text("Quantidade") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = resp, onValueChange = { resp = it }, label = { Text("Responsável") })
        }},
        confirmButton = { Button(onClick = { onConfirm(uso.copy(quantidade = qtd.toDoubleOrNull() ?: uso.quantidade, responsavel = resp)) }) { Text("Salvar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun MotoCostChart(
    motos: List<Motocicleta>, 
    abastecimentos: List<Abastecimento>, 
    manutencoes: List<Manutencao>
) {
    val costs = motos.map { moto ->
        val total = abastecimentos.filter { it.motoId == moto.id }.sumOf { it.valor } +
                    manutencoes.filter { it.motoId == moto.id }.sumOf { it.valor }
        moto.placa to total.toFloat()
    }.filter { it.second > 0 }

    if (costs.isEmpty()) return

    val maxCost = costs.maxOf { it.second }.coerceAtLeast(1f)
    val barColor = Color(0xFF6200EE)

    Card(modifier = Modifier.fillMaxWidth().height(150.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 24.dp)) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val barWidth = (canvasWidth / (costs.size * 2)).coerceAtLeast(10f)

            costs.forEachIndexed { index, pair ->
                val barHeight = (pair.second / maxCost) * canvasHeight
                val x = (index * 2 * barWidth) + (barWidth / 2)

                drawRect(
                    color = barColor,
                    topLeft = Offset(x, canvasHeight - barHeight),
                    size = Size(barWidth, barHeight)
                )

                drawContext.canvas.nativeCanvas.drawText(
                    pair.first.takeLast(4),
                    x + (barWidth / 2),
                    canvasHeight + 35f,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 30f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }
    }
}

@Composable
fun SummaryCard(title: String, mainValue: String, details: List<String>) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text(mainValue, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            details.forEach { Text(it, style = MaterialTheme.typography.bodyMedium) }
        }
    }
}

@Composable
fun ReportItem(
    title: String, 
    subtitle: String, 
    value: String, 
    color: Color,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), border = CardDefaults.outlinedCardBorder()) {
        ListItem(
            headlineContent = { Text(title, fontWeight = FontWeight.SemiBold) },
            supportingContent = { Text(subtitle) },
            trailingContent = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(value, color = color, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        )
    }
}
