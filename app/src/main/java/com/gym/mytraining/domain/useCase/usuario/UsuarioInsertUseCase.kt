package com.gym.mytraining.domain.useCase.usuario

import com.gym.mytraining.data.repository.UsuarioRepository
import com.gym.mytraining.domain.model.Usuario

class UsuarioInsertUseCase(
    private val usuarioRepository: UsuarioRepository
) {
    operator fun invoke(usuario: Usuario) =
        usuarioRepository.addUsuario(usuario)
}