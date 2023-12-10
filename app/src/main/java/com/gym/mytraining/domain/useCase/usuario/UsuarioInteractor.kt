package com.gym.mytraining.domain.useCase.usuario

import com.gym.mytraining.domain.model.Usuario
import kotlinx.coroutines.flow.Flow

interface UsuarioInteractor {
    fun insert(usuario: Usuario): Flow<Usuario>
    fun verificarUsuarioLogado(): Flow<Boolean>

}
class UsuarioInteractorImp(
    private val usuarioInsertUseCase: UsuarioInsertUseCase,
    private val usuarioVerificarUsuarioLogadoUseCase: UsuarioLogadoUseCase
) : UsuarioInteractor {
    override fun insert(usuario: Usuario) = usuarioInsertUseCase.invoke(usuario)
    override fun verificarUsuarioLogado() = usuarioVerificarUsuarioLogadoUseCase.invoke()
}