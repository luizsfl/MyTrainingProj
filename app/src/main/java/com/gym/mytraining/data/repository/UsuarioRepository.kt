package com.gym.mytraining.data.repository

import com.gym.mytraining.data.dataSource.UsuarioDataSource
import com.gym.mytraining.domain.model.Usuario
import com.gym.mytraining.presentation.viewState.ViewStateUsuario
import kotlinx.coroutines.flow.Flow

interface UsuarioRepository {
    fun addUsuario(usuario: Usuario): Flow<ViewStateUsuario>
    fun verificarUsuarioLogado(): Flow<Boolean>

}

class UsuarioRepositoryImp(
    private val usuarioDataSource: UsuarioDataSource
):UsuarioRepository{
    override fun addUsuario(usuario: Usuario) = usuarioDataSource.addUsuario(usuario)

    override fun verificarUsuarioLogado() = usuarioDataSource.verificarUserLogado()
}