package com.gym.mytraining.presentation.viewState

import com.gym.mytraining.domain.model.Exercise
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
    data class Success(val idTraining: String):ViewStateTraining()
    data class SucessList(val list:List<Training>):ViewStateTraining()
    data class Failure(val messengerError:String = String()): ViewStateTraining()
}

sealed class ViewStateExercise{
    data class Loading(val loading: Boolean):ViewStateExercise()
    data class Success(val exercise: Exercise):ViewStateExercise()
    data class SuccessList(val list:List<Exercise>):ViewStateExercise()
    data class Failure(val messengerError:String = String()): ViewStateExercise()
}