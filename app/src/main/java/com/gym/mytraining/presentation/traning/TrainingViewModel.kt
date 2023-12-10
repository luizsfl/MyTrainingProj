package com.gym.mytraining.presentation.traning

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.mytraining.domain.model.Training
import com.gym.mytraining.domain.useCase.training.TrainingInteractor
import com.gym.mytraining.presentation.viewState.ViewStateTraining
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class TrainingViewModel(
    private val trainingInteractor: TrainingInteractor,
): ViewModel(){

    private var _viewStateTraining = MutableLiveData<ViewStateTraining>()
    var viewStateTraining: LiveData<ViewStateTraining> = _viewStateTraining

//    fun insert(training: Training) {
//        viewModelScope.launch {
//            trainingInteractor.insert(training)
//                .onStart { _viewStateTraining.value = ViewStateTraining.Loading(loading = true) }
//                .catch {
//                    _viewStateTraining.value = ViewStateTraining.Failure(messengerError = it.message.orEmpty())
//                }
//                .collect { setLogado(it) }
//        }
//    }


    fun getAll() {
        viewModelScope.launch {
            trainingInteractor.getAll()
                .onStart { _viewStateTraining.value = ViewStateTraining.Loading(loading = true) }
                .catch {
                    _viewStateTraining.value = ViewStateTraining.Failure(messengerError = it.message.orEmpty())
                }
                .collect { _viewStateTraining.value = ViewStateTraining.SucessList(it)}
        }
    }

    fun deleteTraining(item:Training) {
        viewModelScope.launch {
            trainingInteractor.delete(item)
                .onStart { _viewStateTraining.value = ViewStateTraining.Loading(loading = true) }
                .catch {
                    _viewStateTraining.value = ViewStateTraining.Failure(messengerError = it.message.orEmpty())
                }
                .collect { _viewStateTraining.value = ViewStateTraining.Success(item.idTraining)}
        }
    }

}