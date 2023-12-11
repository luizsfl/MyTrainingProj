package com.gym.mytraining.domain.useCase.usuario

import com.gym.mytraining.data.repository.UsuarioRepository
import com.gym.mytraining.domain.model.Usuario
import com.gym.mytraining.presentation.viewState.ViewStateUsuario
import kotlinx.coroutines.flow.Flow

class UsuarioInsertUseCase(
    private val usuarioRepository: UsuarioRepository
) {
    operator fun invoke(usuario: Usuario): Flow<ViewStateUsuario> =
        usuarioRepository.addUsuario(usuario)
}