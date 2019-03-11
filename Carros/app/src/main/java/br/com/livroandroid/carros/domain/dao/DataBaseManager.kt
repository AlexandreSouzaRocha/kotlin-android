package br.com.livroandroid.carros.domain.dao

import android.arch.persistence.room.Room
import br.com.livroandroid.carros.CarrosApplication

object DataBaseManager {
    // Singleton do Room: banco de dados
    private var dbIbstance: CarrosDataBase
    init {
        val appContext = CarrosApplication.getInstance().applicationContext
        // Configura o Room
        dbIbstance = Room.databaseBuilder(
                appContext,
                CarrosDataBase:: class.java,
                "carros.sqlite")
                .build()
    }
    fun getCarroDAO(): CarroDAO{
        return dbIbstance.carroDAO()
    }
}