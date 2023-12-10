package com.gym.mytraining.presentation.exercise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.mytraining.domain.model.Exercise
import com.gym.mytraining.domain.model.Training
import com.gym.mytraining.domain.useCase.exercise.ExerciseInteractor
import com.gym.mytraining.domain.useCase.training.TrainingInteractor
import com.gym.mytraining.presentation.viewState.ViewStateExercise
import com.gym.mytraining.presentation.viewState.ViewStateTraining
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ExerciseViewModel(
    private val exerciseInteractor: ExerciseInteractor,
): ViewModel(){

    private var _viewStateExercise = MutableLiveData<ViewStateExercise>()
    var viewStateExercise: LiveData<ViewStateExercise> = _viewStateExercise

    fun getAll(training: Training) {
        viewModelScope.launch {
            exerciseInteractor.getAll(training)
                .onStart { _viewStateExercise.value = ViewStateExercise.Loading(loading = true) }
                .catch {
                    _viewStateExercise.value = ViewStateExercise.Failure(messengerError = it.message.orEmpty())
                }
                .collect { _viewStateExercise.value = ViewStateExercise.SuccessList(it)}
        }
    }

    fun deleteExercise(exercise: Exercise) {
        viewModelScope.launch {
            exerciseInteractor.delete(exercise)
                .onStart { _viewStateExercise.value = ViewStateExercise.Loading(loading = true) }
                .catch {
                    _viewStateExercise.value = ViewStateExercise.Failure(messengerError = it.message.orEmpty())
                }
                .collect { _viewStateExercise.value = ViewStateExercise.Success(it)}
        }
    }

}