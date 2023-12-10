package com.gym.mytraining.presentation.newTraning

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.mytraining.domain.model.Exercise
import com.gym.mytraining.domain.model.Training
import com.gym.mytraining.domain.useCase.training.TrainingInteractor
import com.gym.mytraining.presentation.viewState.ViewStateTraining
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class NewTrainingViewModel(
    private val trainingInteractor: TrainingInteractor,
): ViewModel(){

    private var _viewStateTraining = MutableLiveData<ViewStateTraining>()
    var viewStateTraining: LiveData<ViewStateTraining> = _viewStateTraining

    fun insert(training: Training,listExercise:List<Exercise>) {
        viewModelScope.launch {
            trainingInteractor.insert(training,listExercise)
                .onStart { _viewStateTraining.value = ViewStateTraining.Loading(loading = true) }
                .catch {
                    _viewStateTraining.value = ViewStateTraining.Failure(messengerError = it.message.orEmpty())
                }
                .collect { setLogado(it) }
        }
    }

    private fun setLogado(idTraining:String) {
        _viewStateTraining.value = ViewStateTraining.Sucess(idTraining)
    }
}