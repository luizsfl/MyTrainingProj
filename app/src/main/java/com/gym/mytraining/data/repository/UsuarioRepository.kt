package com.gym.mytraining.data.repository

import com.gym.mytraining.data.dataSource.UsuarioDataSource
import com.gym.mytraining.domain.model.Usuario
import kotlinx.coroutines.flow.Flow

interface UsuarioRepository {
    fun addUsuario(usuario: Usuario): Flow<Usuario>
    fun verificarUsuarioLogado(): Flow<Boolean>

}

class UsuarioRepositoryImp(
    private val usuarioDataSource: UsuarioDataSource
):UsuarioRepository{
    override fun addUsuario(usuario: Usuario): Flow<Usuario> = usuarioDataSource.addUsuario(usuario)

    override fun verificarUsuarioLogado() = usuarioDataSource.verificarUserLogado()
}