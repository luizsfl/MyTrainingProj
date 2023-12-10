package com.gym.mytraining.presentation.viewState

import androidx.constraintlayout.motion.utils.ViewState
import com.gym.mytraining.domain.model.Training
import com.gym.mytraining.domain.model.Usuario

sealed class ViewStateUsuario {
    data class Loading(val loading: Boolean):ViewStateUsuario()
    data class SucessoUsuario(val usuario: Usuario):ViewStateUsuario()
    data class Failure(val messengerError:String = String()): ViewStateUsuario()
    data class Logado(val usuarioLogado:Boolean = false): ViewStateUsuario()
}

sealed class ViewStateTraining {
    data class Loading(val loading: Boolean):ViewStateTraining()
    data class Sucess(val idTraining: String):ViewStateTraining()
    data class SucessList(val list:List<Training>):ViewStateTraining()
    data class Failure(val messengerError:String = String()): ViewStateTraining()
}
