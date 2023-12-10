package com.gym.mytraining.domain.Mapper

import com.gym.mytraining.data.model.UsuarioResponse
import com.gym.mytraining.domain.model.Usuario

fun UsuarioResponse.toUsuario() =
    Usuario(
        idUsuario = this.idUsuario,
        nome = this.nome,
        email = this.email,
        senha = this.senha,
    )