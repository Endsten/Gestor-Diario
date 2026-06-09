package com.example.gestordiario.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {
    val allItens: Flow<List<ItemAlmoxarifado>> = appDao.getAllItens()
    val allMotos: Flow<List<Motocicleta>> = appDao.getAllMotos()
    val allUsoItens: Flow<List<UsoItem>> = appDao.getAllUsoItens()
    val allAbastecimentos: Flow<List<Abastecimento>> = appDao.getAllAbastecimentos()
    val allManutencoes: Flow<List<Manutencao>> = appDao.getAllManutencoes()

    suspend fun insertItem(item: ItemAlmoxarifado) = appDao.insertItem(item)
    suspend fun updateItem(item: ItemAlmoxarifado) = appDao.updateItem(item)
    suspend fun deleteItem(item: ItemAlmoxarifado) = appDao.deleteItem(item)
    
    suspend fun insertMoto(moto: Motocicleta) = appDao.insertMoto(moto)
    suspend fun updateMoto(moto: Motocicleta) = appDao.updateMoto(moto)
    suspend fun deleteMoto(moto: Motocicleta) = appDao.deleteMoto(moto)
    
    suspend fun insertAbastecimento(abastecimento: Abastecimento) = appDao.insertAbastecimento(abastecimento)
    suspend fun updateAbastecimento(abastecimento: Abastecimento) = appDao.updateAbastecimento(abastecimento)
    suspend fun deleteAbastecimento(abastecimento: Abastecimento) = appDao.deleteAbastecimento(abastecimento)

    suspend fun insertManutencao(manutencao: Manutencao) = appDao.insertManutencao(manutencao)
    suspend fun updateManutencao(manutencao: Manutencao) = appDao.updateManutencao(manutencao)
    suspend fun deleteManutencao(manutencao: Manutencao) = appDao.deleteManutencao(manutencao)
    
    suspend fun insertUsoItem(usoItem: UsoItem) = appDao.insertUsoItem(usoItem)
    suspend fun updateUsoItem(usoItem: UsoItem) = appDao.updateUsoItem(usoItem)
    suspend fun deleteUsoItem(usoItem: UsoItem) = appDao.deleteUsoItem(usoItem)
    
    fun getAbastecimentos(motoId: Int) = appDao.getAbastecimentosPorMoto(motoId)
    fun getManutencoes(motoId: Int) = appDao.getManutencoesPorMoto(motoId)
}
