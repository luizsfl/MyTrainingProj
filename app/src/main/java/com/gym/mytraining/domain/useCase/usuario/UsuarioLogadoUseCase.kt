package com.gym.mytraining.domain.useCase.usuario

import com.gym.mytraining.data.repository.UsuarioRepository

class UsuarioLogadoUseCase(
    private val usuarioRepository: UsuarioRepository
) {
    operator fun invoke() = usuarioRepository.verificarUsuarioLogado()
}