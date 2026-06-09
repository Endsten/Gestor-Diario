package com.example.gestordiario.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.gestordiario.data.ItemAlmoxarifado
import com.example.gestordiario.ui.MainViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AlmoxarifadoScreen(viewModel: MainViewModel) {
    val itens by viewModel.allItens.collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    var showRetiradaDialog by remember { mutableStateOf<ItemAlmoxarifado?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Novo Item")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Estoque", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(itens) { item ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.nome, style = MaterialTheme.typography.titleMedium)
                                Text("Qtd: ${item.quantidade} ${item.unidadeMedida}")
                                Text("Valor Unit: ${NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(item.valor)}")
                            }
                            
                            IconButton(onClick = { viewModel.excluirItem(item) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error)
                            }

                            Button(onClick = { showRetiradaDialog = item }) {
                                Icon(Icons.Default.ExitToApp, contentDescription = null)
                                Text("Retirar")
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddItemDialog(onDismiss = { showAddDialog = false }) { n, u, q, v ->
                viewModel.insertItem(n, u, q, v)
                showAddDialog = false
            }
        }

        if (showRetiradaDialog != null) {
            RetiradaDialog(
                item = showRetiradaDialog!!,
                onDismiss = { showRetiradaDialog = null }
            ) { q, r ->
                viewModel.registrarUsoItem(showRetiradaDialog!!.id, q, r)
                showRetiradaDialog = null
            }
        }
    }
}

@Composable
fun AddItemDialog(onDismiss: () -> Unit, onConfirm: (String, String, Double, Double) -> Unit) {
    var nome by remember { mutableStateOf("") }
    var unidade by remember { mutableStateOf("") }
    var qtd by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cadastrar Produto") },
        text = {
            Column {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome") }
                )

                OutlinedTextField(
                    value = unidade,
                    onValueChange = { unidade = it },
                    label = { Text("Unidade (Ex: UN, L)") }
                )

                OutlinedTextField(
                    value = qtd,
                    onValueChange = { qtd = it },
                    label = { Text("Qtd Inicial") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = valor,
                    onValueChange = { valor = it },
                    label = { Text("Valor Custo") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        nome,
                        unidade,
                        qtd.toDoubleOrNull() ?: 0.0,
                        valor.toDoubleOrNull() ?: 0.0
                    )
                }
            ) {
                Text("Salvar")
            }
        }
    )
}

@Composable
fun RetiradaDialog(item: ItemAlmoxarifado, onDismiss: () -> Unit, onConfirm: (Double, String) -> Unit) {
    var qtd by remember { mutableStateOf("") }
    var resp by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Retirada: ${item.nome}") },
        text = {
            Column {
                OutlinedTextField(
                    value = qtd,
                    onValueChange = { qtd = it },
                    label = { Text("Quantidade") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = resp,
                    onValueChange = { resp = it },
                    label = { Text("Quem retirou?") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(qtd.toDoubleOrNull() ?: 0.0, resp)
                }
            ) {
                Text("Confirmar")
            }
        }
    )
}
