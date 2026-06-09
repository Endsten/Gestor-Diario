package com.example.gestordiario.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gestordiario.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: AppRepository) : ViewModel() {

    val allItens: StateFlow<List<ItemAlmoxarifado>> = repository.allItens.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allMotos: StateFlow<List<Motocicleta>> = repository.allMotos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allAbastecimentos: StateFlow<List<Abastecimento>> = repository.allAbastecimentos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allManutencoes: StateFlow<List<Manutencao>> = repository.allManutencoes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allUsoItens: StateFlow<List<UsoItem>> = repository.allUsoItens.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun insertItem(nome: String, unidade: String, quantidade: Double, valor: Double) {
        viewModelScope.launch {
            repository.insertItem(ItemAlmoxarifado(nome = nome, unidadeMedida = unidade, quantidade = quantidade, valor = valor))
        }
    }

    fun updateItem(item: ItemAlmoxarifado) {
        viewModelScope.launch {
            repository.updateItem(item)
        }
    }

    fun excluirItem(item: ItemAlmoxarifado) {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }

    fun registrarUsoItem(itemId: Int, quantidadeRetirada: Double, responsavel: String) {
        viewModelScope.launch {
            repository.insertUsoItem(UsoItem(itemId = itemId, data = System.currentTimeMillis(), quantidade = quantidadeRetirada, responsavel = responsavel))
            val item = allItens.value.find { it.id == itemId }
            if (item != null) {
                val novaQuantidade = (item.quantidade - quantidadeRetirada).coerceAtLeast(0.0)
                repository.updateItem(item.copy(quantidade = novaQuantidade))
            }
        }
    }

    fun updateUsoItem(usoItem: UsoItem) {
        viewModelScope.launch {
            repository.updateUsoItem(usoItem)
        }
    }

    fun excluirUsoItem(usoItem: UsoItem) {
        viewModelScope.launch {
            repository.deleteUsoItem(usoItem)
            // Estorna a quantidade para o estoque
            val item = allItens.value.find { it.id == usoItem.itemId }
            if (item != null) {
                val novaQuantidade = item.quantidade + usoItem.quantidade
                repository.updateItem(item.copy(quantidade = novaQuantidade))
            }
        }
    }

    fun insertMoto(placa: String, modelo: String, kmAtual: Double) {
        viewModelScope.launch {
            repository.insertMoto(Motocicleta(placa = placa, modelo = modelo, kmAtual = kmAtual))
        }
    }

    fun updateMoto(moto: Motocicleta) {
        viewModelScope.launch {
            repository.updateMoto(moto)
        }
    }

    fun excluirMoto(moto: Motocicleta) {
        viewModelScope.launch {
            repository.deleteMoto(moto)
        }
    }

    fun registrarAbastecimento(motoId: Int, litros: Double, valor: Double, km: Double) {
        viewModelScope.launch {
            repository.insertAbastecimento(Abastecimento(motoId = motoId, data = System.currentTimeMillis(), litros = litros, valor = valor, kmNoAbastecimento = km))
            val moto = allMotos.value.find { it.id == motoId }
            if (moto != null && km > moto.kmAtual) {
                repository.updateMoto(moto.copy(kmAtual = km))
            }
        }
    }

    fun updateAbastecimento(abastecimento: Abastecimento) {
        viewModelScope.launch {
            repository.updateAbastecimento(abastecimento)
        }
    }

    fun excluirAbastecimento(abastecimento: Abastecimento) {
        viewModelScope.launch {
            repository.deleteAbastecimento(abastecimento)
        }
    }

    fun registrarManutencao(motoId: Int, descricao: String, valor: Double) {
        viewModelScope.launch {
            repository.insertManutencao(Manutencao(motoId = motoId, data = System.currentTimeMillis(), descricao = descricao, valor = valor))
        }
    }

    fun updateManutencao(manutencao: Manutencao) {
        viewModelScope.launch {
            repository.updateManutencao(manutencao)
        }
    }

    fun excluirManutencao(manutencao: Manutencao) {
        viewModelScope.launch {
            repository.deleteManutencao(manutencao)
        }
    }

    suspend fun processAiEntry(userMsg: String): String {
        delay(1000)
        return "IA: Entendi sua mensagem - \"$userMsg\""
    }
}

class MainViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
