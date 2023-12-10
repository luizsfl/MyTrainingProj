package com.gym.mytraining.domain.useCase.exercise

import com.gym.mytraining.domain.model.Exercise
import com.gym.mytraining.domain.model.Training
import kotlinx.coroutines.flow.Flow

interface ExerciseInteractor {
    fun getAll(training: Training): Flow<List<Exercise>>
}
class ExerciseInteractorImp(
    private val exerciseGetAllUseCase: ExerciseGetAllUseCase,
) : ExerciseInteractor {
    override fun getAll(training:Training)= exerciseGetAllUseCase.invoke(training)
}