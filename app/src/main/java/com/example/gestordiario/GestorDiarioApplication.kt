package com.example.gestordiario

import android.app.Application
import com.example.gestordiario.data.AppDatabase
import com.example.gestordiario.data.AppRepository

class GestorDiarioApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { AppRepository(database.appDao()) }
}
