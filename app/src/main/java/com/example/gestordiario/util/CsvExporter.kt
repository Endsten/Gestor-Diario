package com.example.gestordiario.util

import com.example.gestordiario.data.*
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

object CsvExporter {
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

    fun exportMotosToCsv(
        outputStream: OutputStream,
        motos: List<Motocicleta>,
        abastecimentos: List<Abastecimento>,
        manutencoes: List<Manutencao>
    ) {
        val writer = outputStream.bufferedWriter()
        
        writer.write("RELATORIO DE MOTOS\n")
        writer.write("Placa;Modelo;Total Combustivel (R$);Total Manutencao (R$);Custo Total (R$)\n")
        
        motos.forEach { moto ->
            val totalAbast = abastecimentos.filter { it.motoId == moto.id }.sumOf { it.valor }
            val totalManut = manutencoes.filter { it.motoId == moto.id }.sumOf { it.valor }
            writer.write("${moto.placa};${moto.modelo};${totalAbast};${totalManut};${totalAbast + totalManut}\n")
        }

        writer.write("\nDETALHES DOS REGISTROS\n")
        writer.write("Data;Tipo;Moto;Descricao/Litros;Valor (R$);KM\n")
        
        abastecimentos.forEach { abast ->
            val moto = motos.find { it.id == abast.motoId }
            writer.write("${dateFormatter.format(Date(abast.data))};Abastecimento;${moto?.placa};${abast.litros}L;${abast.valor};${abast.kmNoAbastecimento}\n")
        }
        
        manutencoes.forEach { manut ->
            val moto = motos.find { it.id == manut.motoId }
            writer.write("${dateFormatter.format(Date(manut.data))};Manutencao;${moto?.placa};${manut.descricao};${manut.valor};-\n")
        }

        writer.flush()
    }

    fun exportAlmoxarifadoToCsv(
        outputStream: OutputStream,
        itens: List<ItemAlmoxarifado>,
        usos: List<UsoItem>
    ) {
        val writer = outputStream.bufferedWriter()
        
        writer.write("RELATORIO DE ALMOXARIFADO\n")
        writer.write("Produto;Quantidade em Estoque;Unidade;Valor Unit;Total em Estoque (R$)\n")
        itens.forEach { item ->
            writer.write("${item.nome};${item.quantidade};${item.unidadeMedida};${item.valor};${item.quantidade * item.valor}\n")
        }

        writer.write("\nHISTORICO DE RETIRADAS\n")
        writer.write("Data;Produto;Quantidade;Responsavel;Custo (R$)\n")
        usos.forEach { uso ->
            val item = itens.find { it.id == uso.itemId }
            val custo = (item?.valor ?: 0.0) * uso.quantidade
            writer.write("${dateFormatter.format(Date(uso.data))};${item?.nome ?: "Desconhecido"};${uso.quantidade};${uso.responsavel};${custo}\n")
        }

        writer.flush()
    }
}
