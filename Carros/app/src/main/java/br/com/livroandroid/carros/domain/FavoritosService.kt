package br.com.livroandroid.carros.domain

import br.com.livroandroid.carros.domain.dao.DataBaseManager

object FavoritosService {
    // Retrina todos os carros favoritados
    fun getCarros(): List<Carro> {
        val dao = DataBaseManager.getCarroDAO()
        val carros = dao.findAll()

        return carros
    }

    // Verifica se um carro está favoritado
    fun isFavorito(carro: Carro): Boolean {
        val dao = DataBaseManager.getCarroDAO()
        val exists = dao.getById(carro.id) != null

        return exists
    }

    // Salva o Carro ou deleta
    fun favoritar(carro: Carro): Boolean {
        val dao = DataBaseManager.getCarroDAO()
        val favorito = isFavorito(carro)
        if (favorito){
            // Remove dos favoritos
            dao.delete(carro)
            return false
        }
        // adiciona nos favoritos
        dao.insert(carro)
        return true
    }
}