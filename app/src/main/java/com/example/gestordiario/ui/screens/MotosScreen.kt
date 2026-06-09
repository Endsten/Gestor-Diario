package com.example.gestordiario.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.gestordiario.data.Motocicleta
import com.example.gestordiario.ui.MainViewModel

@Composable
fun MotosScreen(viewModel: MainViewModel) {
    val motos by viewModel.allMotos.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Motocicleta?>(null) }
    var showDeleteConfirm by remember { mutableStateOf<Motocicleta?>(null) }
    var showActionDialog by remember { mutableStateOf<Pair<Boolean, String>>(false to "") }
    var selectedMoto by remember { mutableStateOf<Motocicleta?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nova Moto")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Gestão de Motos", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(motos) { moto ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${moto.modelo} - ${moto.placa}", style = MaterialTheme.typography.titleMedium)
                                Row {
                                    IconButton(onClick = { showEditDialog = moto }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                                    }
                                    IconButton(onClick = { showDeleteConfirm = moto }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                            
                            Text("KM Atual: ${moto.kmAtual}")
                            
                            Row(modifier = Modifier.padding(top = 8.dp)) {
                                Button(onClick = { 
                                    selectedMoto = moto
                                    showActionDialog = true to "Abastecer"
                                }) { Text("Abastecer") }

                                Spacer(Modifier.width(8.dp))

                                OutlinedButton(onClick = {
                                    selectedMoto = moto
                                    showActionDialog = true to "Manutenção"
                                }) { Text("Manutenção") }
                            }
                        }
                    }
                }
            }
        }

        // Diálogo Adicionar
        if (showAddDialog) {
            AddMotoDialog(onDismiss = { showAddDialog = false }) { p, m, k ->
                viewModel.insertMoto(p, m, k)
                showAddDialog = false
            }
        }

        // Diálogo Editar
        if (showEditDialog != null) {
            EditMotoDialog(
                moto = showEditDialog!!,
                onDismiss = { showEditDialog = null },
                onConfirm = { updatedMoto ->
                    viewModel.updateMoto(updatedMoto)
                    showEditDialog = null
                }
            )
        }

        // Confirmação de Exclusão
        if (showDeleteConfirm != null) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = null },
                title = { Text("Excluir Moto") },
                text = { Text("Tem certeza que deseja excluir a moto ${showDeleteConfirm?.placa}? Isso não apagará os registros de gastos já feitos.") },
                confirmButton = {
                    Button(onClick = {
                        viewModel.excluirMoto(showDeleteConfirm!!)
                        showDeleteConfirm = null
                    }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                        Text("Excluir")
                    }
                },
                dismissButton = { TextButton(onClick = { showDeleteConfirm = null }) { Text("Cancelar") } }
            )
        }

        // Diálogo Ação (Abastecer/Manutenção)
        if (showActionDialog.first && selectedMoto != null) {
            MotoActionDialog(
                type = showActionDialog.second,
                moto = selectedMoto!!,
                onDismiss = { showActionDialog = false to "" }
            ) { val1, val2, val3 ->
                if (showActionDialog.second == "Abastecer") {
                    viewModel.registrarAbastecimento(selectedMoto!!.id, val1, val2, val3)
                } else {
                    viewModel.registrarManutencao(selectedMoto!!.id, "Manutenção: $val1", val2)
                }
                showActionDialog = false to ""
            }
        }
    }
}

@Composable
fun AddMotoDialog(onDismiss: () -> Unit, onConfirm: (String, String, Double) -> Unit) {
    var placa by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var km by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cadastrar Moto") },
        text = {
            Column {
                OutlinedTextField(value = placa, onValueChange = { placa = it }, label = { Text("Placa") })
                OutlinedTextField(value = modelo, onValueChange = { modelo = it }, label = { Text("Modelo") })
                OutlinedTextField(value = km, onValueChange = { km = it }, label = { Text("KM Inicial") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        },
        confirmButton = { Button(onClick = { onConfirm(placa, modelo, km.toDoubleOrNull() ?: 0.0) }) { Text("Salvar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun EditMotoDialog(moto: Motocicleta, onDismiss: () -> Unit, onConfirm: (Motocicleta) -> Unit) {
    var placa by remember { mutableStateOf(moto.placa) }
    var modelo by remember { mutableStateOf(moto.modelo) }
    var km by remember { mutableStateOf(moto.kmAtual.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Moto") },
        text = {
            Column {
                OutlinedTextField(value = placa, onValueChange = { placa = it }, label = { Text("Placa") })
                OutlinedTextField(value = modelo, onValueChange = { modelo = it }, label = { Text("Modelo") })
                OutlinedTextField(value = km, onValueChange = { km = it }, label = { Text("KM") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(moto.copy(placa = placa, modelo = modelo, kmAtual = km.toDoubleOrNull() ?: moto.kmAtual))
            }) { Text("Atualizar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun MotoActionDialog(type: String, moto: Motocicleta, onDismiss: () -> Unit, onConfirm: (Double, Double, Double) -> Unit) {
    var val1 by remember { mutableStateOf("") } 
    var val2 by remember { mutableStateOf("") } 
    var val3 by remember { mutableStateOf(moto.kmAtual.toString()) } 

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("$type - ${moto.placa}") },
        text = {
            Column {
                OutlinedTextField(
                    value = val1,
                    onValueChange = { val1 = it },
                    label = { Text(if (type == "Abastecer") "Litros" else "Descrição") }
                )
                OutlinedTextField(value = val2, onValueChange = { val2 = it }, label = { Text("Valor (R$)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                if (type == "Abastecer") {
                    OutlinedTextField(value = val3, onValueChange = { val3 = it }, label = { Text("KM no Painel") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
            }
        },
        confirmButton = {
            Button(onClick = { 
                onConfirm(val1.toDoubleOrNull() ?: 0.0, val2.toDoubleOrNull() ?: 0.0, val3.toDoubleOrNull() ?: 0.0)
            }) { Text("Confirmar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
