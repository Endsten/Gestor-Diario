package com.example.gestordiario.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "itens_almoxarifado")
data class ItemAlmoxarifado(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val unidadeMedida: String,
    val quantidade: Double,
    val valor: Double
)

@Entity(tableName = "motocicletas")
data class Motocicleta(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val placa: String,
    val modelo: String,
    val kmAtual: Double
)

@Entity(tableName = "abastecimentos")
data class Abastecimento(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val motoId: Int,
    val data: Long,
    val litros: Double,
    val valor: Double,
    val kmNoAbastecimento: Double
)

@Entity(tableName = "manutencoes")
data class Manutencao(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val motoId: Int,
    val data: Long,
    val descricao: String,
    val valor: Double
)

@Entity(tableName = "uso_itens")
data class UsoItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemId: Int,
    val data: Long,
    val quantidade: Double,
    val responsavel: String
)
