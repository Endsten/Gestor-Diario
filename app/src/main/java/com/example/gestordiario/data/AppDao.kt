package com.example.gestordiario.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Almoxarifado
    @Query("SELECT * FROM itens_almoxarifado ORDER BY nome ASC")
    fun getAllItens(): Flow<List<ItemAlmoxarifado>>

    @Insert
    suspend fun insertItem(item: ItemAlmoxarifado)

    @Update
    suspend fun updateItem(item: ItemAlmoxarifado)

    @Delete
    suspend fun deleteItem(item: ItemAlmoxarifado)

    // Motocicletas
    @Query("SELECT * FROM motocicletas ORDER BY placa ASC")
    fun getAllMotos(): Flow<List<Motocicleta>>

    @Insert
    suspend fun insertMoto(moto: Motocicleta)

    @Update
    suspend fun updateMoto(moto: Motocicleta)

    @Delete
    suspend fun deleteMoto(moto: Motocicleta)

    // Registros de Custo (Abastecimento e Manutenção)
    @Query("SELECT * FROM abastecimentos ORDER BY data DESC")
    fun getAllAbastecimentos(): Flow<List<Abastecimento>>

    @Query("SELECT * FROM abastecimentos WHERE motoId = :motoId ORDER BY data DESC")
    fun getAbastecimentosPorMoto(motoId: Int): Flow<List<Abastecimento>>

    @Insert
    suspend fun insertAbastecimento(abastecimento: Abastecimento)

    @Update
    suspend fun updateAbastecimento(abastecimento: Abastecimento)

    @Delete
    suspend fun deleteAbastecimento(abastecimento: Abastecimento)

    @Query("SELECT * FROM manutencoes ORDER BY data DESC")
    fun getAllManutencoes(): Flow<List<Manutencao>>

    @Query("SELECT * FROM manutencoes WHERE motoId = :motoId ORDER BY data DESC")
    fun getManutencoesPorMoto(motoId: Int): Flow<List<Manutencao>>

    @Insert
    suspend fun insertManutencao(manutencao: Manutencao)

    @Update
    suspend fun updateManutencao(manutencao: Manutencao)

    @Delete
    suspend fun deleteManutencao(manutencao: Manutencao)

    // Uso de Itens do Almoxarifado
    @Query("SELECT * FROM uso_itens ORDER BY data DESC")
    fun getAllUsoItens(): Flow<List<UsoItem>>

    @Insert
    suspend fun insertUsoItem(usoItem: UsoItem)

    @Update
    suspend fun updateUsoItem(usoItem: UsoItem)

    @Delete
    suspend fun deleteUsoItem(usoItem: UsoItem)
}
